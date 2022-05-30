package io.omniedge.data.bean

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class Response(
    val message: String?,
    val errors: JsonObject?,
)

data class ErrorData(val password: String?)

data class LoginResponse(
    val message: String?,
    val data: LoginData?
) {
    data class LoginData(val token: String?)
}

data class RegisterDeviceResponse(val message: String?, val data: DeviceData?)
data class ListNetworkResponse(val message: String?, val data: List<NetworkData> = arrayListOf())
data class JoinNetworkResponse(val message: String?, val data: JoinNetworkData)
data class CreateNetworkResponse(val message: String?, val data: NetworkData)

data class DeviceData(
    val id: String,
    val name: String?,
    val platform: String?,
    @SerializedName("hardware_id") val hardwareId: String?,
    @SerializedName("virtual_ip") val virtualIp: String?,
    @SerializedName("created_at") val created_at: String?,
    val online: Boolean = false,
)

data class UserData(
    val id: String,
    val name: String?,
    val email: String?,
    val picture: String?,
    @SerializedName("last_login_ip") val lastLoginIp: String?,
    @SerializedName("last_login_at") val lastLoginAt: String?,
    val status: Int,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?,
    val role: Int,
    @SerializedName("joined_at") val joinedAt: String?,
)

data class NetworkData(
    val id: String,
    val name: String?,
    @SerializedName("ip_range") val ipRange: String?,
    val role: Int,
    val usersCount: Int,
    val devicesCount: Int,
    val devices: List<DeviceData>?,
    val users: List<UserData>?,
    val server: ServerData,
)

data class JoinNetworkData(
    @SerializedName("community_name") val communityName: String,
    @SerializedName("secret_key") val secretKey: String,
    @SerializedName("virtual_ip") val virtualIp: String,
    @SerializedName("subnet_mask") val subnetMask: String,
    @SerializedName("server") val server: ServerData,
)

data class ServerData(val id: String, val name: String, val country: String, val host: String)
