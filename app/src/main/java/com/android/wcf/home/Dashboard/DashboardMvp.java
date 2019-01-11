package com.android.wcf.home.Dashboard;

import com.android.wcf.base.BaseMvp;

public interface DashboardMvp {

    interface DashboardView extends BaseMvp.BaseView {
    }

    interface Presenter extends BaseMvp.Presenter {
        void getParticipant(String fbid);

        void getParticipantStats(String fbid);
    }
}
