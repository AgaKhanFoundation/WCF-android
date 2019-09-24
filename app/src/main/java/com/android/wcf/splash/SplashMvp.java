package com.android.wcf.splash;

import com.android.wcf.base.BaseMvp;
import com.android.wcf.model.Event;

import androidx.annotation.IdRes;
import androidx.annotation.StringRes;

public interface SplashMvp {

    interface SplashView extends BaseMvp.BaseView {

        void navigateToHomeView(Event event);

        void showErrorAndCloseApp(@StringRes int messageRes);
    }

    interface SplashPresenter {

    }
}
