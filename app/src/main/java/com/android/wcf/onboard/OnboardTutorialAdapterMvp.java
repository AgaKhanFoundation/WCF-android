package com.android.wcf.onboard;

import android.view.View;

import java.util.List;

public interface OnboardTutorialAdapterMvp {
    public interface View {
        void setData(List<OnboardTutorialItem> tutorialItems);
    }

    public interface Presenter {

        void updateOnboardTutorialData(List<OnboardTutorialItem> tutorialItems);

        int getItemCount();

        OnboardTutorialItem getStepData(int position);

        void onSkipOnboardingTutorial();

        void onCompleteTutorial();

        void showTutorialNextStep();

    }

    public interface Host extends android.view.View.OnClickListener {
        CharSequence generateSkipLinkText();

        void onSkipOnboardingTutorial();

        void showTutorialNextStep();

        void onCompleteTutorial();

        void showHomeActivity();

        int getStepTagKey();
    }
}
