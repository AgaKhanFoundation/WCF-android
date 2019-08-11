package com.android.wcf.home.challenge;

import com.android.wcf.R;
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
    protected void onCreateTeamError(Throwable error) {
        super.onCreateTeamError(error);
        view.showError(R.string.teams_manager_error, error.getMessage());
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

    @Override
    protected void onAssignParticipantToTeamError(Throwable error, String participantId, final int teamId) {
        super.onAssignParticipantToTeamError(error, participantId, teamId);
        view.showError(R.string.participant_team_join_error, error.getMessage());
    }

    @Override
    public String getTag() {
        return TAG;
    }
}
