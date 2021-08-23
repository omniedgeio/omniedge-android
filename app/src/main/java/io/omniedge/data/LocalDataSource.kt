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
    }

    private val sp = context.getSharedPreferences(LOCAL_SP, Context.MODE_PRIVATE)

    fun getToken(): String? {
        return sp.getString(KEY_TOKEN, null)
    }

    fun updateToken(token: String?) {
        sp.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getDeviceUUID(): String? = sp.getString(KEY_UUID, null)

    fun getHardwareUUID(): String {
        var uuid = sp.getString(KEY_HARDWARE_UUID, null)
        if (uuid.isNullOrEmpty()) {
            uuid = UUID.randomUUID().toString()
            sp.edit().putString(KEY_HARDWARE_UUID, uuid).apply()
        }
        return uuid
    }

    fun updateDeviceUUID(uuid: String) {
        sp.edit().putString(KEY_UUID, uuid).apply()
    }

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