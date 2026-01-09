package io.omniedge.data.bean

import com.google.gson.annotations.SerializedName

data class ChangePassword(
    @SerializedName("old_password") val oldPassword: String,
    @SerializedName("new_password") val newPassword: String,
)

data class CreateNetwork(
    val name: String,
    @SerializedName("ip_range") val ipRange: String? = null,
    @SerializedName("server_id") val serverID: String? = null,
)

data class LinkGoogleAccount(
    val idToken: String,
)

open class Login(val authSessionUuid: String?)

data class GoogleLogin(
    @SerializedName("auth_session_uuid") val sessionUuid: String?,
    @SerializedName("id_token") val idToken: String,
) : Login(sessionUuid)
data class RegisterDevice(
    val name: String,
    @SerializedName("hardware_id") val hardwareId: String,
    val os: String? = null,
    val platform: String? = "android",
)

    @SerializedName("refresh_token") val refreshToken: String
)

data class DeviceHeartbeat(
    @SerializedName("hardware_id") val hardwareId: String? = null
)

data class UpdateDevice(
    val name: String? = null,
)

data class UpdateNetwork(
    val name: String? = null,
    @SerializedName("server_id") val serverId: String? = null,
)

data class UpdateProfile(
    val name: String? = null,
)

data class OAuthTokenExchange(
    @SerializedName("grant_type") val grantType: String = "authorization_code",
    @SerializedName("client_id") val clientId: String = "omniedge-android",
    val code: String,
    @SerializedName("code_verifier") val codeVerifier: String,
    @SerializedName("redirect_uri") val redirectUri: String
)

data class SessionNotify(
    @SerializedName("auth_session_uuid") val authSessionUuid: String
)

