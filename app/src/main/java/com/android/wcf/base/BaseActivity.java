package com.android.wcf.base;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public abstract class BaseActivity extends AppCompatActivity implements BaseMvp.BaseView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
