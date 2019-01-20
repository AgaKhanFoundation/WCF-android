package com.android.wcf.home.Campaign;

import android.util.Log;

import com.android.wcf.R;
import com.android.wcf.home.BasePresenter;
import com.android.wcf.model.Event;
import com.android.wcf.model.Participant;
import com.android.wcf.model.Stats;
import com.android.wcf.model.Team;

import java.util.List;

public class CampaignPresenter extends BasePresenter implements CampaignMvp.Presenter {

    private static final String TAG = CampaignPresenter.class.getSimpleName();
    private CampaignMvp.CampaignView campaignView;

    Event event;
    Participant participant = null;
    Stats participantStats = null;

    List<Team> teams = null;
    Team team = null;
    Stats teamStats = null;

    public CampaignPresenter(CampaignMvp.CampaignView view) {
        this.campaignView = view;
    }

    public List<Team> getTeamsList() {
        return teams;
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    protected void onGetEventSuccess(Event event) {
        super.onGetEventSuccess(event);
        campaignView.showJourney(event);
    }

    @Override
    protected void onGetEventError(Throwable error) {
        super.onGetEventError(error);
    }

    @Override
    protected void onCreateParticipantSuccess(Participant participant) {
        super.onCreateParticipantSuccess(participant);
        this.participant = participant;

    }

    @Override
    protected void onCreateParticipantError(Throwable error) {
        super.onCreateParticipantError(error);
        campaignView.showError(R.string.participants_manager_error, error.getMessage());
    }

    @Override
    protected void onGetParticipantSuccess(Participant participant) {
        super.onGetParticipantSuccess(participant);
        this.participant = participant;
    }

    @Override
    protected void onGetParticipantError(Throwable error) {
        super.onGetParticipantError(error);
        campaignView.showError(R.string.participants_manager_error, error.getMessage());
    }

    @Override
    protected void onGetParticipantStatsSuccess(Stats stats) {
        super.onGetParticipantStatsSuccess(stats);
        participantStats = stats;
    }

    @Override
    protected void onGetParticipantStatsError(Throwable error) {
        super.onGetParticipantStatsError(error);
        campaignView.showError(R.string.participants_manager_error, error.getMessage());
    }


    @Override
    protected void onDeleteParticipantSuccess(Integer count) {
        super.onDeleteParticipantSuccess(count);
    }

    @Override
    protected void onDeleteParticipantError(Throwable error) {
        super.onDeleteParticipantError(error);
        campaignView.showError(R.string.participants_manager_error, error.getMessage());
    }

    @Override
    protected void onCreateTeamSuccess(Team team) {
        super.onCreateTeamSuccess(team);
        campaignView.teamCreated(team);
    }

    @Override
    protected void onCreateTeamError(Throwable error) {
        super.onCreateTeamError(error);
        campaignView.showError(R.string.teams_manager_error, error.getMessage());
    }

    @Override
    protected void onGetTeamListSuccess(List<Team> teams) {
        super.onGetTeamListSuccess(teams);
        this.teams = teams;
        campaignView.enableShowCreateTeam(true);
        if (teams == null || teams.size() == 0) {
            campaignView.enableJoinExistingTeam(false);
        } else {
            campaignView.enableJoinExistingTeam(true);
        }
    }

    @Override
    protected void onGetTeamListError(Throwable error) {
        super.onGetTeamListError(error);

        campaignView.enableShowCreateTeam(true);
        campaignView.enableJoinExistingTeam(false);

        campaignView.showError(R.string.teams_manager_error, error.getMessage());
    }

    @Override
    protected void onGetTeamSuccess(Team team) {
        super.onGetTeamSuccess(team);
        this.team = team;
    }

    @Override
    protected void onGetTeamError(Throwable error) {
        super.onGetTeamError(error);
        campaignView.showError(R.string.teams_manager_error, error.getMessage());
    }

    @Override
    protected void onGetTeamStatsSuccess(Stats stats) {
        super.onGetTeamStatsSuccess(stats);
        this.teamStats = stats;
    }

    @Override
    protected void onGetTeamStatsError(Throwable error) {
        super.onGetTeamStatsError(error);
        campaignView.showError(R.string.teams_manager_error, error.getMessage());
    }

    @Override
    protected void onDeleteTeamSuccess(Integer count) {
        super.onDeleteTeamSuccess(count);
        getTeams();
    }

    @Override
    protected void onDeleteTeamError(Throwable error) {
        super.onDeleteTeamError(error);
        campaignView.showError(R.string.teams_manager_error, error.getMessage());
    }

    @Override
    public void showCreateTeamClick() {
        campaignView.showCreateNewTeamView();
    }

    @Override
    public void createTeamClick(String teamName) {
        Log.d(TAG, "createTeamClick");
        super.createTeam(teamName);

    }

    @Override
    public void cancelCreateTeamClick() {
        campaignView.hideCreateNewTeamView();
    }

    @Override
    public void showTeamsToJoinClick() {
        campaignView.showTeamList(teams);
    }

    @Override
    protected void onAssignParticipantToTeamSuccess(List<Integer> results, String fbid, final int teamId) {
        super.onAssignParticipantToTeamSuccess(results, fbid, teamId);
        if (results != null && results.size() == 1) {
            campaignView.participantJoinedTeam(fbid, teamId);
        }
        else {
            campaignView.showError("Unable to assign to team. Please try again");
        }
    }

    @Override
    protected void onAssignParticipantToTeamError(Throwable error, String fbid, final int teamId) {
        super.onAssignParticipantToTeamError(error, fbid, teamId);
        campaignView.showError(R.string.participants_manager_error, error.getMessage());
    }
}
