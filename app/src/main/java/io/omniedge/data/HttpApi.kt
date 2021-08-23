package io.omniedge.data

import io.omniedge.data.bean.*
import io.reactivex.Single
import retrofit2.http.*

interface HttpApi {

    @POST("/api/auth/register")
    fun register(): Single<Response>

    @Headers("provider: password")
    @POST("/api/auth/login/password")
    fun login(@Body params: PasswordLogin): Single<LoginResponse>

    @Headers("provider: google")
    @POST("/api/auth/login/google")
    fun googleLogin(@Body params: GoogleLogin): Single<Response>

    @POST("/api/auth/reset-password/code")
    fun resetPassword(): Single<Response>

    @POST("/api/auth/reset-password/verify")
    fun resetPasswordVerify(): Single<Response>

    @POST("/api/user/profile")
    fun retrieveProfile(): Single<Response>

    @POST("/api/user/profile")
    fun updateProfile(): Single<Response>

    @POST("/api/user/auth/identities/google")
    fun linkGoogleAccount(): Single<Response>

    @POST("/api/user/auth/identities/password")
    fun activatePasswordLogin(): Single<Response>

    @Headers("Content-Type: application/json")
    @POST("/api/devices/register")
    fun registerDevice(@Body params: RegisterDevice): Single<RegisterDeviceResponse>

    @GET("/api/devices")
    fun listDevices(): Single<Response>

    @GET("/api/devices/{uuid}")
    fun retrieveDevice(@Path("uuid") uuid: String): Single<Response>

    @POST("/api/devices/{uuid}")
    fun updateDevice(@Path("uuid") uuid: String): Single<Response>

    @DELETE("/api/devices/{uuid}")
    fun deleteDevice(@Path("uuid") uuid: String): Single<Response>

    @POST("/api/virtual-networks")
    fun createNetwork(@Body network: CreateNetwork): Single<Response>

    @GET("/api/virtual-networks")
    fun listNetworks(): Single<ListNetworkResponse>

    @GET("/api/virtual-networks/{uuid}")
    fun retrieveNetwork(@Path("uuid") uuid: String): Single<Response>

    @POST("/api/virtual-networks/{uuid}")
    fun updateNetwork(@Path("uuid") uuid: String): Single<Response>

    @DELETE("/api/virtual-networks/{uuid}")
    fun deleteNetwork(@Path("uuid") uuid: String): Single<Response>

    @DELETE("/api/virtual-networks/{networkUUID}/devices/{deviceUUID}")
    fun removeDeviceFromNetwork(
        @Path("networkUUID") networkUUID: String,
        @Path("deviceUUID") deviceUUID: String,
    ): Single<Response>

    @POST("/api/virtual-networks/{networkUUID}/devices/{deviceUUID}/join")
    fun joinNetwork(
        @Path("networkUUID") networkUUID: String,
        @Path("deviceUUID") deviceUUID: String,
    ): Single<JoinNetworkResponse>

}