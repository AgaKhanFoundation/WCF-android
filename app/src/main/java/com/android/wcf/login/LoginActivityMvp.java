package com.android.wcf.login;

import com.android.wcf.base.BaseMvp;

public interface LoginActivityMvp {
    interface View extends BaseMvp.BaseView {

        void showHomeActivity();

        void showOnboarding();

        boolean isOnboardingComplete();

        void showLoginView();

    }

    interface Presenter {
        void  loginComplete();

    }
}
