package com.android.wcf.home;

import com.android.wcf.base.BaseMvp;

import androidx.annotation.StringRes;

public interface HomeMvp {

    interface HomeView extends BaseMvp.BaseView {
        void showErrorAndCloseApp(@StringRes int messageRes);

    }

    interface HomePresenter {

    }
}
