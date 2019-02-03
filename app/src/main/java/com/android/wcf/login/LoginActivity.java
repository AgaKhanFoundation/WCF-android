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
import android.view.View;

import com.android.wcf.R;
import com.android.wcf.activity.MainTabActivity;
import com.android.wcf.base.BaseActivity;
import com.android.wcf.utils.Preferences;
import com.facebook.login.widget.LoginButton;

import java.util.List;

public class LoginActivity extends BaseActivity implements LoginMvp.LoginView {
  private static final int SIGNED_IN = 0;
  private static final int STATE_SIGNING_IN = 1;
  private static final int STATE_IN_PROGRESS = 2;
  private static final int RC_SIGN_IN = 0;

  private Context mContext;
  LoginButton loginButton;
  private LoginPresenter presenter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    mContext = this;
    presenter = new LoginPresenter(this);
    setupView();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    presenter.onActivityResult(requestCode, resultCode, data);
  }

  private void setupView() {
    loginButton = findViewById(R.id.login_button);
    loginButton.setReadPermissions("public_profile", "email");
    loginButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        presenter.onFbLoginPressed();

      }
    });
  }

  @Override
  public void showMessage(String message) {

  }

  @Override
  public void showMainTabActivity(List<String> userInfo) {
    Preferences.setPreferencesBoolean("isUserLoggedIn", true, this);
    Preferences.setPreferencesString("userName", userInfo.get(0), mContext);
    Preferences.setPreferencesString("userId", userInfo.get(1), mContext);
    Preferences.setPreferencesString("userEmail", userInfo.get(2), mContext);
    Preferences.setPreferencesString("userGender", userInfo.get(3), mContext);
    Preferences.setPreferencesString("userProfileUrl", userInfo.get(4), mContext);
    Intent intent = new Intent(LoginActivity.this, MainTabActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    this.startActivity(intent);
  }

  @Override
  public void showGoogleSignInPrompt() {
    //TODO : set view for google sign in
//    Intent signInIntent = googleSignInClient.getSignInIntent();
//    startActivityForResult(signInIntent, RC_SIGN_IN);
  }

  @Override
  public void onPointerCaptureChanged(boolean hasCapture) {

  }
}

