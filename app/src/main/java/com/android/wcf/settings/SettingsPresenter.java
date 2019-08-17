package com.android.wcf.settings;

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
    public String getTag() {
        return TAG;
    }
}
