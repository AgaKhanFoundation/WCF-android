package com.android.wcf.onboard;

import java.util.ArrayList;
import java.util.List;

public class OnboardPresenter implements OnboardMvp.Presenter {

    OnboardMvp.View mView;
    int stepsCount = 3;

    public OnboardPresenter(OnboardMvp.View view) {
        mView = view;
    }

    @Override
    public void onCreate() {
        mView.showWelcomeView();

    }

    @Override
    public void onStartOnboardingTutorial() {
        List<OnboardTutorialItem> items = new ArrayList<>();
        for (int idx = 0; idx < stepsCount; idx++) {
            items.add(mView.loadOnboardingStepFromResource(idx));
        }
        mView.showOnboardingTutorial(items);
    }

    @Override
    public void onSkipOnboardingTutorial() {
        mView.showHomeActivity();

    }

    @Override
    public void onCompleteTutorial() {
        mView.onCompleteTutorial();
        mView.showHomeActivity();
    }
}
