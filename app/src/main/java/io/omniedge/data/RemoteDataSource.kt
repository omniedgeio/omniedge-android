package io.omniedge.data

import io.omniedge.data.bean.*
import io.reactivex.Single
import retrofit2.http.Body

class RemoteDataSource(private val httpApi: HttpApi) : HttpApi {

    override fun register(@Body params: Register): Single<Response> {
        return httpApi.register(params)
    }

    override fun login(params: PasswordLogin): Single<LoginResponse> {
        return httpApi.login(params)
    }

    override fun googleLogin(params: GoogleLogin): Single<Response> {
        return httpApi.googleLogin(params)
    }

    override fun resetPassword(): Single<Response> {
        return httpApi.resetPassword()
    }

    override fun resetPasswordVerify(): Single<Response> {
        return httpApi.resetPasswordVerify()
    }

    override fun retrieveProfile(): Single<Response> {
        return httpApi.retrieveProfile()
    }

    override fun updateProfile(): Single<Response> {
        return httpApi.updateProfile()
    }

    override fun linkGoogleAccount(): Single<Response> {
        return httpApi.linkGoogleAccount()
    }

    override fun activatePasswordLogin(): Single<Response> {
        return httpApi.activatePasswordLogin()
    }

    override fun registerDevice(@Body params: RegisterDevice): Single<RegisterDeviceResponse> {
        return httpApi.registerDevice(params)
    }

    override fun listDevices(): Single<Response> {
        return httpApi.listDevices()
    }

    override fun retrieveDevice(uuid: String): Single<Response> {
        return httpApi.retrieveDevice(uuid)
    }

    override fun updateDevice(uuid: String): Single<Response> {
        return httpApi.updateDevice(uuid)
    }

    override fun deleteDevice(uuid: String): Single<Response> {
        return httpApi.deleteDevice(uuid)
    }

    override fun createNetwork(network: CreateNetwork): Single<Response> {
        return httpApi.createNetwork(network)
    }

    override fun listNetworks(): Single<ListNetworkResponse> {
        return httpApi.listNetworks()
    }

    override fun retrieveNetwork(uuid: String): Single<Response> {
        return httpApi.retrieveNetwork(uuid)
    }

    override fun updateNetwork(uuid: String): Single<Response> {
        return httpApi.updateNetwork(uuid)
    }

    override fun deleteNetwork(uuid: String): Single<Response> {
        return httpApi.deleteNetwork(uuid)
    }

    override fun removeDeviceFromNetwork(
        networkUUID: String,
        deviceUUID: String
    ): Single<Response> {
        return httpApi.removeDeviceFromNetwork(networkUUID, deviceUUID)
    }

    override fun joinNetwork(networkUUID: String, deviceUUID: String): Single<JoinNetworkResponse> {
        return httpApi.joinNetwork(networkUUID, deviceUUID)
    }
}

