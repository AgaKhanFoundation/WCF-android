package com.android.wcf.settings;

import com.android.wcf.base.BaseMvp;

public interface TeamMembershipMvp extends BaseMvp.BaseView {
    interface View extends BaseMvp.BaseView {
        void participantRemovedFromTeam(String participantId);
    }

    interface Presenter extends BaseMvp.Presenter {
        void removeMemberFromTeam(String participantId);
    }

    interface Host extends BaseMvp.Host {
    }
}
