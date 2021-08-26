package io.omniedge.data.bean

import com.google.gson.annotations.SerializedName

data class Response(
    val message: String?,
    val errors: ErrorData?,
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

data class DeviceData(
    val uuid: String,
    val name: String?,
    val os: String?,
    @SerializedName("virtual_ip") val virtualIp: String?,
    @SerializedName("last_seen") val lastSeen: String?,
    val online: Boolean = false,
)

data class NetworkData(
    val uuid: String,
    val name: String?,
    @SerializedName("ip_range") val ipRange: String?,
    val role: String?,
    val devices: List<DeviceData>?,
)

data class JoinNetworkData(
    @SerializedName("community_name") val communityName: String,
    @SerializedName("secret_key") val secretKey: String,
    @SerializedName("virtual_ip") val virtualIp: String,
    @SerializedName("subnet_mask") val subnetMask: String,
    @SerializedName("server") val server: ServerData,
) {
    data class ServerData(val name: String, val country: String, val host: String)
}
