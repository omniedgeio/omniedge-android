package io.omniedge.data

import android.content.Context
import android.util.Log
import io.omniedge.BuildConfig
import io.omniedge.SingletonHolder
import io.omniedge.data.bean.*
import io.omniedge.util.NetConfig
import io.reactivex.Single

/**
 * Created on 2019-12-22 11:24
 *
 */
class DataRepository private constructor(private val context: Context) {

    private val remoteDataSource = RemoteDataSource(
        NetConfig.provideRetrofit(BuildConfig.BASE_URL).create(HttpApi::class.java)
    )
    private val localDataSource = LocalDataSource(context)

    companion object {
        private const val TAG = "DataRepository"
        private val instance = SingletonHolder<DataRepository, Context> {
            return@SingletonHolder DataRepository(it)
        }

        fun getInstance(context: Context): DataRepository {
            return instance.getInstance(context.applicationContext)
        }

    }

    fun register(registerInfo: Register): Single<Response> {
        return remoteDataSource.register(registerInfo)
    }

    fun login(email: String, password: String): Single<LoginResponse> {
        return remoteDataSource.login(PasswordLogin(null, email, password))
            .doOnSuccess {
                Log.d(TAG, "sign in result:$it")
            }
    }

    fun loginWithGoogle(idToken: String): Single<LoginResponse> {
        return remoteDataSource.loginWithGoogle(GoogleLogin(null, idToken))
    }

    fun resetPassword(email: String): Single<Response> {
        return remoteDataSource.resetPassword(ResetPassword(email))
    }

    fun listDevices(): Single<Response> {
        return remoteDataSource.listDevices()
    }

    fun retrieveProfile(): Single<Response> {
        return remoteDataSource.retrieveProfile()
    }

    fun registerDevice(device: RegisterDevice): Single<RegisterDeviceResponse> {
        return remoteDataSource.registerDevice(device)
    }

    fun listNetworks(): Single<ListNetworkResponse> {
        return remoteDataSource.listNetworks()
    }

    fun joinNetwork(networkID: String, deviceID: String): Single<JoinNetworkResponse> {
        return remoteDataSource.joinNetwork(networkID, deviceID)
    }

    fun updateToken(token: String?) {
        localDataSource.updateToken(token)
    }

    fun getToken(): String? {
        return localDataSource.getToken()
    }

    fun getDeviceName(): String {
        return localDataSource.deviceName
    }

    fun getDeviceUUID(): String? {
        return localDataSource.getDeviceUUID()
    }

    fun getOSInfo(): String {
        return localDataSource.osInfo
    }

    fun updateDeviceUUID(uuid: String) {
        localDataSource.updateDeviceUUID(uuid)
    }

    fun getHardwareUUID(): String {
        return localDataSource.getHardwareUUID()
    }

    fun updateCommunityName(communityName: String) {
        localDataSource.updateCommunityName(communityName)
    }

    fun updateSecretKey(secretKey: String) {
        localDataSource.updateSecretKey(secretKey)
    }

    fun updateSubnetMask(subnetMask: String) {
        localDataSource.updateSubnetMask(subnetMask)
    }

    fun updateVirtualIP(virtualIp: String) {
        localDataSource.updateVirtualIP(virtualIp)
    }

    fun setNetworkInfo(networkData: JoinNetworkData) {
        localDataSource.setNetworkInfo(networkData)
    }

    fun getLatestJoinedNetworkUUID(): String? {
        return localDataSource.getLatestJoinedNetworkUUID()
    }

    fun setLatestJoinedNetworkUUID(uuid: String) {
        localDataSource.setLatestJoinedNetworkUUID(uuid)
    }
}