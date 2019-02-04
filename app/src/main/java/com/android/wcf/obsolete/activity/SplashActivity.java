package com.android.wcf.obsolete.activity;
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

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.android.wcf.R;
import com.android.wcf.permissions.ApplicationPermission;
import com.android.wcf.utils.AppUtil;
import com.android.wcf.utils.Debug;
import com.android.wcf.utils.Preferences;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private static boolean DEBUG = com.android.wcf.utils.Build.DEBUG;
    private ApplicationPermission mApplicationPermission;
    private List<String> mPermissionList;
    private final int SPLASH_TIMER = 3000;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mContext = this;
        addPermission();
        if (mApplicationPermission.checkAllPermission()) {
            setSplashDelay();
        } else {
            ActivityCompat.requestPermissions(this, mApplicationPermission.requestPermission(null), AppUtil.PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        try {
            switch (requestCode) {
                case AppUtil.PERMISSION_REQUEST_CODE:
                    ArrayList<String> reqPermissionList = new ArrayList<String>();
                    for (int i = 0; i < permissions.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                            Debug.info(DEBUG, "Application Permission", permissions[i] + " Permission not granted.");
                            if (shouldShowRequestPermissionRationale(permissions[i]))
                                reqPermissionList.add(permissions[i]);
                        }
                    }
                    if (reqPermissionList.size() == 0) {
                        setSplashDelay();
                    } else {
                        ActivityCompat.requestPermissions(this, mApplicationPermission.requestPermission(reqPermissionList), AppUtil.PERMISSION_REQUEST_CODE);
                    }
            }
        } catch (Exception e) {
            Debug.error(DEBUG, "SplashActivity", "Exception::" + e.getMessage());
        }
    }

    private void setSplashDelay() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Preferences.getPreferencesBoolean("isUserLoggedIn", mContext)) {
                    startApp(MainTabActivity.class);
                } else {
                    startApp(LoginActivity.class);
                }
                finish();
            }
        }, SPLASH_TIMER);
    }

    private void startApp(Class<?> cl) {
        Intent intent = new Intent(SplashActivity.this, cl);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        SplashActivity.this.startActivity(intent);
        SplashActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        mApplicationPermission = null;
        mPermissionList = null;
        super.onDestroy();
    }

    private void addPermission() {
        mPermissionList = new ArrayList<>();
        mPermissionList.add(Manifest.permission.INTERNET);
        mPermissionList.add(Manifest.permission.ACCESS_NETWORK_STATE);
        mPermissionList.add(Manifest.permission.ACCESS_WIFI_STATE);
        mPermissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        mPermissionList.add(Manifest.permission.RECEIVE_BOOT_COMPLETED);
        mPermissionList.add(Manifest.permission.CALL_PHONE);
        mApplicationPermission = new ApplicationPermission(getApplicationContext());
        mApplicationPermission.setPermissionList(mPermissionList);
    }
}
