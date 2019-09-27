package com.android.wcf.settings;

import android.util.Log;

import com.android.wcf.home.BasePresenter;

import java.util.List;

public class TeamMembershipPresenter extends BasePresenter implements TeamMembershipMvp.Presenter {

    public static final String TAG = TeamMembershipPresenter.class.getSimpleName();
    TeamMembershipMvp.View view;

    TeamMembershipPresenter(TeamMembershipMvp.View view) {
        this.view = view;
    }

    @Override
    public void removeMemberFromTeam(String participantId) {
        super.participantLeaveFromTeam(participantId);
    }

    @Override
    protected void onParticipantLeaveFromTeamSuccess(List<Integer> results, String participantId) {
        super.onParticipantLeaveFromTeamSuccess(results, participantId);
        view.participantRemovedFromTeam(participantId);
    }

    @Override
    protected void onParticipantLeaveFromTeamError(Throwable error, String participantId) {
        super.onParticipantLeaveFromTeamError(error, participantId);
    }


    protected void onDeleteTeamSuccess(Integer count) {
        super.onDeleteTeamSuccess(count);
        view.onTeamDeleteSuccess();
    }

    protected void onDeleteTeamError(Throwable error) {
       super.onDeleteTeamError(error);
       view.onTeamDeleteError(error);
    }

    @Override
    public String getTag() {
        return TAG;
    }
}
