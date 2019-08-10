package com.android.wcf.home.dashboard;

import android.net.Uri;

import com.android.wcf.base.BaseMvp;

public interface DashboardMvp {

    interface DashboardView extends BaseMvp.BaseView {
    }

    interface Presenter extends BaseMvp.Presenter {
        void getParticipantDashboard(int participantId);

        void getParticipantSponsors(int participantId);

        void getParticipantDonors(int participantId);

        void getParticipantStats(int participantId);
    }

    interface Host {
        void onDashboardFragmentInteraction(Uri uri);
        void showToolbarUpAffordance(boolean showFlag);
        void setViewTitle(String title);
        void showDeviceConnection();

    }
}
