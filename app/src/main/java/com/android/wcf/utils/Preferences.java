package com.android.wcf.utils;
/**
 * Copyright © 2017 Aga Khan Foundation
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/
import android.content.Context;
import android.content.SharedPreferences;

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
