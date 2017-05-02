package com.android.wcf.utils;

import android.util.Log;

public class Debug {

    public static void verbose(boolean debug, String tag, String msg) {
        if (debug) Log.v(tag, msg);
    }

    public static void error(boolean debug, String tag, String msg) {
        if (debug) Log.e(tag, msg);
    }

    public static void warnings(boolean debug, String tag, String msg) {
        if (debug) Log.w(tag, msg);
    }

    public static void debug(boolean debug, String tag, String msg) {
        if (debug) Log.d(tag, msg);
    }

    public static void info(boolean debug, String tag, String msg) {
        if (debug) Log.i(tag, msg);
    }
}
