package com.android.wcf.home.dashboard;

import android.util.Log;

import com.android.wcf.home.BasePresenter;
import com.android.wcf.model.Participant;
import com.android.wcf.model.Record;
import com.android.wcf.model.Stats;
import com.fitbitsdk.service.models.ActivitySteps;
import com.fitbitsdk.service.models.Steps;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class DashboardPresenter extends BasePresenter implements DashboardMvp.Presenter {

    private static final String TAG = DashboardPresenter.class.getSimpleName();
    private DashboardMvp.DashboardView dashboardView;

    Participant mParticipant = null;
    Stats mParticipantStats = null;

    int totalDaysTracked = 0;
    List<String> savedDates = new ArrayList<>();
    List<String> failedDates = new ArrayList<>();

    public DashboardPresenter(DashboardMvp.DashboardView view) {
        this.dashboardView = view;
    }

    @Override
    public String getTag() {
        return TAG;
    }

    protected void onGetParticipantError(Throwable error) {
        super.onGetParticipantError(error);
        dashboardView.onGetParticipantError(error);

    }

    protected void onGetParticipantSuccess(Participant participant) {
        super.onGetParticipantSuccess(participant);
        mParticipant = participant;
        dashboardView.hideLoadingProgressView("onGetParticipantSuccess");
    }

    public void onGetParticipantStatsSuccess(Stats stats) {
        super.onGetParticipantStatsSuccess(stats);
        mParticipantStats = stats;
        dashboardView.hideLoadingProgressView("onGetParticipantStatsSuccess");
    }

    @Override
    protected void onGetParticipantStatsError(Throwable error) {
        super.onGetParticipantStatsError(error);
        dashboardView.onGetParticipantStatsError(error);

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
        dashboardView.showLoadingProgressView("getParticipantStats");
        super.getParticipantStats(participantId);

    }

    @Override
    public void saveStepsData(int participantId, int trackingSourceId, ActivitySteps data, Date startDate, Date endDate, String lastSavedDate) {
        totalDaysTracked = 0;
        savedDates.clear();
        failedDates.clear();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());
        Date stepsDate = null;
        List<Steps> stepsDataAscending = new ArrayList(data.getSteps());
        Collections.sort(stepsDataAscending, new Comparator<Steps>() {
            @Override
            public int compare(Steps stepdate1, Steps stepDate2) {
                return stepdate1.getDate().compareTo(stepDate2.getDate());
            }
        });

        totalDaysTracked = stepsDataAscending.size();
        if (totalDaysTracked > 0) {
            dashboardView.showLoadingProgressView("saveStepsData");
        }

        for (Steps steps: stepsDataAscending){
            String stepsDateString = steps.getDate();
            try {
                stepsDate = sdf.parse(steps.getDate()); //ensure valid date. parse will fail if incorrect format and we will skip it

                if (stepsDate.getTime() < startDate.getTime() || stepsDate.getTime() > endDate.getTime() ) {
                    //only save when the steps are for date when challenge is in progress
                    totalDaysTracked--;
                    continue;
                }
                if (today.compareTo(stepsDateString) <= 0) {
                    //don't save for today or future date. Save only for past date.
                    //this is a workaround for the API limitation:
                    // 1) avoid 409 constraint violation
                    // 2) preventing steps from double counting when this method is run multiple times

                    totalDaysTracked--;
                    continue;
                }
                if (lastSavedDate.compareTo(stepsDateString) > 0) {
                    //step date earlier than lastSaved; must have previously saved it, so skip in this run
                    totalDaysTracked--;
                    continue;
                }
                if (steps.getValue() == 0) {
                    //no need to save 0 steps
                    totalDaysTracked--;
                    continue;
                }
                saveTrackedSteps(participantId, trackingSourceId, stepsDateString, steps.getValue());
            } catch (ParseException e) {
               Log.e(TAG, "Error parsing " + steps.getDate() + ": " + e.getMessage());
                totalDaysTracked--;
            }
        }
        if (totalDaysTracked <= 0) {
            dashboardView.hideLoadingProgressView("saveStepsData");

        }
    }

    @Override
    protected void onStepsRecordSuccess(Record stepsRecord, String stepDate) {
        super.onStepsRecordSuccess(stepsRecord, stepDate);
        savedDates.add(stepDate);
        checkAndSaveLastTrackedDate();
    }

    @Override
    protected void onStepsRecordError(Throwable error, final int participantId, final int trackerSourceId, final String stepsDate, final long stepsCount) {
        super.onStepsRecordError(error, participantId, trackerSourceId, stepsDate, stepsCount);

        if (error.getMessage().startsWith("HTTP 409")) {
            savedDates.add(stepsDate);
        }
        else {
            failedDates.add(stepsDate);
        }
        checkAndSaveLastTrackedDate();
    }

    protected void checkAndSaveLastTrackedDate() {
        if (savedDates.size() + failedDates.size() >= totalDaysTracked) {
            dashboardView.hideLoadingProgressView("checkAndSaveLastTrackedDate");
            String lastSavedDate = null;
            if (failedDates.size() > 0) {
                Collections.sort(failedDates);
                String earliestFailedDate = failedDates.get(0);
                if (earliestFailedDate != null) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd");
                    Date date = null;
                    try {
                        date = dateFormat.parse(earliestFailedDate);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        calendar.add(Calendar.DATE, -1);
                        lastSavedDate = dateFormat.format(calendar.getTime());

                        Log.d(TAG, "earliest failed date=" + earliestFailedDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            else {
                if (savedDates.size() > 0) {
                    Collections.sort(savedDates);
                    lastSavedDate = savedDates.get(savedDates.size() - 1);

                    Log.d(TAG, "last saved date=" + lastSavedDate);
                }
            }
            if (lastSavedDate != null) {
                dashboardView.stepsRecorded(lastSavedDate);
            }
        }
    }
}
