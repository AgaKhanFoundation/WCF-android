package com.android.wcf.login;
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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.wcf.R;
import com.android.wcf.application.DataHolder;
import com.android.wcf.application.WCFApplication;
import com.android.wcf.base.BaseActivity;
import com.android.wcf.facebook.FacebookHelper;
import com.android.wcf.helper.SharedPreferencesUtil;
import com.android.wcf.home.HomeActivity;
import com.android.wcf.model.Constants;
import com.android.wcf.model.Participant;
import com.android.wcf.onboard.OnboardActivity;
import com.android.wcf.web.WebViewFragment;

public class LoginActivity extends BaseActivity implements LoginActivityMvp.View, LoginMvp.Host {
    private static final String TAG = LoginActivity.class.getSimpleName();

    private Toolbar toolbar;
    LoginActivityMvp.Presenter loginPesenter;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginPesenter = new LoginActivityPresenter(this);

        setupView();

        showLoginView();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment instanceof LoginFragment) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager sfm = getSupportFragmentManager();

        if (sfm.getBackStackEntryCount() > 0) {
            Fragment fragment = sfm.findFragmentById(R.id.fragment_container);
            if (fragment instanceof LoginFragment) {
                finish();
            }
            else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void showToolbar() {
        toolbar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideToolbar() {
        toolbar.setVisibility(View.GONE);
    }

    private void setupView() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    @Override
    public void showTermsAndConditions() {
        Fragment fragment = WebViewFragment.getInstance(getString(R.string.terms_url), getString(R.string.term_title));
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    protected void restartApp() {
        SharedPreferencesUtil.clearMyLogin();
        WCFApplication.instance.restartApp();
        finish();
    }

    @Override
    public void signout(boolean complete) {
        DataHolder.clearCache();
        if (complete) {
            SharedPreferencesUtil.clearAll();
        }
        else {
            SharedPreferencesUtil.clearMyLogin();
            SharedPreferencesUtil.clearMyTeamId();
        }
        FacebookHelper.logout();
        restartApp();
    }
    
    public void showLoginView() {
        Fragment fragment = new LoginFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean isOnboardingComplete() {
        return !SharedPreferencesUtil.getShowOnboardingTutorial();
    }

    @Override
    public void showHomeActivity() {
        Intent intent = HomeActivity.createIntent(this);
        this.startActivity(intent);
    }

    @Override
    public void showOnboarding() {
        Intent intent = OnboardActivity.createIntent(this);
        this.startActivity(intent);
    }

    @Override
    public void loginComplete() {
        loginPesenter.loginComplete();
    }

    @Override
    public void switchServerForTestingTeam() {
        Log.d(TAG, "Click switchServerForTestingTeam()");

        WCFApplication.switchServerForTestingTeam();
        signout(false);
    }
}
