package com.android.wcf.settings;

import android.util.Log;

import com.android.wcf.home.BasePresenter;

import java.util.List;

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
    public String getTag() {
        return TAG;
    }
}
