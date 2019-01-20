package com.android.wcf.base;

import android.view.View;

public interface BaseMvp {
    interface BaseView {

        void showMessage(String message);

        void showError(Throwable error);

        void showError(String message);

        void showError(String title, String message);

        void showError(String title, int messageId);

        void showError(int titleId, String message);

        void showError(int titleId, int messageId);

        void showLoadingDialogFragment();

        void hideLoadingDialogFragment();

        View showLoadingView();

        void hideLoadingView();

        void closeKeyboard();

    }

    interface Presenter {
        String getTag();
    }
}
