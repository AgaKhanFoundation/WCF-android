package com.android.wcf.login;

import android.util.Log;

public class LoginPresenter extends WCFActivityPresenter<LoginMvp.LoginView> {
  private static final String TAG = LoginPresenter.class.getSimpleName();

  private LoginMvp.LoginView view;

  public LoginPresenter(LoginMvp.LoginView LoginView) {
    this.view = LoginView;
  }

  public void onLoginSuccess() {
    Log.i(TAG, "Login Success: Showing Main Tab");
    view.showMainTabActivity();
  }

  public void onLoginError() {
    view.showMessage("Something went wrong. Please try again!");
  }
}
