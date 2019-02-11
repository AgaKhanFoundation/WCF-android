package com.android.wcf.onboard;

public class OnboardTutorialAdapterPresenter implements OnboardTutorialAdapterMvp.Presenter {

    OnboardTutorialAdapterMvp.View view;

    public OnboardTutorialAdapterPresenter(OnboardTutorialAdapterMvp.View view) {
        this.view = view;
    }
}
