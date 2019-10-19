package com.android.wcf.settings;

import android.util.Log;

import com.android.wcf.R;
import com.android.wcf.home.BasePresenter;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

public class SettingsPresenter extends BasePresenter implements SettingsMvp.Presenter {

    private static final String TAG = SettingsPresenter.class.getSimpleName();
    SettingsMvp.View settingsView;

    @Override
    public void onShowMilesCommitmentSelected(int currentMiles, final EditTextDialogListener editTextDialogListener) {
        settingsView.showMilesEditDialog(currentMiles, editTextDialogListener);
    }

    @Override
    public void onShowLeaveTeamSelected() {
        settingsView.confirmToLeaveTeam();
    }

    @Override
    public void removeFromTeam(String participantId) {
       participantLeaveFromTeam(participantId);
    }


    @Override
    public void onDeleteAccountSelected() {
        boolean deleteAllowed = settingsView.isAccountDeletable();
        if (deleteAllowed) {
            settingsView.confirmToDeleteAccount();
        }
        else {
            settingsView.cannotDeleteLeadAccountWithMembers();
        }
    }

    public SettingsPresenter(SettingsMvp.View settingsView) {
        this.settingsView = settingsView;
    }

    @Override
    protected void onParticipantLeaveFromTeamSuccess(List<Integer> results, String participantId) {
        super.onParticipantLeaveFromTeamSuccess(results, participantId);
        settingsView.participantRemovedFromTeam(participantId);
    }
    @Override
    protected void onParticipantLeaveFromTeamError(Throwable error, String participantId) {
        super.onParticipantLeaveFromTeamError(error, participantId);
        settingsView.onParticipantLeaveFromTeamError(error, participantId);
    }

    @Override
    protected void onTeamPublicVisibilityUpdateError(Throwable error){
        super.onTeamPublicVisibilityUpdateError(error);
        settingsView.teamPublicVisibilityUpdateError(error);
    }

    @Override
    protected void onTeamPublicVisibilityUpdateSuccess(List<Integer> result) {
        super.onTeamPublicVisibilityUpdateSuccess(result);
        if (result.get(0) != 1) {
            settingsView.teamPublicVisibilityUpdateError(new Error("Update failed"));
        }
    }

    @Override
    protected void onDeleteParticipantSuccess(Integer count) {
        super.onDeleteParticipantSuccess(count);
        settingsView.participantDeleted();
    }

    @Override
    protected void onDeleteParticipantError(Throwable error) {
        if (!(error instanceof NoSuchElementException)) {
            settingsView.onParticipantDeleteError(error);
        }
        else {
            settingsView.participantDeleted();
        }
    }

    @Override
    public void deleteLeaderTeam(int teamId) {
        deleteTeam(teamId);
    }

    @Override
    protected void onDeleteTeamSuccess(List<Integer> result) {
       super.onDeleteTeamSuccess(result);
       settingsView.signout(true);
    }

    @Override
    protected void onDeleteTeamError(Throwable error) {
        super.onDeleteTeamError(error);
        settingsView.signout(true); //TODO view should handle error, not signout
    }

    @Override
    public String getTag() {
        return TAG;
    }
}
