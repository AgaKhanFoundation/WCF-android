package com.android.wcf.onboard;

public interface OnboardMvp {
    interface View {
        void showWelcomeView();
        void startOnboarding();

        void showHomeActivity();
    }

    interface Presenter {
        void onCreate();

        void onStartOnboardingTutorial();

        void onSkipOnboardingTutorial();
    }
}
