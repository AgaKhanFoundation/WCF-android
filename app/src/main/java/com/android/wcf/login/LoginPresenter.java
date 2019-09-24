package com.android.wcf.login;

public class LoginPresenter implements LoginMvp.Presenter {
    private static final String TAG = LoginPresenter.class.getSimpleName();

    private LoginMvp.View view;

    public LoginPresenter(LoginMvp.View loginView) {
        this.view = loginView;
    }

    public void onLoginSuccess() {
        view.loginComplete();
    }

    public void onLoginError(String message) {
        if (message != null && !message.isEmpty())
            view.showMessage(message);
        else
            view.showMessage("Something went wrong. Please try again!");
    }

    @Override
    public String getTag() {
        return null;
    }

    @Override
    public void onStop() {

    }
}

