package io.omniedge

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Rect
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.os.TraceCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import io.omniedge.data.DataRepository
import io.omniedge.n2n.N2NService
import io.omniedge.n2n.event.StartEvent
import io.omniedge.n2n.event.StopEvent
import io.omniedge.n2n.event.SupernodeDisconnectEvent
import io.omniedge.n2n.model.EdgeStatus
import io.omniedge.n2n.model.N2NSettingInfo
import io.omniedge.ui.activity.BaseActivity
import io.omniedge.ui.activity.HelpActivity
import io.omniedge.ui.activity.SignInActivity
import io.omniedge.ui.fragment.BaseFragment
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_device_list.*
import kotlinx.android.synthetic.main.fragment_device_list.*
import org.json.JSONObject
import java.util.*

/**
 * Created by cypdev@outlook.com
 * <br/> Date: 12/12/20
 * <br/> 技术文档：
 * <br/> 一句话描述：Demo，显示设备列表
 */
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

        DeviceListVm.loading().observe(this, Observer { loading: Boolean? ->
            if (loading == true) {
                LoadingDialogUtils.showLoading(this, false)
            } else {
                LoadingDialogUtils.hideLoading()
            }
            btn_retry.visibility = if (loading == true) View.GONE else View.VISIBLE
        })
        DeviceListVm.connectStatus()
            .observe(this, Observer { connected: Boolean? ->
                switch_vpn.isChecked = connected == true
                btn_ping.isEnabled = connected == true
                btn_ping.isFocusable = connected == true
            })
        DeviceListVm.deviceList().observe(this, Observer { deviceListData: DeviceListData? ->
            switch_vpn.isEnabled = deviceListData != null
        })
        DeviceListVm.deviceList().observe(this, Observer { deviceListData: DeviceListData? ->
            val validVirtualIpOrNull: String? = deviceListData?.joinVirtualNetworkResponse
                ?.virtualIP?.takeIf { ip -> ip.isNotBlank() }
            val setVirtualIpSuccess = validVirtualIpOrNull?.let { tv_ip.text = it; true } ?: false
            btn_retry.visibility = if (setVirtualIpSuccess) View.GONE else View.VISIBLE
        })
        btn_retry.setOnClickListener { DeviceListVm.joinNetwork() }
        btn_retry.performClick()
        transaction.commitAllowingStateLoss()
        TraceCompat.endSection()
    }

    private fun logout() {
        val signOutBlock = {
            stopService()
            DeviceListVm.clearDeviceListData()
            finish()
            startActivity(Intent(this, SignInActivity::class.java))
        }
        DataRepository.getInstance(this)
            .amplifySignOut()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BusObserver<Boolean>(this) {
                override fun loading(): Boolean {
                    return true
                }

                override fun onSuccess(t: Boolean) {
                    super.onSuccess(t)
                    Log.d(TAG, "signOut: success")
                    signOutBlock()
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    Log.e(TAG, "signOut: error ", e)
                    signOutBlock()
                }
            })
    }

    private fun showPopup(v: View) {
        PopupMenu(this, v).apply {
            setOnMenuItemClickListener(this@DeviceListActivity)
            inflate(R.menu.actions)
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
        if (requestCode == REQUEST_CODE_VPN && resultCode == RESULT_OK) {
            DeviceListVm.deviceList().value?.let { deviceListData: DeviceListData ->
                val intent = Intent(this, N2NService::class.java)
                val bundle = Bundle()
                val n2NSettingModel = deviceListData.toN2NSettingModel(
                    1, deviceNameVal, macAddressVal)
                val n2NSettingInfo = N2NSettingInfo(n2NSettingModel)
                bundle.putParcelable("n2nSettingInfo", n2NSettingInfo)
                intent.putExtra("Setting", bundle)
            }?.also {
                startService(it)
            }
        }
    }
}

class DeviceListFragment : BaseFragment() {
    override fun getLayoutRes(): Int {
        return R.layout.fragment_device_list
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        TraceCompat.beginSection("DeviceList#onViewCreated")
        refresh.setOnRefreshListener {
            refresh.postDelayed({ refresh.isRefreshing = false }, 500)
        }
        refresh.isEnabled = false
        val deviceAdapter = DeviceAdapter()
        listview.adapter = deviceAdapter
        val decor = object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect, view: View, parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.bottom = 5
            }
        }
        listview.addItemDecoration(decor)
        val list = mutableListOf<Device>()
        val fragmentActivity = activity as FragmentActivity
        fragmentActivity.findViewById<View>(R.id.btn_ping)
            .setOnClickListener { deviceAdapter.updatePing() }

        DeviceListVm.deviceList()
            .observe(fragmentActivity, Observer { deviceListData: DeviceListData? ->
                val communityName = deviceListData?.joinVirtualNetworkResponse?.communityName
                if (!TextUtils.isEmpty(communityName)) {
                    tv_team.setOnClickListener {
                        tv_team.text = communityName
                        tv_team.setOnClickListener(null)
                    }
                }
                list.clear()
                val virtualIP = deviceListData?.joinVirtualNetworkResponse?.virtualIP ?: ""
                deviceListData?.deviceList?.forEach { device ->
                    device?.let {
                        if (virtualIP == it.virtualIp) {
                            Device(
                                App.instance.getString(R.string.this_device, it.virtualIp),
                                it.virtualIp
                            )
                        } else {
                            Device(it.name, it.virtualIp)
                        }
                    }?.apply {
                        list.add(this)
                    }
                }
                deviceAdapter.setData(list)
            })
        TraceCompat.endSection()
    }
}

data class Device(val name: String, val privateIp: String?, var ping: String? = "")

class DeviceVh(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tvName: TextView = itemView.findViewById(R.id.tv_name)
    val tvIp: TextView = itemView.findViewById(R.id.tv_ip)
    val tvPing: TextView = itemView.findViewById(R.id.tv_ping)
}

class DeviceAdapter : RecyclerView.Adapter<DeviceVh>() {
    private val data = mutableListOf<Device>()

    fun setData(boxes: List<Device>) {
        data.clear()
        data.addAll(boxes)
        notifyDataSetChanged()
    }

    fun updatePing() {
        for ((index, bean) in data.withIndex()) {
            val privateIp = bean.privateIp ?: return
            object : Thread("updatePing-$index") {
                override fun run() {
                    super.run()
                    val ping: String = try {
                        ping(privateIp)
                    } catch (e: Throwable) { // 跨进程调用
                        ""
                    }
                    if (ping.isNotEmpty() && "?ms" != ping) {
                        bean.ping = ping
                        ThreadUtils.runOnMainThread { this@DeviceAdapter.notifyItemChanged(index) }
                    }
                }
            }.start()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceVh {
        TraceCompat.beginSection("DeviceAdapter#onCreateViewHolder")
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_device_list, parent, false)
        val deviceVh = DeviceVh(itemView)
        TraceCompat.endSection()
        return deviceVh
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DeviceVh, position: Int) {
        TraceCompat.beginSection("DeviceAdapter#onBindViewHolder")
        val bean = data[position]
//        OmniLog.d("DeviceAdapter#onBindViewHolder bean=$bean")
        val context = holder.itemView.context
        holder.tvName.text = bean.name
        val ipString: CharSequence = bean.privateIp ?: context.getString(R.string.unknownIP)
        holder.tvIp.text = ipString
        holder.tvPing.text = bean.ping.takeIf { it?.isNotEmpty() == true } ?: ""

//        holder.itemView.setOnClickListener { updatePing(bean, this) }
        TraceCompat.endSection()
    }
}


object DeviceListVm {
    @SuppressLint("StaticFieldLeak")
    private val dataRepository = DataRepository.getInstance(App.instance)
    private val compositeDisposable = CompositeDisposable()
    private val loadingLv: MutableLiveData<Boolean> = MutableLiveData()
    private val lvConnectStatus: MutableLiveData<Boolean> = MutableLiveData()
    private val deviceListDataLv: MutableLiveData<DeviceListData> = MutableLiveData()

    private val observer = object : SingleObserver<DeviceListData> {
        override fun onSubscribe(d: Disposable) {
            loadingLv.postValue(true)
        }

        override fun onSuccess(t: DeviceListData) {
            loadingLv.postValue(false)
            deviceListDataLv.postValue(t)
        }

        override fun onError(e: Throwable) {
            loadingLv.postValue(false)
            deviceListDataLv.postValue(null)
            e.printStackTrace()
        }
    }

    init {
        compositeDisposable.add(RxBus.INSTANCE.toObservable(StartEvent::class.java)
            .subscribe { lvConnectStatus.postValue(true) })
        compositeDisposable.add(RxBus.INSTANCE.toObservable(StopEvent::class.java)
            .subscribe { lvConnectStatus.postValue(false) })
        compositeDisposable.add(RxBus.INSTANCE.toObservable(SupernodeDisconnectEvent::class.java)
            .subscribe { lvConnectStatus.postValue(false) })
    }

    fun clearDeviceListData() {
        deviceListDataLv.postValue(null)
    }

    fun joinNetwork() {
        if (deviceListDataLv.value != null) {
            return
        }
        val body = JSONObject().apply {
            put("instanceID", instanceIdVal)
            put("name", deviceNameVal)
            put("userAgent", userAgent)
            put("description", "${Build.BRAND} ${Build.MODEL} OS:${Build.VERSION.RELEASE}")
            put("publicKey", "asdfsadfasdf")
        }
        dataRepository.fetchDeviceListData(body).subscribeOn(Schedulers.io()).subscribe(observer)
    }

    fun deviceList(): LiveData<DeviceListData> {
        return deviceListDataLv
    }

    fun connectStatus(): LiveData<Boolean> {
        return lvConnectStatus
    }

    fun loading(): LiveData<Boolean> {
        return loadingLv
    }
}

private val deviceNameVal: String by lazy {
    var result: String = App.sp.getString("device_name", "") ?: ""
    if (result.isEmpty()) {
        OmniLog.d("emptyDeviceName")
        result = try {
            Settings.Global.getString(App.instance.contentResolver, Settings.Global.DEVICE_NAME)
        } catch (e: Exception) {
            ""
        }
        App.sp.edit().putString("device_name", result).apply()
    }
    result
}

private val instanceIdVal : String by lazy {
    var result: String = App.sp.getString("instance_id", "") ?: ""
    if (result.isEmpty()) {
        OmniLog.d("emptyInstanceId")
        result = UUID.randomUUID().toString().replace("-", "")
        App.sp.edit().putString("instance_id", result).apply()
    }
    result
}

private val macAddressVal : String by lazy {
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

private val userAgent = "Android"
