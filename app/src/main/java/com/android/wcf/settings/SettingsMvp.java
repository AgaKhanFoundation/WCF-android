package com.android.wcf.settings;

import com.android.wcf.base.BaseMvp;

public interface SettingsMvp {
    interface View extends BaseMvp.BaseView {
        void showMilesEditDialog( int currentMiles, final EditTextDialogListener editTextDialogListener);

        void confirmToLeaveTeam();

        void participantRemovedFromTeam(String participantId);
    }

    interface Presenter extends BaseMvp.Presenter{
        void onShowMilesCommitmentSelected( int currentMiles, final EditTextDialogListener editTextDialogListener);

        void onShowLeaveTeamSelected();

        void removeFromTeam(String participantId);
    }

    interface Host extends BaseMvp.Host {

        void showDeviceConnection();

        void signout();

        void restartHomeActivity();
    }
}
