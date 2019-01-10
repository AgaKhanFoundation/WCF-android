package com.android.wcf.home.Dashboard;

import com.android.wcf.R;
import com.android.wcf.home.BasePresenter;
import com.android.wcf.model.Participant;
import com.android.wcf.model.Stats;

public class DashboardPresenter extends BasePresenter implements DashboardMvp.Presenter {

    private static final String TAG = DashboardPresenter.class.getSimpleName();
    private DashboardMvp.DashboardView dashboardView;

    Participant mParticipant = null;
    Stats mParticipantStats = null;

    public DashboardPresenter(DashboardMvp.DashboardView view) {
        this.dashboardView = view;
    }

    @Override
    public String getTag() {
        return TAG;
    }

    protected void onGetParticipantError(Throwable error) {
        super.onGetParticipantError(error);
        dashboardView.showError(R.string.participants_manager_error, error.getMessage());
    }

    protected void onGetParticipantSuccess(Participant participant) {
        super.onGetParticipantSuccess(participant);
        mParticipant = participant;
    }

    public void onGetParticipantStatsSuccess(Stats stats) {
        super.onGetParticipantStatsSuccess(stats);
        mParticipantStats = stats;
    }

    @Override
    protected void onGetParticipantStatsError(Throwable error) {
        super.onGetParticipantStatsError(error);
        dashboardView.showError(R.string.participants_manager_error, error.getMessage());
    }

}
