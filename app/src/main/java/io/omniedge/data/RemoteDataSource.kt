package io.omniedge.data

import io.omniedge.data.bean.*
import io.reactivex.Single

class RemoteDataSource(private val httpApi: HttpApi) : HttpApi {

    // ==================== AUTH ====================
    override fun register(params: Register): Single<Response> {
        return httpApi.register(params)
    }

    override fun login(params: PasswordLogin): Single<LoginResponse> {
        return httpApi.login(params)
    }

    override fun loginWithGoogle(params: GoogleLogin): Single<LoginResponse> {
        return httpApi.loginWithGoogle(params)
    }

    override fun refreshToken(params: RefreshToken): Single<LoginResponse> {
        return httpApi.refreshToken(params)
    }

    override fun resetPassword(params: ResetPassword): Single<Response> {
        return httpApi.resetPassword(params)
    }

    override fun resetPasswordVerify(params: ResetPasswordVerify): Single<Response> {
        return httpApi.resetPasswordVerify(params)
    }

    // ==================== PROFILE ====================
    override fun getProfile(): Single<ProfileResponse> {
        return httpApi.getProfile()
    }

    override fun updateProfile(params: UpdateProfile): Single<Response> {
        return httpApi.updateProfile(params)
    }

    override fun changePassword(params: ChangePassword): Single<Response> {
        return httpApi.changePassword(params)
    }

    // ==================== DEVICES ====================
    override fun registerDevice(params: RegisterDevice): Single<RegisterDeviceResponse> {
        return httpApi.registerDevice(params)
    }

    override fun listDevices(): Single<ListDevicesResponse> {
        return httpApi.listDevices()
    }

    override fun retrieveDevice(id: String): Single<DeviceResponse> {
        return httpApi.retrieveDevice(id)
    }

    override fun updateDevice(id: String, params: UpdateDevice): Single<Response> {
        return httpApi.updateDevice(id, params)
    }

    override fun deleteDevice(id: String): Single<Response> {
        return httpApi.deleteDevice(id)
    }

    override fun deviceHeartbeat(params: DeviceHeartbeat?): Single<HeartbeatResponse> {
        return httpApi.deviceHeartbeat(params)
    }

    // ==================== VIRTUAL NETWORKS ====================
    override fun createNetwork(network: CreateNetwork): Single<CreateNetworkResponse> {
        return httpApi.createNetwork(network)
    }

    override fun listNetworks(): Single<ListNetworkResponse> {
        return httpApi.listNetworks()
    }

    override fun retrieveNetwork(id: String): Single<NetworkResponse> {
        return httpApi.retrieveNetwork(id)
    }

    override fun updateNetwork(id: String, params: UpdateNetwork): Single<Response> {
        return httpApi.updateNetwork(id, params)
    }

    override fun deleteNetwork(id: String): Single<Response> {
        return httpApi.deleteNetwork(id)
    }

    override fun listNetworkDevices(id: String): Single<ListDevicesResponse> {
        return httpApi.listNetworkDevices(id)
    }

    override fun joinNetwork(networkID: String, deviceID: String): Single<JoinNetworkResponse> {
        return httpApi.joinNetwork(networkID, deviceID)
    }

    override fun removeDeviceFromNetwork(networkID: String, deviceID: String): Single<Response> {
        return httpApi.removeDeviceFromNetwork(networkID, deviceID)
    }

    // ==================== SERVERS ====================
    override fun listServers(): Single<ListServersResponse> {
        return httpApi.listServers()
    }
}
