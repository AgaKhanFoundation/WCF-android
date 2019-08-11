package com.android.wcf.home.challenge;

import com.android.wcf.home.BasePresenter;

import java.util.List;

public class JoinTeamPresenter extends BasePresenter implements JoinTeamMvp.Presenter {
    private static final String TAG = JoinTeamPresenter.class.getSimpleName();
    JoinTeamMvp.View view;

    public JoinTeamPresenter(JoinTeamMvp.View view) {
        this.view = view;
    }

    @Override
    public String getTag() {
        return TAG;
    }


    @Override
    protected void onAssignParticipantToTeamSuccess(List<Integer> results, String participantId, final int teamId) {
        super.onAssignParticipantToTeamSuccess(results, participantId, teamId);
        if (results != null && results.size() == 1) {
            view.participantJoinedTeam(participantId, teamId);
        } else {
            view.showError("Unable to assign to team. Please try again");
        }
    }
}
