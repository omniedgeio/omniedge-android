package io.omniedge.data

import io.omniedge.data.bean.*
import io.reactivex.Single
import retrofit2.http.*

interface HttpApi {

    // ==================== AUTH ====================
    @POST("/api/v2/auth/register")
    fun register(@Body params: Register): Single<Response>

    @Headers("provider: password")
    @POST("/api/v2/auth/login/password")
    fun login(@Body params: PasswordLogin): Single<LoginResponse>

    @Headers("provider: google")
    @POST("/api/v2/auth/login/google")
    fun loginWithGoogle(@Body params: GoogleLogin): Single<LoginResponse>

    @POST("/api/v2/auth/refresh")
    fun refreshToken(@Body params: RefreshToken): Single<LoginResponse>

    @POST("/api/v2/auth/reset-password/code")
    fun resetPassword(@Body params: ResetPassword): Single<Response>

    @POST("/api/v2/auth/reset-password/verify")
    fun resetPasswordVerify(@Body params: ResetPasswordVerify): Single<Response>

    // ==================== PROFILE ====================
    @GET("/api/v2/profile")
    fun getProfile(): Single<ProfileResponse>

    @PUT("/api/v2/profile")
    fun updateProfile(@Body params: UpdateProfile): Single<Response>

    @PUT("/api/v2/profile/change-password")
    fun changePassword(@Body params: ChangePassword): Single<Response>

    // ==================== DEVICES ====================
    @Headers("Content-Type: application/json")
    @POST("/api/v2/devices")
    fun registerDevice(@Body params: RegisterDevice): Single<RegisterDeviceResponse>

    @GET("/api/v2/devices")
    fun listDevices(): Single<ListDevicesResponse>

    @GET("/api/v2/devices/{id}")
    fun retrieveDevice(@Path("id") id: String): Single<DeviceResponse>

    @PUT("/api/v2/devices/{id}")
    fun updateDevice(@Path("id") id: String, @Body params: UpdateDevice): Single<Response>

    @DELETE("/api/v2/devices/{id}")
    fun deleteDevice(@Path("id") id: String): Single<Response>

    @POST("/api/v2/devices/heartbeat")
    fun deviceHeartbeat(@Body params: DeviceHeartbeat?): Single<HeartbeatResponse>

    // ==================== VIRTUAL NETWORKS ====================
    @POST("/api/v2/virtual-networks")
    fun createNetwork(@Body network: CreateNetwork): Single<CreateNetworkResponse>

    @GET("/api/v2/virtual-networks")
    fun listNetworks(): Single<ListNetworkResponse>

    @GET("/api/v2/virtual-networks/{id}")
    fun retrieveNetwork(@Path("id") id: String): Single<NetworkResponse>

    @PUT("/api/v2/virtual-networks/{id}")
    fun updateNetwork(@Path("id") id: String, @Body params: UpdateNetwork): Single<Response>

    @DELETE("/api/v2/virtual-networks/{id}")
    fun deleteNetwork(@Path("id") id: String): Single<Response>

    @GET("/api/v2/virtual-networks/{id}/devices")
    fun listNetworkDevices(@Path("id") id: String): Single<ListDevicesResponse>

    @POST("/api/v2/virtual-networks/{networkID}/devices/{deviceID}")
    fun joinNetwork(
        @Path("networkID") networkID: String,
        @Path("deviceID") deviceID: String,
    ): Single<JoinNetworkResponse>

    @DELETE("/api/v2/virtual-networks/{networkID}/devices/{deviceID}")
    fun removeDeviceFromNetwork(
        @Path("networkID") networkID: String,
        @Path("deviceID") deviceID: String,
    ): Single<Response>

    // ==================== SERVERS ====================
    @GET("/api/v2/servers")
    fun listServers(): Single<ListServersResponse>
}