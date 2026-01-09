package io.omniedge.data

import android.content.Context
import android.os.Build
import android.provider.Settings
import io.omniedge.data.bean.JoinNetworkData
import java.util.*

class LocalDataSource(context: Context) {
    companion object {
        const val LOCAL_SP = "local"

        const val KEY_TOKEN = "token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
        const val KEY_TOKEN_EXPIRES_AT = "token_expires_at"
        const val KEY_UUID = "uuid"
        const val KEY_HARDWARE_UUID = "hardware_uuid"
        const val KEY_COMMUNITY_NAME = "community_name"
        const val KEY_SECRET_NAME = "secret_key"
        const val KEY_SUBNET_MASK = "subnet_mask"
        const val KEY_VIRTUAL_IP = "virtual_ip"
        const val KEY_SERVER_NAME = "server_name"
        const val KEY_SERVER_HOST = "server_host"
        const val KEY_SERVER_COUNTRY = "server_country"
        const val KEY_LATEST_JOINED_NETWORK_UUID = "latest_joined_network_uuid"
        const val KEY_USER_EMAIL = "user_email"
        const val KEY_USER_NAME = "user_name"
        const val KEY_PKCE_VERIFIER = "pkce_verifier"
        const val KEY_PKCE_STATE = "pkce_state"
    }

    private val sp = context.getSharedPreferences(LOCAL_SP, Context.MODE_PRIVATE)

    // ==================== TOKEN MANAGEMENT ====================
    fun getToken(): String? {
        return sp.getString(KEY_TOKEN, null)
    }

    fun updateToken(token: String?) {
        sp.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getRefreshToken(): String? {
        return sp.getString(KEY_REFRESH_TOKEN, null)
    }

    fun updateRefreshToken(refreshToken: String?) {
        sp.edit().putString(KEY_REFRESH_TOKEN, refreshToken).apply()
    }

    fun getTokenExpiresAt(): String? {
        return sp.getString(KEY_TOKEN_EXPIRES_AT, null)
    }

    fun updateTokenExpiresAt(expiresAt: String?) {
        sp.edit().putString(KEY_TOKEN_EXPIRES_AT, expiresAt).apply()
    }

    fun saveLoginData(token: String?, refreshToken: String?, expiresAt: String?) {
        sp.edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .putString(KEY_TOKEN_EXPIRES_AT, expiresAt)
            .apply()
    }

    fun clearAuth() {
        sp.edit()
            .remove(KEY_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .remove(KEY_TOKEN_EXPIRES_AT)
            .remove(KEY_USER_EMAIL)
            .remove(KEY_USER_NAME)
            .apply()
    }

    fun savePKCE(verifier: String, state: String) {
        sp.edit()
            .putString(KEY_PKCE_VERIFIER, verifier)
            .putString(KEY_PKCE_STATE, state)
            .apply()
    }

    fun getPKCEVerifier(): String? = sp.getString(KEY_PKCE_VERIFIER, null)
    fun getPKCEState(): String? = sp.getString(KEY_PKCE_STATE, null)

    fun clearPKCE() {
        sp.edit()
            .remove(KEY_PKCE_VERIFIER)
            .remove(KEY_PKCE_STATE)
            .apply()
    }

    // ==================== USER PROFILE ====================
    fun getUserEmail(): String? = sp.getString(KEY_USER_EMAIL, null)
    
    fun updateUserEmail(email: String?) {
        sp.edit().putString(KEY_USER_EMAIL, email).apply()
    }

    fun getUserName(): String? = sp.getString(KEY_USER_NAME, null)
    
    fun updateUserName(name: String?) {
        sp.edit().putString(KEY_USER_NAME, name).apply()
    }

    // ==================== DEVICE ====================
    fun getDeviceUUID(): String? = sp.getString(KEY_UUID, null)

    fun getHardwareUUID(): String {
        var uuid = sp.getString(KEY_HARDWARE_UUID, null)
        if (uuid.isNullOrEmpty()) {
            uuid = generateUUID()
            sp.edit().putString(KEY_HARDWARE_UUID, uuid).apply()
        }
        return uuid
    }

    fun generateUUID(): String {
        return UUID.randomUUID().toString()
    }

    fun updateDeviceUUID(uuid: String) {
        sp.edit().putString(KEY_UUID, uuid).apply()
    }

    // ==================== NETWORK INFO ====================
    fun updateCommunityName(communityName: String) {
        sp.edit().putString(KEY_COMMUNITY_NAME, communityName).apply()
    }

    fun updateSecretKey(secretKey: String) {
        sp.edit().putString(KEY_SECRET_NAME, secretKey).apply()
    }

    fun updateSubnetMask(subnetMask: String) {
        sp.edit().putString(KEY_SUBNET_MASK, subnetMask).apply()
    }

    fun updateVirtualIP(virtualIp: String) {
        sp.edit().putString(KEY_VIRTUAL_IP, virtualIp).apply()
    }

    fun setNetworkInfo(networkData: JoinNetworkData) {
        sp.edit()
            .putString(KEY_COMMUNITY_NAME, networkData.communityName)
            .putString(KEY_SECRET_NAME, networkData.secretKey)
            .putString(KEY_SUBNET_MASK, networkData.subnetMask)
            .putString(KEY_VIRTUAL_IP, networkData.virtualIp)
            .putString(KEY_SERVER_NAME, networkData.server.name)
            .putString(KEY_SERVER_HOST, networkData.server.host)
            .putString(KEY_SERVER_COUNTRY, networkData.server.country)
            .apply()
    }

    fun getLatestJoinedNetworkUUID(): String? = sp.getString(KEY_LATEST_JOINED_NETWORK_UUID, null)

    fun setLatestJoinedNetworkUUID(uuid: String) {
        sp.edit().putString(KEY_LATEST_JOINED_NETWORK_UUID, uuid).apply()
    }

    // ==================== DEVICE INFO ====================
    val osInfo = "${Build.BRAND} ${Build.MODEL} OS:${Build.VERSION.RELEASE}"

    val deviceName: String by lazy {
        Settings.Global.getString(context.contentResolver, "device_name")
            ?: Settings.System.getString(context.contentResolver, "device_name")
            ?: Settings.Secure.getString(context.contentResolver, "bluetooth_name")
            ?: Settings.System.getString(context.contentResolver, "bluetooth_name")
            ?: Settings.Secure.getString(context.contentResolver, "lock_screen_owner_info")
            ?: Build.MODEL
    }
}