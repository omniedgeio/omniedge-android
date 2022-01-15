package io.omniedge.data

import io.omniedge.data.bean.*
import io.reactivex.Single
import retrofit2.http.*

interface HttpApi {

    @POST("/api/v1/auth/register")
    fun register(@Body params: Register): Single<Response>

    @Headers("provider: password")
    @POST("/api/v1/auth/login/password")
    fun login(@Body params: PasswordLogin): Single<LoginResponse>

    @Headers("provider: google")
    @POST("/api/v1/auth/login/google")
    fun loginWithGoogle(@Body params: GoogleLogin): Single<LoginResponse>

    @POST("/api/v1/auth/reset-password/code")
    fun resetPassword(@Body params: ResetPassword): Single<Response>

    @POST("/api/v1/auth/reset-password/verify")
    fun resetPasswordVerify(): Single<Response>

    @POST("/api/v1/user/profile")
    fun retrieveProfile(): Single<Response>

    @POST("/api/v1/user/profile")
    fun updateProfile(): Single<Response>

    @POST("/api/v1/user/auth/identities/google")
    fun linkGoogleAccount(): Single<Response>

    @POST("/api/v1/user/auth/identities/password")
    fun activatePasswordLogin(): Single<Response>

    @Headers("Content-Type: application/json")
    @POST("/api/v1/devices/register")
    fun registerDevice(@Body params: RegisterDevice): Single<RegisterDeviceResponse>

    @GET("/api/v1/devices")
    fun listDevices(): Single<Response>

    @GET("/api/v1/devices/{id}")
    fun retrieveDevice(@Path("id") id: String): Single<Response>

    @POST("/api/v1/devices/{id}")
    fun updateDevice(@Path("id") id: String): Single<Response>

    @DELETE("/api/v1/devices/{id}")
    fun deleteDevice(@Path("id") id: String): Single<Response>

    @POST("/api/v1/virtual-networks")
    fun createNetwork(@Body network: CreateNetwork): Single<Response>

    @GET("/api/v1/virtual-networks")
    fun listNetworks(): Single<ListNetworkResponse>

    @GET("/api/v1/virtual-networks/{id}")
    fun retrieveNetwork(@Path("id") id: String): Single<Response>

    @POST("/api/v1/virtual-networks/{id}")
    fun updateNetwork(@Path("id") id: String): Single<Response>

    @DELETE("/api/v1/virtual-networks/{id}")
    fun deleteNetwork(@Path("id") id: String): Single<Response>

    @DELETE("/api/v1/virtual-networks/{networkID}/devices/{deviceID}")
    fun removeDeviceFromNetwork(
        @Path("networkID") networkID: String,
        @Path("deviceID") deviceID: String,
    ): Single<Response>

    @POST("/api/v1/virtual-networks/{networkID}/devices/{deviceID}/join")
    fun joinNetwork(
        @Path("networkID") networkID: String,
        @Path("deviceID") deviceID: String,
    ): Single<JoinNetworkResponse>

}