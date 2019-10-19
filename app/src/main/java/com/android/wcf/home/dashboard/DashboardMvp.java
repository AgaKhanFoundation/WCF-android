package com.android.wcf.home.dashboard;

import com.android.wcf.base.BaseMvp;
import com.fitbitsdk.service.models.ActivitySteps;

import java.util.Date;

public interface DashboardMvp {

    interface DashboardView extends BaseMvp.BaseView {
        void stepsRecorded(String lastSavedDate);

        void onGetParticipantError(Throwable error);

        void onGetParticipantStatsError(Throwable error);
    }

    interface Presenter extends BaseMvp.Presenter {
        void getParticipantDashboard(int participantId);

        void getParticipantSponsors(int participantId);

        void getParticipantDonors(int participantId);

        void getParticipantStats(String participantId);

        void saveStepsData(int participantId, int trackingSourceId, ActivitySteps data, Date eventStartDate, Date eventEndDate, String lastSavedDate);

        }

    interface Host extends BaseMvp.Host {
        void showParticipantBadgesEarned();

        void showDeviceConnection();

        void showSupportersInvite();

        void showSupportersList();

    }
}
