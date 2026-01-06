package io.omniedge.data.bean

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class Response(
    val message: String?,
    val errors: JsonObject?,
)

data class ErrorData(val password: String?)

// ==================== AUTH RESPONSES ====================
data class LoginResponse(
    val message: String?,
    val data: LoginData?
) {
    data class LoginData(
        val token: String?,
        @SerializedName("refresh_token") val refreshToken: String?,
        @SerializedName("expires_at") val expiresAt: String?
    )
}

// ==================== DEVICE RESPONSES ====================
data class RegisterDeviceResponse(val message: String?, val data: DeviceData?)
data class DeviceResponse(val message: String?, val data: DeviceData?)
data class ListDevicesResponse(val message: String?, val data: List<DeviceData> = arrayListOf())

data class HeartbeatResponse(
    val message: String?,
    @SerializedName("last_seen") val lastSeen: String?
)

data class DeviceData(
    val id: String,
    val name: String?,
    val os: String?,
    val platform: String?,
    @SerializedName("hardware_id") val hardwareId: String?,
    @SerializedName("virtual_ip") val virtualIp: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("last_seen") val lastSeen: String?,
    val online: Boolean = false,
)

// ==================== PROFILE RESPONSES ====================
data class ProfileResponse(
    val message: String?,
    val data: ProfileData?
) {
    data class ProfileData(
        val id: String,
        val email: String?,
        val name: String?,
        val picture: String?,
        @SerializedName("created_at") val createdAt: String?,
    )
}

// ==================== NETWORK RESPONSES ====================
data class ListNetworkResponse(val message: String?, val data: List<NetworkData> = arrayListOf())
data class NetworkResponse(val message: String?, val data: NetworkData?)
data class CreateNetworkResponse(val message: String?, val data: NetworkData?)
data class JoinNetworkResponse(val message: String?, val data: JoinNetworkData?)

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
    val role: Int? = null,
    @SerializedName("users_count") val usersCount: Int? = null,
    @SerializedName("devices_count") val devicesCount: Int? = null,
    val devices: List<DeviceData>? = null,
    val users: List<UserData>? = null,
    val server: ServerData? = null,
    @SerializedName("created_at") val createdAt: String? = null,
)

data class JoinNetworkData(
    @SerializedName("community_name") val communityName: String,
    @SerializedName("secret_key") val secretKey: String,
    @SerializedName("virtual_ip") val virtualIp: String,
    @SerializedName("subnet_mask") val subnetMask: String,
    @SerializedName("server") val server: ServerData,
)

// ==================== SERVER RESPONSES ====================
data class ListServersResponse(val message: String?, val data: List<ServerData> = arrayListOf())

data class ServerData(
    val id: String,
    val name: String,
    val country: String? = null,
    val host: String
)
