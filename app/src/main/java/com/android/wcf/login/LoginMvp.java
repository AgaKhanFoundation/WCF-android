package com.android.wcf.login;

import com.android.wcf.base.BaseMvp;

public interface LoginMvp {
    interface View extends BaseMvp.BaseView {
        void loginComplete();
    }

    interface Presenter extends BaseMvp.Presenter {
        void onLoginSuccess();

        void onLoginError(String message);

    }

    interface Host {
        void setToolbarTitle(String title, boolean homeAffordance);
        void showToolbar();
        void hideToolbar();

        void showTermsAndConditions();

        void loginComplete();
        void signout(boolean complete);

        void switchServerForTestingTeam();

    }
}
