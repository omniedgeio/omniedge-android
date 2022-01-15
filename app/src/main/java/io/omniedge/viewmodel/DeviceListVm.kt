package io.omniedge.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import io.omniedge.App
import io.omniedge.OmniLog
import io.omniedge.R
import io.omniedge.RxBus
import io.omniedge.data.DataRepository
import io.omniedge.data.bean.*
import io.omniedge.n2n.event.StartEvent
import io.omniedge.n2n.event.StopEvent
import io.omniedge.n2n.event.SupernodeDisconnectEvent
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


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
        compositeDisposable.add(
            RxBus.INSTANCE.toObservable(StartEvent::class.java)
                .subscribe {
                    lvConnectStatus.postValue(true)
                })
        compositeDisposable.add(
            RxBus.INSTANCE.toObservable(StopEvent::class.java)
                .subscribe {
                    lvConnectStatus.postValue(false)
                })
        compositeDisposable.add(
            RxBus.INSTANCE.toObservable(SupernodeDisconnectEvent::class.java)
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
                response.data?.id?.apply {
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
        val deviceID = repository.getDeviceUUID()
        if (deviceID != null && network?.id != null) {
            repository.joinNetwork(network.id, deviceID)
                .subscribeOn(Schedulers.io())

                // start service after joined network
                .doOnSuccess { response ->
                    repository.setNetworkInfo(response.data)
                    joinedNetworkLv.postValue(response.data)
                    repository.setLatestJoinedNetworkUUID(network.id)
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