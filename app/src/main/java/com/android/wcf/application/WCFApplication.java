package com.android.wcf.application;
/**
 * Copyright © 2017 Aga Khan Foundation
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p>
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * <p>
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p>
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * <p>
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

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.android.wcf.BuildConfig;
import com.android.wcf.R;
import com.android.wcf.helper.DistanceConverter;
import com.android.wcf.helper.DistanceMetric;
import com.android.wcf.home.HomeActivity;
import com.android.wcf.login.LoginActivity;
import com.android.wcf.network.WCFClient;
import com.android.wcf.splash.SplashActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class WCFApplication extends Application {

    public static WCFApplication instance;
    public static final String TAG = WCFApplication.class.getSimpleName();
    private static boolean serverSwitched = false;

    @Override
    public void onCreate() {
        super.onCreate();
        DistanceConverter.setDefaultMetrics(DistanceMetric.MILES); //TODO this can be driven by Locale
        PreferenceManager.setDefaultValues(this, R.xml.settings_preferences_root, false);
        instance = this;

    }

    public Application getInstance() {
        return instance;
    }

    public void requestLogin() {
        Intent intent = LoginActivity.createIntent(this);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        instance.startActivity(intent);
    }

    public void openHomeActivity() {
        Intent intent = HomeActivity.createIntent(this);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        instance.startActivity(intent);
    }

    public void restartApp() {
        Intent intent = SplashActivity.createIntent(this);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        instance.startActivity(intent);
    }

    public static void  switchServerForTestingTeam() {
        Log.d(TAG, "switchServerForTestingTeam()");
        serverSwitched = true;
        WCFClient.switchServerForTestingTeam();

    }

    public static boolean isProdBackend() {
        return WCFClient.isProdBackend();
    }

    public String getAppVersion() {
      return ( "v" + BuildConfig.VERSION_NAME
              + ( WCFClient.isProdBackend() ? "-p" : "-t")
              + (serverSwitched ? " -switched" : "")
      );
    }

    public String getHashKey() {
        // Add code to print out the key hash

        try {
            final PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                final MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                final String fbHashKey = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.d(TAG, "fbHashKey:" + fbHashKey);
                return fbHashKey;
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "hashKey package error:" + e.getMessage());

        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "hashKey algo error:" + e.getMessage());

        }
        return "Unknown";
    }

}
