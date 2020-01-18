package com.android.wcf.settings;

import com.android.wcf.base.BaseMvp;

public interface TeamMembershipMvp extends BaseMvp.BaseView {
    interface View extends BaseMvp.BaseView {
        void participantRemovedFromTeam(String participantId);
        void onTeamDeleteSuccess();
        void onTeamDeleteError(Throwable error);
        void showTeamNameEditDialog(final TeamMembershipMvp.Presenter presenter, String currentName, EditTextDialogListener editTextDialogListener);

        void onTeamNameUpdateSuccess(String teamName);

        void onTeamNameUpdateConstraintError(String teamName);

        void onTeamNameUpdateError(Throwable error);

        void onTeamImageUpdateSuccess(String teamImage);

        void onTeamImageUpdateConstraintError(String teamImage);

        void onTeamImageUpdateError(Throwable error);

    }

    interface Presenter extends BaseMvp.Presenter {
        void removeMemberFromTeam(String participantId);
        void deleteTeam(int teamId);
        void onEditTeamName(final TeamMembershipMvp.Presenter presenter, final String currentName, final EditTextDialogListener editTextDialogListener);
        void updateTeamName(int teamId, String teamName);
        void updateTeamImage(int teamId, String imageFilename);
    }

    interface Host extends BaseMvp.Host {
        void restartHomeActivity();
    }
}
