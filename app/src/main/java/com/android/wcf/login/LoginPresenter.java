package com.android.wcf.login;

import android.util.Log;

public class LoginPresenter implements LoginMvp.Presenter {
    private static final String TAG = LoginPresenter.class.getSimpleName();

    private LoginMvp.LoginView view;

    public LoginPresenter(LoginMvp.LoginView LoginView) {
        this.view = LoginView;
    }

    public void onLoginSuccess() {
        if (view.isOnboardingComplete()) {
            Log.d(TAG, "Login Success: Showing HomeActivity");
            view.showHomeActivity();
        } else {
            Log.d(TAG, "Login Success: Showing Onboarding");
            view.showOnboarding();

        }
    }

    public void onLoginError(String message) {
        if (message != null && !message.isEmpty())
            view.showMessage(message);
        else
            view.showMessage("Something went wrong. Please try again!");
    }
}

