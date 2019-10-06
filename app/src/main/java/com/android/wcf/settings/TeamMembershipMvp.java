package com.android.wcf.settings;

import com.android.wcf.base.BaseMvp;

public interface TeamMembershipMvp extends BaseMvp.BaseView {
    interface View extends BaseMvp.BaseView {
        void participantRemovedFromTeam(String participantId);
        void onTeamDeleteSuccess();
        void onTeamDeleteError(Throwable error);
    }

    interface Presenter extends BaseMvp.Presenter {
        void removeMemberFromTeam(String participantId);
        void deleteTeam(int teamId);
    }

    interface Host extends BaseMvp.Host {
        void restartHomeActivity();
    }
}
