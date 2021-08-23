package io.omniedge.data.bean

import com.google.gson.annotations.SerializedName

data class ActivatePasswordLogin(
    val password: String,
    val confirmPassword: String,
)

data class ChangePassword(
    val oldPassword: String,
    val password: String,
    val confirmPassword: String,
)

data class CreateNetwork(
    val name: String,
    val serverUUID: String,
)

data class LinkGoogleAccount(
    val idToken: String,
)

open class Login(authSessionUuid: String?)

data class GoogleLogin(
    val authSessionUuid: String?,
    val idToken: String,
    val key: String,
) : Login(authSessionUuid)

data class PasswordLogin(
    val authSessionUuid: String?,
    val email: String,
    val password: String,
) : Login(authSessionUuid)

data class Register(
    val name: String,
    val email: String,
    val password: String,
    val confirmPassword: String
)

data class RegisterDevice(
    val name: String,
    @SerializedName("hardware_uuid") val hardwareUUID: String,
    val os: String,
)

data class ResetPassword(val email: String)

data class ResetPasswordVerify(
    val token: String,
    val password: String,
    val confirmPassword: String,
)

data class UpdateDevice(
    val uuid: String,
)

data class UpdateNetwork(
    val name: String,
    val serverUUID: String,
)

data class UpdateProfile(
    val name: String,
    val email: String,
)
