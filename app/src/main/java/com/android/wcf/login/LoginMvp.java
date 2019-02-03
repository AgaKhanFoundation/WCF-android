package com.android.wcf.login;

import com.android.wcf.base.BaseMvp;

import java.util.List;

public interface LoginMvp {
    interface LoginView extends BaseMvp.BaseView {

         void showMainTabActivity(List<String> userInfo);
         void showGoogleSignInPrompt();
    }
}
