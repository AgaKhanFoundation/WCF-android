package com.android.wcf.home.dashboard;

import android.util.Log;

import com.android.wcf.R;
import com.android.wcf.home.BasePresenter;
import com.android.wcf.model.Participant;
import com.android.wcf.model.Record;
import com.android.wcf.model.Stats;
import com.fitbitsdk.service.models.ActivitySteps;
import com.fitbitsdk.service.models.Steps;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    @Override
    public void getParticipantDashboard(int participantId) {

    }

    @Override
    public void getParticipantSponsors(int participantId) {

    }

    @Override
    public void getParticipantDonors(int participantId) {

    }

    @Override
    public void getParticipantStats(String participantId) {
        super.getParticipantStats(participantId);

    }

    @Override
    public void saveStepsData(int participantId, int trackingSourceId, ActivitySteps data) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Steps steps: data.getSteps()){
            Date stepsDate = null;
            String stepsDateString = null;
            try {
                stepsDateString = steps.getDate();
                stepsDate = sdf.parse(steps.getDate());
            } catch (ParseException e) {
               Log.e(TAG, "Error parsing " + steps.getDate() + ": " + e.getMessage());
               stepsDate = new Date();
                stepsDateString = sdf.format(stepsDate);
            }
            saveTrackedSteps(participantId, trackingSourceId, stepsDateString, steps.getValue());
        }
    }

    @Override
    protected void onStepsRecordSuccess(Record stepsRecord) {
        super.onStepsRecordSuccess(stepsRecord);
        dashboardView.stepsRecorded();
    }

    @Override
    protected void onStepsRecordError(Throwable error, final int participantId, final int trackerSourceId, final String stepsDate, final long stepsCount) {
        super.onStepsRecordError(error, participantId, trackerSourceId, stepsDate, stepsCount);
    }

}
