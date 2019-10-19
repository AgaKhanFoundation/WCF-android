package com.android.wcf.settings;

import android.util.Log;

import com.android.wcf.R;
import com.android.wcf.home.BasePresenter;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

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
        if (error instanceof IOException) {
            view.showNetworkErrorMessage(R.string.data_error);
        }
        else {
            super.onParticipantLeaveFromTeamError(error, participantId);
        }
    }


    @Override
    protected void onDeleteTeamSuccess(List<Integer> result) {
        super.onDeleteTeamSuccess(result);
        if (result.get(0) > 0) { //TODO define a constaint for API success/failure
            view.onTeamDeleteSuccess();
        }
    }

    @Override
    protected void onDeleteTeamError(Throwable error) {
        if (error instanceof IOException) {
            view.showNetworkErrorMessage(R.string.data_error);
        }

        else if (!(error instanceof NoSuchElementException)) {
            super.onDeleteTeamError(error);
            view.onTeamDeleteError(error);
        }
        else {
            view.onTeamDeleteSuccess();
        }
    }

    @Override
    public String getTag() {
        return TAG;
    }
}
