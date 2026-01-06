package io.omniedge

import android.text.TextUtils
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Created on 2019-12-25 23:45
 * 
 * Shell command execution utilities (replacing ShellUtils from utilcodex)
 */

private val toRegex = "time=\\S* ms".toRegex()

/**
 * Execute a shell command and return the result
 */
private fun execCmd(command: String): ShellResult {
    return try {
        val process = Runtime.getRuntime().exec(command)
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        val errorReader = BufferedReader(InputStreamReader(process.errorStream))
        
        val output = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            output.append(line).append("\n")
        }
        
        val errorOutput = StringBuilder()
        while (errorReader.readLine().also { line = it } != null) {
            errorOutput.append(line).append("\n")
        }
        
        val exitCode = process.waitFor()
        reader.close()
        errorReader.close()
        
        ShellResult(
            exitCode = exitCode,
            successMsg = output.toString().trim(),
            errorMsg = errorOutput.toString().trim()
        )
    } catch (e: Exception) {
        ShellResult(exitCode = -1, successMsg = "", errorMsg = e.message ?: "Unknown error")
    }
}

private data class ShellResult(
    val exitCode: Int,
    val successMsg: String,
    val errorMsg: String
)

fun ping(ip: String): String {
    val command = String.format("ping -c 1 %s", ip)
    val logMsg = StringBuilder().apnd("PingUtils#ping cmd=$command")
    OmniLog.d(logMsg.toString())

    val start = System.currentTimeMillis()
    val result = execCmd(command)
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