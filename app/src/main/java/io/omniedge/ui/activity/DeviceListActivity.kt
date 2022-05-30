package io.omniedge.ui.activity

import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import android.os.SystemClock
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.os.TraceCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import io.omniedge.*
import io.omniedge.n2n.N2NService
import io.omniedge.n2n.model.EdgeStatus
import io.omniedge.n2n.model.N2NSettingInfo
import io.omniedge.ui.fragment.DeviceListFragment
import io.omniedge.viewmodel.DeviceListVm
import kotlinx.android.synthetic.main.activity_device_list.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class DeviceListActivity : BaseActivity(), PopupMenu.OnMenuItemClickListener {

    companion object {
        const val TAG = "DeviceList"
        private const val REQUEST_CODE_VPN = 2
        private const val FRAGMENT_TAG_DEVICE_LIST = "fragment_tag_device_list"

        init {
            initValues()
        }

        private fun initValues() {
            val start = SystemClock.elapsedRealtime()
            ThreadUtils.run {
                OmniLog.d("deviceNameVal=$deviceNameVal")
                OmniLog.d("instanceIdVal=$instanceIdVal")
                OmniLog.d("macAddressVal=$macAddressVal")
                OmniLog.d("initValues end. take ${SystemClock.elapsedRealtime() - start}ms")
            }
        }
    }

    override fun showBack(): Boolean {
        return false
    }

    override fun getLayoutRes(): Int {
        return R.layout.activity_device_list
    }

    override fun getPageTitle(): Int {
        return R.string.device_list
    }

    private fun startService() {
        TraceCompat.beginSection("DeviceList#startService")
        stopService()
        val intent = VpnService.prepare(this)
        if (intent != null) {
            startActivityForResult(intent, REQUEST_CODE_VPN)
        } else {
            onActivityResult(REQUEST_CODE_VPN, RESULT_OK, null)
        }
        TraceCompat.endSection()
    }

    private fun stopService() {
        val instance: N2NService? = N2NService.INSTANCE
        val status = instance?.currentStatus ?: EdgeStatus.RunningStatus.DISCONNECT
        if (instance != null
            && status != EdgeStatus.RunningStatus.DISCONNECT
            && status != EdgeStatus.RunningStatus.FAILED
        ) { /* Asynchronous call */
            instance.stop(null)
        }
    }

    override fun init() {
        super.init()
        TraceCompat.beginSection("DeviceList#init")

        findViewById<TextView>(R.id.tv_device_name).text = deviceNameVal
        findViewById<ImageView>(R.id.iv_profile).setOnClickListener { showPopup(it) }
        val transaction = supportFragmentManager.beginTransaction()
        if (supportFragmentManager.findFragmentByTag(FRAGMENT_TAG_DEVICE_LIST) == null) {
            transaction.add(R.id.container, DeviceListFragment(), FRAGMENT_TAG_DEVICE_LIST)
        }
        switch_vpn.setOnClickListener {
            if (switch_vpn.isChecked) {
                startService()
            } else {
                stopService()
            }
        }

        DeviceListVm.loading().observe(this) { loading: Boolean? ->
            if (loading == true) {
                LoadingDialogUtils.showLoading(this, false)
            } else {
                LoadingDialogUtils.hideLoading()
            }
        }
        DeviceListVm.connectStatus().observe(this) { connected: Boolean? ->
            switch_vpn.isChecked = connected == true
            btn_ping.isEnabled = connected == true
            btn_ping.isFocusable = connected == true
        }
        DeviceListVm.joinedNetwork().observe(this) {
            it?.apply { DeviceListVm.listNetworks() }

            // restart to another network from previous one
            if (DeviceListVm.connectStatus().value == true) {
                lifecycleScope.launch {
                    stopService()
                    delay(500)
                    startService()
                }
            }
        }
        DeviceListVm.currentNetwork().observe(this) {
            switch_vpn.isEnabled = it != null

            it.apply {
                tv_ip.text = this?.second?.virtualIp ?: ""
            }
        }
        DeviceListVm.toast().observe(this) {
            if (!it.isNullOrBlank()) {
                showToast(it)
            }
        }
        DeviceListVm.logout().observe(this) {
            logout()
        }
        DeviceListVm.registerDevice()
        transaction.commitAllowingStateLoss()
        TraceCompat.endSection()
    }

    private fun logout() {
        val signOutBlock = {
            stopService()
            DeviceListVm.clearNetwork()
            App.repository.updateToken(null)
            finish()
            startActivity(Intent(this, LoginActivity::class.java))

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.CLIENT_ID)
                .requestEmail()
                .build()
            val client = GoogleSignIn.getClient(this, gso)
            client.signOut()
        }

        signOutBlock()
    }

    private fun showPopup(v: View) {
        PopupMenu(this, v).apply {
            setOnMenuItemClickListener(this@DeviceListActivity)
            inflate(R.menu.actions)
            setFinishOnTouchOutside(true)
            show()
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                logout()
                true
            }
            R.id.about -> {
                startActivity(Intent(this@DeviceListActivity, HelpActivity::class.java))
                true
            }
            else -> false
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val home = Intent(Intent.ACTION_MAIN)
            home.addCategory(Intent.CATEGORY_HOME)
            startActivity(home)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_VPN) {
            when (resultCode) {
                RESULT_OK -> {
                    DeviceListVm.joinedNetwork().value?.let {
                        val intent = Intent(this, N2NService::class.java)
                        val bundle = Bundle()
                        val n2NSettingModel =
                            it.toN2NSettingModel(1, deviceNameVal, macAddressVal)
                        val n2NSettingInfo = N2NSettingInfo(n2NSettingModel)
                        bundle.putParcelable("n2nSettingInfo", n2NSettingInfo)
                        intent.putExtra("Setting", bundle)
                    }?.also {
                        startService(it)
                    }
                }
                else -> {
                    DeviceListVm.connectStatus().postValue(false)
                }
            }
        }
    }
}


private val deviceNameVal = App.repository.getDeviceName()

private val instanceIdVal: String by lazy {
    var result: String = App.sp.getString("instance_id", "") ?: ""
    if (result.isEmpty()) {
        OmniLog.d("emptyInstanceId")
        result = UUID.randomUUID().toString().replace("-", "")
        App.sp.edit().putString("instance_id", result).apply()
    }
    result
}

private val macAddressVal: String by lazy {
    var result: String = App.sp.getString("mac_address", "") ?: ""
    if (result.isEmpty()) {
        val macAddress = SensitiveUtils.getMacAddress(App.instance)
        if (macAddress.isNullOrBlank()) {
            result = SensitiveUtils.randomMACAddress()
        } else {
            result = macAddress
            App.sp.edit().putString("mac_address", result).apply()
        }
    }
    result
}