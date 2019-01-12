package com.android.wcf.base;


import android.support.v4.app.Fragment;
import android.view.View;

/**
 * A simple {@link Fragment} subclass.
 */
abstract public class BaseFragment extends Fragment implements BaseMvp.BaseView {


    public BaseFragment() {
    }

    @Override
    public void showError(Throwable error) {

    }

    @Override
    public void showError(String message) {

    }

    @Override
    public void showError(String title, String message) {

    }

    @Override
    public void showError(String title, int messageId) {

    }

    @Override
    public void showError(int titleId, String message) {

    }

    @Override
    public void showError(int titleId, int messageId) {

    }

    @Override
    public void showLoadingDialogFragment() {

    }

    @Override
    public void hideLoadingDialogFragment() {

    }

    @Override
    public View showLoadingView() {
        return null;
    }

    @Override
    public void hideLoadingView() {

    }
}
