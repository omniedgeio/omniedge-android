package io.omniedge

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Rect
import android.net.VpnService
import android.os.Bundle
import android.os.SystemClock
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.appcompat.widget.PopupMenu
import androidx.core.os.TraceCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import io.omniedge.data.DataRepository
import io.omniedge.data.bean.*
import io.omniedge.n2n.N2NService
import io.omniedge.n2n.event.StartEvent
import io.omniedge.n2n.event.StopEvent
import io.omniedge.n2n.event.SupernodeDisconnectEvent
import io.omniedge.n2n.model.EdgeStatus
import io.omniedge.n2n.model.N2NSettingInfo
import io.omniedge.ui.activity.BaseActivity
import io.omniedge.ui.activity.HelpActivity
import io.omniedge.ui.activity.LoginActivity
import io.omniedge.ui.fragment.BaseFragment
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_device_list.*
import kotlinx.android.synthetic.main.fragment_device_list.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
        })
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

class DeviceListFragment : BaseFragment() {

    override fun getLayoutRes(): Int {
        return R.layout.fragment_device_list
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        TraceCompat.beginSection("DeviceList#onViewCreated")
        refresh.setOnRefreshListener {
            refresh.isRefreshing = false
            DeviceListVm.registerDevice()
        }
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
        val fragmentActivity = activity as FragmentActivity
        fragmentActivity.findViewById<View>(R.id.btn_ping)
            ?.setOnClickListener { deviceAdapter.updatePing() }

        DeviceListVm.networks().observe(fragmentActivity) { networks ->
            deviceAdapter.updateData(networks)
        }

        TraceCompat.endSection()
    }
}

class DeviceVh(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val rbNetwork: AppCompatRadioButton? = itemView.findViewById(R.id.network_radio_button)

    val tvDeviceName: TextView? = itemView.findViewById(R.id.tv_name)
    val tvIp: TextView? = itemView.findViewById(R.id.tv_ip)
    val tvPing: TextView? = itemView.findViewById(R.id.tv_ping)
}

class DeviceAdapter : RecyclerView.Adapter<DeviceVh>() {
    private data class ListDeviceData(
        val device: DeviceData?,
        val network: NetworkData?,
        val isNetworkData: Boolean,
        val index: Int,
        var joined: Boolean = false,
        var ping: String = "?ms",
    )

    companion object {
        const val TYPE_NETWORK = 0
        const val TYPE_DEVICE = 1

    }

    private val networkData = mutableListOf<ListDeviceData>()

    fun updatePing() {
        for ((index, data) in networkData.withIndex()) {
            if (data.isNetworkData) continue
            val privateIp = data.device?.virtualIp ?: continue
            object : Thread("updatePing-$index") {
                override fun run() {
                    super.run()
                    val ping: String = try {
                        ping(privateIp)
                    } catch (e: Throwable) { // 跨进程调用
                        OmniLog.e("error on ping $privateIp", e)
                        "?ms"
                    }
                    if (ping.isNotEmpty() && "?ms" != ping) {
                        data.ping = ping
                        ThreadUtils.runOnMainThread {
                            networkData.indexOf(data).let {
                                if (it > 0) {
                                    this@DeviceAdapter.notifyItemChanged(it)
                                }
                            }
                        }
                    }
                }
            }.start()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceVh {
        TraceCompat.beginSection("DeviceAdapter#onCreateViewHolder")
        val viewHolder: DeviceVh = when (viewType) {
            TYPE_NETWORK -> {
                val inflater = LayoutInflater.from(parent.context)
                val itemView = inflater.inflate(R.layout.item_list_network, parent, false)
                DeviceVh(itemView)
            }
            TYPE_DEVICE -> {
                val inflater = LayoutInflater.from(parent.context)
                val itemView = inflater.inflate(R.layout.item_list_device, parent, false)
                DeviceVh(itemView)
            }
            else -> {
                throw IllegalStateException()
            }
        }
        TraceCompat.endSection()
        return viewHolder
    }

    override fun getItemCount(): Int {
        return networkData.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (networkData[position].isNetworkData) {
            true -> TYPE_NETWORK
            false -> TYPE_DEVICE
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DeviceVh, position: Int) {
        TraceCompat.beginSection("DeviceAdapter#onBindViewHolder")
        val data = networkData[position]
        if (data.isNetworkData) {
            holder.rbNetwork?.text = data.network?.name
            holder.rbNetwork?.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    DeviceListVm.joinNetwork(data.network)
                }
            }
            holder.rbNetwork?.isChecked = data.joined

        } else {
            holder.tvDeviceName?.text = data.device?.name
            holder.tvIp?.text = data.device?.virtualIp
            holder.tvPing?.text = if (data.device?.online == true) data.ping else "offline"
        }
        TraceCompat.endSection()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(networks: List<NetworkData>) {
        networkData.clear()
        var index = 0
        val uuid = App.repository.getLatestJoinedNetworkUUID()
        val deviceUUID = App.repository.getDeviceUUID()
        networks.forEach { data ->
            var deviceJoined = false
            var deviceData: DeviceData? = null
            val listData =
                ListDeviceData(null, data, true, index++)
            networkData.add(listData)
            data.devices?.forEach {
                if (it.uuid == deviceUUID) {
                    deviceJoined = true
                    deviceData = it
                }
                networkData.add(ListDeviceData(it, null, false, index))
            }
            listData.joined = uuid != null && uuid == data.uuid && deviceJoined
            if (listData.joined) {
                DeviceListVm.currentNetwork()
                    .postValue(Pair(data, deviceData) as Pair<NetworkData, DeviceData>?)
                // join network on the first time
                if (DeviceListVm.joinedNetwork().value == null) {
                    DeviceListVm.joinNetwork(data)
                }
            }
        }
        // TODO: 2021/8/22 use the efficient way to refresh
        notifyDataSetChanged()
    }
}


object DeviceListVm {
    @SuppressLint("StaticFieldLeak")
    private val repository = DataRepository.getInstance(App.instance)
    private val compositeDisposable = CompositeDisposable()
    private val loadingLv: MutableLiveData<Boolean> = MutableLiveData()
    private val lvConnectStatus: MutableLiveData<Boolean> = MutableLiveData()
    private val toastLv: MutableLiveData<String?> = MutableLiveData()
    private val logoutLv: MutableLiveData<Boolean> = MutableLiveData()
    private val networksLv: MutableLiveData<List<NetworkData>> = MutableLiveData()
    private val joinedNetworkLv: MutableLiveData<JoinNetworkData?> = MutableLiveData()
    private val currentNetworkLv: MutableLiveData<Pair<NetworkData, DeviceData>> = MutableLiveData()

    init {
        compositeDisposable.add(RxBus.INSTANCE.toObservable(StartEvent::class.java)
            .subscribe {
                lvConnectStatus.postValue(true)
            })
        compositeDisposable.add(RxBus.INSTANCE.toObservable(StopEvent::class.java)
            .subscribe {
                lvConnectStatus.postValue(false)
            })
        compositeDisposable.add(RxBus.INSTANCE.toObservable(SupernodeDisconnectEvent::class.java)
            .subscribe {
                lvConnectStatus.postValue(false)
            })
    }

    fun connectStatus() = lvConnectStatus

    fun loading() = loadingLv

    fun toast() = toastLv

    fun logout() = logoutLv

    fun networks() = networksLv

    fun joinedNetwork() = joinedNetworkLv

    fun currentNetwork() = currentNetworkLv

    fun registerDevice() {
        repository.registerDevice(
            RegisterDevice(
                repository.getDeviceName(),
                repository.getHardwareUUID(),
                repository.getOSInfo()
            )
        )
            .subscribeOn(Schedulers.io())
            .doOnSuccess { response ->
                response.data?.uuid?.apply {
                    repository.updateDeviceUUID(this)
                }
            }
            // list networks
            .flatMap { repository.listNetworks() }
            .subscribe(object : SingleObserver<ListNetworkResponse> {
                override fun onSubscribe(d: Disposable) {
                    loadingLv.postValue(true)
                }

                override fun onSuccess(t: ListNetworkResponse) {
                    loadingLv.postValue(false)
                    OmniLog.d("response: $t")
                    networksLv.postValue(t.data)
                }

                override fun onError(e: Throwable) {
                    loadingLv.postValue(false)
                    OmniLog.e("error on list network", e)
                    toastLv.postValue(e.message)
                }
            })

    }

    fun listNetworks() {
        repository.listNetworks()
            .subscribeOn(Schedulers.io())
            .subscribe(object : SingleObserver<ListNetworkResponse> {
                override fun onSubscribe(d: Disposable) {
                    loadingLv.postValue(true)
                }

                override fun onSuccess(t: ListNetworkResponse) {
                    loadingLv.postValue(false)
                    OmniLog.d("response: $t")
                    networksLv.postValue(t.data)
                }

                override fun onError(e: Throwable) {
                    loadingLv.postValue(false)
                    OmniLog.e("error on list network", e)
                    toastLv.postValue(e.message)
                }
            })

    }

    fun joinNetwork(network: NetworkData?) {
        val deviceUUID = repository.getDeviceUUID()
        if (deviceUUID != null && network?.uuid != null) {
            repository.joinNetwork(network.uuid, deviceUUID)
                .subscribeOn(Schedulers.io())

                // start service after joined network
                .doOnSuccess { response ->
                    repository.setNetworkInfo(response.data)
                    joinedNetworkLv.postValue(response.data)
                    repository.setLatestJoinedNetworkUUID(network.uuid)
                }
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<JoinNetworkResponse> {
                    override fun onSubscribe(d: Disposable) {
                        loadingLv.postValue(true)
                    }

                    override fun onSuccess(t: JoinNetworkResponse) {
                        loadingLv.postValue(false)
                    }

                    override fun onError(e: Throwable) {
                        loadingLv.postValue(false)
                        OmniLog.e("error on join network", e)
                        toastLv.postValue(e.message)
                    }
                })

        } else {
            toastLv.postValue(App.instance.getString(R.string.invalid_token))
            logoutLv.postValue(true)
        }
    }

    fun clearNetwork() {
        joinedNetworkLv.postValue(null)
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
