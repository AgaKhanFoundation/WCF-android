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
    public void removeFromTeam(String fbid) {
       participantLeaveFromTeam(fbid);
    }

    public SettingsPresenter(SettingsMvp.View settingsView) {
        this.settingsView = settingsView;
    }

    @Override
    protected void onParticipantLeaveFromTeamSuccess(List<Integer> results, String fbid) {
        super.onParticipantLeaveFromTeamSuccess(results, fbid);
        settingsView.participantRemovedFromTeam(fbid);
    }
    @Override
    protected void onParticipantLeaveFromTeamError(Throwable error, String fbid) {
        super.onParticipantLeaveFromTeamError(error, fbid);
    }

}
