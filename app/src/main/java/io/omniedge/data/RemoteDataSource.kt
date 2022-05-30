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

    override fun loginWithGoogle(params: GoogleLogin): Single<LoginResponse> {
        return httpApi.loginWithGoogle(params)
    }

    override fun resetPassword(params: ResetPassword): Single<Response> {
        return httpApi.resetPassword(params)
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

    override fun retrieveDevice(id: String): Single<Response> {
        return httpApi.retrieveDevice(id)
    }

    override fun updateDevice(id: String): Single<Response> {
        return httpApi.updateDevice(id)
    }

    override fun deleteDevice(id: String): Single<Response> {
        return httpApi.deleteDevice(id)
    }

    override fun createNetwork(network: CreateNetwork): Single<CreateNetworkResponse> {
        return httpApi.createNetwork(network)
    }

    override fun listNetworks(): Single<ListNetworkResponse> {
        return httpApi.listNetworks()
    }

    override fun retrieveNetwork(id: String): Single<Response> {
        return httpApi.retrieveNetwork(id)
    }

    override fun updateNetwork(id: String): Single<Response> {
        return httpApi.updateNetwork(id)
    }

    override fun deleteNetwork(id: String): Single<Response> {
        return httpApi.deleteNetwork(id)
    }

    override fun removeDeviceFromNetwork(
        networkID: String,
        deviceID: String
    ): Single<Response> {
        return httpApi.removeDeviceFromNetwork(networkID, deviceID)
    }

    override fun joinNetwork(networkID: String, deviceID: String): Single<JoinNetworkResponse> {
        return httpApi.joinNetwork(networkID, deviceID)
    }
}

