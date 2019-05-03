package com.android.wcf.login;

import com.android.wcf.base.BaseMvp;

public interface LoginMvp {
    interface LoginView extends BaseMvp.BaseView {

         void showHomeActivity();

         void showOnboarding();

         boolean isOnboardingComplete();

    }
}