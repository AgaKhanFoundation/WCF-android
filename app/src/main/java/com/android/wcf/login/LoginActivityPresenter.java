package com.android.wcf.login;

import android.util.Log;

public class LoginActivityPresenter implements LoginActivityMvp.Presenter {
    private static final String TAG = LoginActivityPresenter.class.getSimpleName();

    private LoginActivityMvp.View view;

    public LoginActivityPresenter(LoginActivityMvp.View LoginView) {
        this.view = LoginView;
    }

    @Override
    public void loginComplete() {

        if (view.isOnboardingComplete()) {
            Log.d(TAG, "Login Success: Showing HomeActivity");
            view.showHomeActivity();
        } else {
            Log.d(TAG, "Login Success: Showing Onboarding");
            view.showOnboarding();
        }
    }
}
