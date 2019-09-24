package com.android.wcf.splash;

import com.android.wcf.R;
import com.android.wcf.home.BasePresenter;
import com.android.wcf.model.Event;

import java.util.List;

public class SplashPresenter extends BasePresenter implements SplashMvp.SplashPresenter {

    private static final String TAG = SplashPresenter.class.getSimpleName();
    private SplashMvp.SplashView splashView;

    private List<Event> events = null;

    public SplashPresenter(SplashMvp.SplashView view) {
        this.splashView = view;
    }

    @Override
    protected void onGetEventsListSuccess(List<Event> events) {
        super.onGetEventsListSuccess(events);
        this.events = events;
        Event currentEvent = getCurrentEvent();
        if ( currentEvent == null) {
            splashView.showErrorAndCloseApp(R.string.events_list_not_setup_error);
            return;
        }
        splashView.navigateToHomeView(currentEvent);
    }

    @Override
    protected void onGetEventsListError(Throwable error) {
        super.onGetEventsListError(error);
        splashView.showErrorAndCloseApp(R.string.events_list_retrieval_error);
    }

    private Event getCurrentEvent() {
        if (events == null || events.size() == 0) {
            return null;
        }
        return events.get(0);
    }
}
