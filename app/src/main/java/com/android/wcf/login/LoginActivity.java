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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.wcf.R;
import com.android.wcf.base.BaseActivity;
import com.android.wcf.helper.SharedPreferencesUtil;
import com.android.wcf.home.HomeActivity;
import com.android.wcf.onboard.OnboardActivity;
import com.android.wcf.utils.Preferences;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

public class LoginActivity extends BaseActivity implements LoginMvp.LoginView {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private Context mContext;
    private LoginPresenter presenter;
    private CallbackManager callbackManager;

    LoginButton loginButton;

    public static Intent createIntent(Context context) {
        Intent intent =  new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
        callbackManager = CallbackManager.Factory.create();
        presenter = new LoginPresenter(this);
        setupView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void setupView() {
        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile", "email");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("LoginPresenter", "onSuccess called");
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                String userName = "", userId = "", userEmail = "", userGender = "", userProfileUrl = "";
                                Log.e("response: ", response + "");
                                try {
                                    AccessToken token = AccessToken.getCurrentAccessToken();
                                    Log.d("access only Token is", String.valueOf(token.getToken()));

                                    userId = object.getString("id");
                                    userEmail = object.getString("email");
                                    userProfileUrl = response.getJSONObject().getJSONObject("picture").getJSONObject("data").getString("url");

                                    SharedPreferencesUtil.saveMyFacebookId(userId);
                                    SharedPreferencesUtil.saveUserLoggedIn(true);
                                    SharedPreferencesUtil.saveUserFullName(userName);
                                    SharedPreferencesUtil.saveUserEmail(userEmail);
                                    SharedPreferencesUtil.saveUserFbProfileUrl(userProfileUrl);

                                    presenter.onLoginSuccess();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,birthday,cover,picture.type(large)");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel called");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "onError");
                presenter.onLoginError();
            }
        });
    }

    @Override
    public void showMessage(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
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
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}

