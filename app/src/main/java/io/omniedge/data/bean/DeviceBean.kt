package io.omniedge.data.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created on 2019-12-22 14:05
 *
 */
@Parcelize
data class DeviceBean(
    val name: String?,
    val ip: String?,
    val port: String?,
    var time: String? = "0ms"
) :
    Parcelable {
}