package io.omniedge;

import android.util.Log;

public class OmniLog {
    public static boolean DEBUG = true;
    //    public static final int SDK_VERSION_CODE = LocalConstants.getVersionCode();
    private static final String TAG = "OmniLog";
//    private static final String USNP = "U SHALL NOT PASS!";

    private static ILogger logger = new ILogger() {
        public void v(String msg, Throwable t) {
            Log.v(OmniLog.TAG, msg, t);
        }

        public void d(String msg, Throwable t) {
            Log.d(OmniLog.TAG, msg, t);
        }

        public void w(String msg, Throwable t) {
            Log.w(OmniLog.TAG, msg, t);
        }

        public void i(String msg, Throwable t) {
            Log.i(OmniLog.TAG, msg, t);
        }

        public void e(String msg, Throwable t) {
            Log.e(OmniLog.TAG, msg, t);
        }
    };

    static void setLogger(ILogger logger2) {
        logger = logger2;
    }

    public static void d(String msg) {
        logger.d(msg, (Throwable) null);
    }

    public static void w(String msg, Throwable t) {
        logger.w(msg, t);
    }

    public static void e(String msg, Throwable t) {
        logger.e(msg, t);
    }

    public static void i(String msg, Throwable t) {
        logger.i(msg, t);
    }

    public static void v(String msg, Throwable t) {
        logger.v(msg, t);
    }

    public static void ysnp(Throwable t) {
        logger.e((String) null, t);
    }

    public static boolean debug() {
        return DEBUG;
    }

    public static void v(String msg) {
        logger.v(msg, (Throwable) null);
    }

    public static void e(String msg) {
        logger.e(msg, (Throwable) null);
    }
}