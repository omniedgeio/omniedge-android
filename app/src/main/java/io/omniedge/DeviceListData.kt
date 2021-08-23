package io.omniedge

import io.omniedge.data.bean.JoinNetworkData
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

fun JoinNetworkData.toN2NSettingModel(id: Long, name: String, macAddress: String) = N2NSettingModel(
    id,
    DemoConfig.version,
    name,
    virtualIp,
    subnetMask,
    communityName,
    secretKey,
    server.host,
    DemoConfig.moreSettings,
    DemoConfig.superNodeBackup,
    macAddress,
    DemoConfig.mtu,
    DemoConfig.localIP,
    DemoConfig.holePunchInterval,
    DemoConfig.resolveSupernodeIP,
    DemoConfig.localPort,
    DemoConfig.allowRouting,
    DemoConfig.dropMuticast,
    DemoConfig.useHttpTunnel,
    DemoConfig.traceLevel,
    DemoConfig.isSelcected,
    DemoConfig.gatewayIp,
    DemoConfig.dnsServer,
    DemoConfig.encryptionMode,
)