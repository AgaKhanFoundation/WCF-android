package com.android.wcf.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Malik Khoja on 4/27/2017.
 */

public class Preferences {

    private static String PREF_NAME = "WCF";
    private static SharedPreferences.Editor editor;
    private static SharedPreferences shared_pref;

    public static void setPreferencesBoolean(String key, boolean value, Context mContext) {
        if (mContext != null) {
            shared_pref = mContext.getSharedPreferences(PREF_NAME,
                    Context.MODE_PRIVATE);
            editor = shared_pref.edit();
            editor.putBoolean(key, value);
            editor.commit();
        }
    }

    public static boolean getPreferencesBoolean(String key, Context mContext) {
        shared_pref = mContext.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        return shared_pref.getBoolean(key, false);
    }

    public static void setPreferencesString(String key, String value, Context mContext) {
        if (mContext != null) {
            shared_pref = mContext.getSharedPreferences(PREF_NAME,
                    Context.MODE_PRIVATE);
            editor = shared_pref.edit();
            editor.putString(key, value);
            editor.commit();
        }
    }

    public static String getPreferencesString(String key, Context mContext) {
        shared_pref = mContext.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        return shared_pref.getString(key, "");
    }
}
