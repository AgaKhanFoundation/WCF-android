package com.android.wcf.settings;

import com.android.wcf.base.BaseMvp;

public interface SettingsMvp {
    interface View {
        void showMilesEditDialog();

        void confirmToLeaveTeam();

        void participantRemovedFromTeam(String fbid);
    }

    interface Presenter {
        void onShowMilesCommitmentSelected();

        void onShowLeaveTeamSelected();

        void removeFromTeam(String fbid);
    }

    interface Host {
        void setToolbarTitle(String title);

        void showDeviceConnection();

        void signout();

        void restartHomeActivity();
    }
}
