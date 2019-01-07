package com.android.wcf.base;

import android.view.View;

public interface BaseMvp {
    public interface BaseView {
        public void showError(Throwable error);

        public void showError(String message);

        public void showError(String title, String message);

        public void showError(String title, int messageId);

        public void showError(int titleId, String message);

        public void showError(int titleId, int messageId);

        public void showLoadingDialogFragment();

        public void hideLoadingDialogFragment();

        public View showLoadingView();

        public void hideLoadingView();

    }
}
