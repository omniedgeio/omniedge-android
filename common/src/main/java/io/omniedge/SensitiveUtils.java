package io.omniedge;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Random;

public class SensitiveUtils {
    private static final String DEFAULT_MAC_ADDRESS = "02:00:00:00:00:00";

    private static final Singleton<String> sWifiMacAddress = new Singleton<String>() {
        protected String create(Object... param) {
            String wifiMacAddressInternal = getWifiMacAddressInternal((Context) param[0]);
            if (DEFAULT_MAC_ADDRESS.equals(wifiMacAddressInternal)) {
                wifiMacAddressInternal = "";
            }
            return wifiMacAddressInternal;
        }
    };

    public static String randomMACAddress() {
        Random rand = new Random();
        byte[] macAddr = new byte[6];
        rand.nextBytes(macAddr);

        macAddr[0] = (byte) (macAddr[0] & (byte) 254);  //zeroing last 2 bytes to make it unicast and locally adminstrated

        StringBuilder sb = new StringBuilder(18);
        for (byte b : macAddr) {

            if (sb.length() > 0)
                sb.append(":");

            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }

    public static String getMacAddress(Context context) {
        return sWifiMacAddress.get(context);
    }

    @SuppressLint("HardwareIds")
    private static String getWifiMacAddressInternal(Context context) {
        String macAddress = null;
        WifiInfo wifiInfo = getWifiInfo(context);
        if (wifiInfo != null) {
            macAddress = wifiInfo.getMacAddress();
        }
        if (!DEFAULT_MAC_ADDRESS.equals(macAddress) && !TextUtils.isEmpty(macAddress)) {
            return macAddress;
        }
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            String name = getNetworkInterfaceName();
            while (interfaces.hasMoreElements()) {
                NetworkInterface netWork = interfaces.nextElement();
                if (netWork.getName().equals(name)) {
                    byte[] bytes = netWork.getHardwareAddress();
                    if (bytes != null && bytes.length != 0) {
                        StringBuilder builder = new StringBuilder();
                        for (byte b : bytes) {
                            builder.append(String.format("%02X:", b));
                        }
                        if (builder.length() > 0) {
                            builder.deleteCharAt(builder.length() - 1);
                        }
                        return builder.toString();
                    }
                }
            }
            return macAddress;
        } catch (Exception e) {
            OmniLog.w(e.getMessage(), e);
            return macAddress;
        }
    }

    private static String getNetworkInterfaceName() {
        try {
            @SuppressLint("PrivateApi")
            Class<?> clazz = Class.forName("android.os.SystemProperties");
            return (String) clazz.getMethod("get", new Class[]{String.class, String.class})
                    .invoke(clazz, new Object[]{"wifi.interface", "wlan0"});
        } catch (Exception e) {
            OmniLog.w(e.getMessage(), e);
            return "wlan0";
        }
    }

    private static WifiInfo getWifiInfo(Context context) {
        if (context == null) {
            return null;
        }
        try {
            Context appContext = context.getApplicationContext();
            WifiInfo wifiInfo = ((WifiManager) appContext.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
            ConnectivityManager connectivityManager = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager.getNetworkInfo(1).getState() != NetworkInfo.State.CONNECTED
                    || wifiInfo.getSSID() == null) {
                return null;
            }
            return wifiInfo;
        } catch (Throwable e) {
            OmniLog.w(e.getMessage(), e);
        }
        return null;
    }
}