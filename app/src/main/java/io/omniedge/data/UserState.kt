package io.omniedge.data

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import io.omniedge.SingletonHolder

/**
 * 用户相关信息
 * Created on 2019-12-17 13:13
 *
 */
class UserState private constructor(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.applicationContext.getSharedPreferences("user_state", Context.MODE_PRIVATE)

    companion object {
        private val instance = SingletonHolder<UserState, Context> { context ->
            return@SingletonHolder UserState(context)
        }

        fun getInstance(context: Context): UserState {
            return instance.getInstance(context)
        }

        private const val KEY_USER_DATA = "key_user_data"

        private const val KEY_TOKEN = "key_token"

        private const val KEY_EMAIL = "key_email"

        private const val KEY_SERVER_IP = "key_server_ip"
        private const val KEY_SERVER_PORT = "key_server_port"
        private const val KEY_SERVER_NAME = "key_server_name"
        private const val KEY_TAP_IP = "key_tap_ip"
    }

    fun isLogin(): Boolean {
        return !TextUtils.isEmpty(sharedPreferences.getString(KEY_TOKEN, ""))
    }

    fun getUserData(): String {
        return sharedPreferences.getString(KEY_USER_DATA, "")!!
    }

    fun getToken(): String {
        return sharedPreferences.getString(KEY_TOKEN, "")!!
    }

    fun putToken(token: String) {
        sharedPreferences.edit().putString(KEY_TOKEN, token).apply()
    }

    fun putServerIp(serverIp: String) {
        sharedPreferences.edit().putString(KEY_SERVER_IP, serverIp).apply()
    }

    fun getServerIp(): String? {
        return sharedPreferences.getString(KEY_SERVER_IP, null)
    }

    fun putServerPort(serverPort: String) {
        sharedPreferences.edit().putString(KEY_SERVER_PORT, serverPort).apply()
    }

    fun getServerPort(): String? {
        return sharedPreferences.getString(KEY_SERVER_PORT, null)
    }

    fun putServerName(serverName: String) {
        sharedPreferences.edit().putString(KEY_SERVER_NAME, serverName).apply()
    }

    fun getServerName(): String? {
        return sharedPreferences.getString(KEY_SERVER_NAME, null)
    }

    fun putTapIp(tapIp: String) {
        sharedPreferences.edit().putString(KEY_TAP_IP, tapIp).apply()
    }

    fun getTapIp(): String? {
        return sharedPreferences.getString(KEY_TAP_IP, null)
    }

    fun putEmailAndPassword(email: String) {
        sharedPreferences.edit()
            .putString(KEY_EMAIL, email)
            .apply()
    }

    fun getEmail(): String {
        return sharedPreferences.getString(KEY_EMAIL, "")!!
    }


}