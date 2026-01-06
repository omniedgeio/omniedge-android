package io.omniedge.data.bean

import com.google.gson.annotations.SerializedName

data class ActivatePasswordLogin(
    val password: String,
    @SerializedName("confirm_password") val confirmPassword: String,
)

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

open class Login(authSessionUuid: String?)

data class GoogleLogin(
    @SerializedName("auth_session_uuid") val authSessionUuid: String?,
    @SerializedName("id_token") val idToken: String,
) : Login(authSessionUuid)

data class PasswordLogin(
    @SerializedName("auth_session_uuid") val authSessionUuid: String?,
    val email: String,
    val password: String,
) : Login(authSessionUuid)

data class Register(
    val name: String,
    val email: String,
    val password: String,
    @SerializedName("confirm_password") val confirmPassword: String
)

data class RegisterDevice(
    val name: String,
    @SerializedName("hardware_id") val hardwareId: String,
    val os: String? = null,
    val platform: String? = "android",
)

data class ResetPassword(val email: String)

data class ResetPasswordVerify(
    val email: String,
    val code: String,
    val password: String,
)

data class RefreshToken(
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
