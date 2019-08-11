package com.android.wcf.settings;

import android.util.Log;

import com.android.wcf.home.BasePresenter;

import java.util.List;

public class SettingsPresenter  extends BasePresenter implements SettingsMvp.Presenter {

    private static final String TAG = SettingsPresenter.class.getSimpleName();
    SettingsMvp.View settingsView;

    @Override
    public void onShowMilesCommitmentSelected() {
        settingsView.showMilesEditDialog();
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

}
