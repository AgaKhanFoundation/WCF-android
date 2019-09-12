package com.android.wcf.home.dashboard;

import com.android.wcf.base.BaseMvp;
import com.fitbitsdk.service.models.ActivitySteps;

public interface DashboardMvp {

    interface DashboardView extends BaseMvp.BaseView {
        void stepsRecorded();
    }

    interface Presenter extends BaseMvp.Presenter {
        void getParticipantDashboard(int participantId);

        void getParticipantSponsors(int participantId);

        void getParticipantDonors(int participantId);

        void getParticipantStats(String participantId);

        void saveStepsData(int participantId, int trackingSourceId, ActivitySteps data);

        }

    interface Host extends BaseMvp.Host {
        void showParticipantBadgesEarned();

        void showDeviceConnection();

        void showSupportersInvite();

        void showSupportersList();

    }
}
