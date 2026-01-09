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

    // ==================== AUTH ====================
    fun exchangeOAuthCode(code: String, verifier: String, redirectUri: String): Single<LoginResponse> {

        return remoteDataSource.exchangeOAuthCode(OAuthTokenExchange(code = code, codeVerifier = verifier, redirectUri = redirectUri))
            .doOnSuccess { response ->
                response.data?.let { data ->
                    localDataSource.saveLoginData(data.token, data.refreshToken, data.expiresAt)
                }
            }
    }

    fun savePKCE(verifier: String, state: String) {
        localDataSource.savePKCE(verifier, state)
    }

    fun getPKCEVerifier(): String? = localDataSource.getPKCEVerifier()
    fun getPKCEState(): String? = localDataSource.getPKCEState()
    fun clearPKCE() = localDataSource.clearPKCE()

    fun loginWithGoogle(idToken: String, authSessionUuid: String? = null): Single<LoginResponse> {
        return remoteDataSource.loginWithGoogle(GoogleLogin(authSessionUuid, idToken))
            .doOnSuccess { response ->
                response.data?.let { data ->
                    localDataSource.saveLoginData(data.token, data.refreshToken, data.expiresAt)
                }
            }
    }

    fun refreshToken(): Single<LoginResponse> {
        val refreshToken = localDataSource.getRefreshToken() ?: ""
        return remoteDataSource.refreshToken(RefreshToken(refreshToken))
            .doOnSuccess { response ->
                response.data?.let { data ->
                    localDataSource.saveLoginData(data.token, data.refreshToken, data.expiresAt)
                }
            }
    }

    fun logout() {

        localDataSource.clearAuth()
    }

    fun notifySession(authSessionUuid: String): Single<Response> {
        return remoteDataSource.notifySession(SessionNotify(authSessionUuid))
    }

    // ==================== PROFILE ====================
    fun getProfile(): Single<ProfileResponse> {
        return remoteDataSource.getProfile()
            .doOnSuccess { response ->
                response.data?.let { profile ->
                    localDataSource.updateUserEmail(profile.email)
                    localDataSource.updateUserName(profile.name)
                }
            }
    }

    fun updateProfile(name: String): Single<Response> {
        return remoteDataSource.updateProfile(UpdateProfile(name))
    }

    fun changePassword(oldPassword: String, newPassword: String): Single<Response> {
        return remoteDataSource.changePassword(ChangePassword(oldPassword, newPassword))
    }

    // ==================== DEVICES ====================
    fun listDevices(): Single<ListDevicesResponse> {
        return remoteDataSource.listDevices()
    }

    fun registerDevice(device: RegisterDevice): Single<RegisterDeviceResponse> {
        return remoteDataSource.registerDevice(device)
    }

    fun deviceHeartbeat(): Single<HeartbeatResponse> {
        val hardwareId = localDataSource.getHardwareUUID()
        return remoteDataSource.deviceHeartbeat(DeviceHeartbeat(hardwareId))
    }

    // ==================== NETWORKS ====================
    fun createNetwork(network: CreateNetwork): Single<CreateNetworkResponse> {
        return remoteDataSource.createNetwork(network)
    }

    fun listNetworks(): Single<ListNetworkResponse> {
        return remoteDataSource.listNetworks()
    }

    fun joinNetwork(networkID: String, deviceID: String): Single<JoinNetworkResponse> {
        return remoteDataSource.joinNetwork(networkID, deviceID)
    }

    // ==================== SERVERS ====================
    fun listServers(): Single<ListServersResponse> {
        return remoteDataSource.listServers()
    }

    // ==================== TOKEN MANAGEMENT ====================
    fun updateToken(token: String?) {
        localDataSource.updateToken(token)
    }

    fun getToken(): String? {
        return localDataSource.getToken()
    }

    fun getRefreshToken(): String? {
        return localDataSource.getRefreshToken()
    }

    fun isLoggedIn(): Boolean {
        return !localDataSource.getToken().isNullOrEmpty()
    }

    // ==================== DEVICE INFO ====================
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

    fun generateUUID(): String {
        return localDataSource.generateUUID()
    }

    // ==================== NETWORK INFO ====================
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