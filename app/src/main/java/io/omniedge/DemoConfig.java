package io.omniedge;

/**
 * Created by cypdev@outlook.com
 * <br/> Date: 12/12/20
 * <br/> 技术文档：
 * <br/> 一句话描述：Demo的配置
 */
public class DemoConfig {
    static int version = 1;
    static String netmask = "255.255.255.0";
    static boolean moreSettings = false;
    static String superNodeBackup = "";
    static int mtu = 1386;
    static String localIP = "";
    static int holePunchInterval = 20;
    static boolean resolveSupernodeIP = false;
    static int localPort = 0;
    static boolean allowRouting = false;
    static boolean dropMuticast = true;
    static boolean useHttpTunnel = false;
    static int traceLevel = 4;
    static boolean isSelcected = false;
    static String gatewayIp = "";
    static String dnsServer = "";
    static String encryptionMode = "Twofish";
}
