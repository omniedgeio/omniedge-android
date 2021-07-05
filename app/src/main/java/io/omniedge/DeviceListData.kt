package io.omniedge

import com.amplifyframework.datastore.generated.model.Device
import com.amplifyframework.datastore.generated.model.VirtualNetwork
import io.omniedge.n2n.model.N2NSettingModel
import org.json.JSONObject

/**
Created by chenyouping@bytedance.com
<br/> Date: 1/31/21
<br/> 技术文档：
<br/> 一句话描述：
<br/>
<pre> 原理：
</pre>
 */
data class DeviceListData(
    val virtualNetwork: VirtualNetwork,
    val joinVirtualNetworkResponse: JoinVirtualNetworkResponse,
    val deviceList: List<Device?>
)

data class JoinVirtualNetworkResponse(
    val instanceID: String,
    val virtualNetworkID: String,
    val communityName: String,
    val secretKey: String,
    val addr: String,
    val publicKey: String,
    val virtualIP: String
)

fun JoinVirtualNetworkResponse?.isValid(): Boolean {
    return this != null && communityName.isNotEmpty() && virtualIP.isNotEmpty()
            && secretKey.isNotEmpty()
}

fun DeviceListData.toN2NSettingModel(id: Long, name: String, macAddress: String): N2NSettingModel {
    return N2NSettingModel(
        id,
        DemoConfig.version,
        name,
        joinVirtualNetworkResponse.virtualIP,
        DemoConfig.netmask,
        joinVirtualNetworkResponse.communityName,
        joinVirtualNetworkResponse.secretKey,
        DemoConfig.superNode,
        DemoConfig.moreSettings,
        DemoConfig.superNodeBackup,
        macAddress,
        DemoConfig.mtu,
        DemoConfig.localIP,
        DemoConfig.holePunchInterval,
        DemoConfig.resoveSupernodeIP,
        DemoConfig.localPort,
        DemoConfig.allowRouting,
        DemoConfig.dropMuticast,
        DemoConfig.useHttpTunnel,
        DemoConfig.traceLevel,
        DemoConfig.isSelcected,
        DemoConfig.gatewayIp,
        DemoConfig.dnsServer,
        DemoConfig.encryptionMode
    )
}

fun JSONObject?.toJoinVirtualNetworkResponse(): JoinVirtualNetworkResponse? {
    return this?.let {
        JoinVirtualNetworkResponse(
            it.optString("instanceID"),
            it.optString("virtualNetworkID"),
            it.optString("communityName"),
            it.optString("secretKey"),
            it.optString("addr"),
            it.optString("publicKey"),
            it.optString("virtualIP")
        )
    }
}