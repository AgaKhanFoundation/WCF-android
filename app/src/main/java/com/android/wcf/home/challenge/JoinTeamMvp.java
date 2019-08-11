package com.android.wcf.home.challenge;

import com.android.wcf.base.BaseMvp;

public interface JoinTeamMvp extends BaseMvp.BaseView {
    interface View extends BaseMvp.BaseView {
        void participantJoinedTeam(String participantId, int teamId);
    }

    interface Presenter extends BaseMvp.Presenter {
        void assignParticipantToTeam(String participantId, int teamId);
    }

    interface Host extends BaseMvp.Host {

    }
}
