package com.android.wcf.settings;

import com.android.wcf.base.BaseMvp;

public interface SettingsMvp {
    interface View extends BaseMvp.BaseView {
        void showMilesEditDialog( int currentMiles, final EditTextDialogListener editTextDialogListener);

        void confirmToLeaveTeam();

        void participantRemovedFromTeam(String participantId);

        void teamPublicVisibilityUpdateError(Throwable error);

        void cannotDeleteLeadAccountWithMembers();

        void confirmToDeleteAccount();

        boolean isAccountDeletable();

        void participantDeleted();

        void onParticipantDeleteError(Throwable error);

        void onParticipantLeaveFromTeamError(Throwable error, String participantId);

        void onTeamDeleteError(Throwable error);

        void signout(boolean complete);

    }

    interface Presenter extends BaseMvp.Presenter{
        void onShowMilesCommitmentSelected( int currentMiles, final EditTextDialogListener editTextDialogListener);

        void onShowLeaveTeamSelected();

        void removeFromTeam(String participantId);

        void updateTeamPublicVisibility(int teamId, boolean isChecked);

        void createParticipantCommitment(String participantId, int eventId, int stepsCommitted);

        void updateParticipantCommitment(int commitmentId, String participantId, int eventId, int stepsCommitted);

        void onDeleteAccountSelected();

        void deleteParticipant(String participantId);

        void deleteLeaderTeam(int myTeamId);

    }

    interface Host extends BaseMvp.Host {

        void showDeviceConnection();

        void signout(boolean complete);

        void restartApp();

        void restartHomeActivity();

        void showTeamMembershipDetail(boolean isTeamLead);

        void showAKFProfileView();

        void switchServerForTestingTeam();
    }
}
