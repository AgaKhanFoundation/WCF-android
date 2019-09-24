package com.android.wcf.onboard;

import java.util.List;

public interface OnboardMvp {
    interface View {
        void showWelcomeView();

        void showOnboardingTutorial(List<OnboardTutorialItem> stepsData);

        OnboardTutorialItem loadOnboardingStepFromResource(int step);

        void showHomeActivity();

        CharSequence generateSkipLinkText();

        void onCompleteTutorial();

    }

    interface Presenter {
        void onCreate();

        void onStartOnboardingTutorial();

        void onSkipOnboardingTutorial();

        void onCompleteTutorial();

    }
}
