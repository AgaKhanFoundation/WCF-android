package com.android.wcf.home.challenge;

import com.android.wcf.home.BasePresenter;
import com.android.wcf.model.Team;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CreateTeamPresenter extends BasePresenter implements CreateTeamMvp.Presenter {

    private static final String TAG = CreateTeamPresenter.class.getSimpleName();
    CreateTeamMvp.View view;

    public CreateTeamPresenter(CreateTeamMvp.View view) {
        this.view = view;
    }

    @Override
    public void createTeam(@NotNull String teamName, @Nullable String teamLeadParticipantId, @Nullable Boolean teamVisibility) {
        if (teamVisibility == null) {
            teamVisibility = true;
        }
        super.createTeam(teamName, teamLeadParticipantId, teamVisibility);
    }

    @Override
    protected void onCreateTeamSuccess(Team team) {
        super.onCreateTeamSuccess(team);
        view.teamCreated(team);
    }

    @Override
    protected void onCreateTeamConstraintError() {
        super.onCreateTeamConstraintError();
        view.showCreateTeamConstraintError();
    }

    @Override
    protected void onCreateTeamError(Throwable error) {
        super.onCreateTeamError(error);
        view.onCreateTeamError(error);

    }

    @Override
    protected void onAssignParticipantToTeamSuccess(List<Integer> results, String participantId, final int teamId) {
        super.onAssignParticipantToTeamSuccess(results, participantId, teamId);
        if (results != null && results.size() == 1) { //TODO: also check results[0] value
            view.participantJoinedTeam(participantId, teamId);
        } else {
            view.showError("Unable to assign to team. Please try again");
        }
    }

    @Override
    protected void onAssignParticipantToTeamError(Throwable error, String participantId, final int teamId) {
        super.onAssignParticipantToTeamError(error, participantId, teamId);
        view.onAssignParicipantToTeamError(error, participantId, teamId);
    }

    @Override
    public String getTag() {
        return TAG;
    }
}
