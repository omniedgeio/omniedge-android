package io.omniedge

import android.text.TextUtils
import com.blankj.utilcode.util.ShellUtils

/**
 * Created on 2019-12-25 23:45
 */

private val toRegex = "time=\\S* ms".toRegex()

fun ping(ip: String): String {
    val command = String.format("ping -c 1 %s", ip)
    val logMsg = StringBuilder().apnd("PingUtils#ping cmd=$command")
    OmniLog.d(logMsg.toString())

    val start = System.currentTimeMillis()
    val result = ShellUtils.execCmd(command, false)
    val timeValue = if (!TextUtils.isEmpty(result.successMsg)) {
        val split = toRegex.find(result.successMsg)?.value?.split("=")
        split?.get(1).also { logMsg.insrt("success") }
    } else ""

    logMsg.apnd("takeMills=${System.currentTimeMillis() - start}")
        .apnd("timeValue=$timeValue")
    OmniLog.d(logMsg.toString())

    return timeValue ?: ""
}

fun warmUpPingProcess() {
    ThreadUtils.execute {
        val ping: String = try {
            ping("8.8.8.8")
        } catch (e: Throwable) { // 跨进程调用
            ""
        }
        OmniLog.d("PingUtils#warmUpPingProcess ping=$ping")
    }
}