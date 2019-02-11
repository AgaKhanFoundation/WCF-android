package com.android.wcf.onboard;

public class OnboardTutorialAdapter implements OnboardTutorialAdapterMvp.View {

    OnboardTutorialAdapterMvp.Host host;
    OnboardTutorialAdapterMvp.Presenter presenter;

    public OnboardTutorialAdapter(OnboardTutorialAdapterMvp.Host host) {
        this.host = host;
    }
}
