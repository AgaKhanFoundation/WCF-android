package com.android.wcf.onboard;

public class OnboardPresenter implements OnboardMvp.Presenter {

    OnboardMvp.View mView;

    public OnboardPresenter(OnboardMvp.View view) {
        mView = view;
    }

    @Override
    public void onCreate() {
        mView.showWelcomeView();

    }

    @Override
    public void onStartOnboardingTutorial() {

    }

    @Override
    public void onSkipOnboardingTutorial() {
        mView.showHomeActivity();

    }
}
