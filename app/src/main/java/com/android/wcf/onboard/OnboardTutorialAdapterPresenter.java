package com.android.wcf.onboard;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class OnboardTutorialAdapterPresenter implements OnboardTutorialAdapterMvp.Presenter {

    OnboardTutorialAdapterMvp.View view;
    OnboardTutorialAdapterMvp.Host host;
    List<OnboardTutorialItem> tutorialItems = new ArrayList<>();

    public OnboardTutorialAdapterPresenter(@NonNull OnboardTutorialAdapterMvp.View view, @NonNull OnboardTutorialAdapterMvp.Host host) {
        this.view = view;
        this.host = host;

    }

    @Override
    public void updateOnboardTutorialData(List<OnboardTutorialItem> tutorialItems) {

        this.tutorialItems.clear();
        if (tutorialItems != null) {
            this.tutorialItems.addAll(tutorialItems);
        }
    }

    @Override
    public int getItemCount() {
        return tutorialItems.size();
    }

    @Override
    public OnboardTutorialItem getStepData(int position) {
        if (position < tutorialItems.size()) {
            return tutorialItems.get(position);
        }
        return null;
    }

    @Override
    public void onSkipOnboardingTutorial() {
        host.onSkipOnboardingTutorial();
    }

    @Override
    public void showTutorialNextStep() {
        host.showTutorialNextStep();
    }

    @Override
    public void onCompleteTutorial() {
        host.onCompleteTutorial();
        host.showHomeActivity();
    }
}
