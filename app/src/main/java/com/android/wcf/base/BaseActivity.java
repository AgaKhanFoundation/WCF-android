package com.android.wcf.base;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public abstract class BaseActivity extends AppCompatActivity implements BaseMvp.BaseView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //TODO: implement the proper dialogFragment for showing error messages

    @Override
    public void showError(int messageId) {
        Toast.makeText(this, getString(messageId) , Toast.LENGTH_SHORT).show();
    }


    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showError(Throwable error) {
        showMessage(error.getMessage());
    }

    @Override
    public void showError(String message) {
        showMessage(message);
    }

    @Override
    public void showError(String title, String message) {
        showMessage(message);
    }

    @Override
    public void showError(String title, int messageId) {
        Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showError(int titleId, String message) {
        showMessage(message);
    }

    @Override
    public void showError(int titleId, int messageId) {
        Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show();
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


    @Override
    public void closeKeyboard() {
        View view = view = findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getRootView().getWindowToken(), 0);
        }
    }
}
