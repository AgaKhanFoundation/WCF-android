package com.android.wcf.splash;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.StringRes;
import androidx.core.app.ActivityCompat;

import com.android.wcf.R;
import com.android.wcf.base.BaseActivity;
import com.android.wcf.helper.SharedPreferencesUtil;
import com.android.wcf.home.HomeActivity;
import com.android.wcf.login.LoginActivity;
import com.android.wcf.model.Event;
import com.android.wcf.onboard.OnboardActivity;
import com.android.wcf.permissions.ApplicationPermission;
import com.android.wcf.utils.AppUtil;
import com.android.wcf.utils.Debug;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends BaseActivity implements SplashMvp.SplashView {

    private static boolean DEBUG = com.android.wcf.utils.Build.DEBUG;
    private ApplicationPermission mApplicationPermission;
    private List<String> mPermissionList;
    private final int SPLASH_TIMER = 3000;
    private Context mContext;
    private SplashPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mContext = this;
        presenter = new SplashPresenter(this);
        addPermission();
        if (mApplicationPermission.checkAllPermission()) {
            setSplashDelay();
        } else {
            ActivityCompat.requestPermissions(this, mApplicationPermission.requestPermission(null), AppUtil.PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public boolean isAttached() {
        return !isDestroyed() && !isFinishing();
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
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (shouldShowRequestPermissionRationale(permissions[i]))
                                    reqPermissionList.add(permissions[i]);
                            }
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

    @Override
    public void navigateToHomeView(Event event) {

        //For V1 of the app, we will assign the users to the first event
        //later we will provide UI to allow users to select from a list

        int currentEventId = SharedPreferencesUtil.getMyActiveEventId();
        if (currentEventId == SharedPreferencesUtil.DEFAULT_ACTIVE_EVENT_ID || currentEventId != event.getId()) {
            SharedPreferencesUtil.saveMyActiveEvent(event.getId());
        }

        if (!SharedPreferencesUtil.isUserLoggedIn()) {
            startApp(LoginActivity.class);
        } else if (SharedPreferencesUtil.getShowOnboardingTutorial()) {
            startApp(OnboardActivity.class);
        } else {
            startApp(HomeActivity.class);
        }
    }

    @Override
    public void showErrorAndCloseApp(@StringRes int messageId) {
        showError(messageId);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, SPLASH_TIMER);
    }

    private void setSplashDelay() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                presenter.getEventsList();
            }
        }, SPLASH_TIMER);
    }

    private void startApp(Class<?> activityClass) {
        Intent intent = new Intent(SplashActivity.this, activityClass);
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