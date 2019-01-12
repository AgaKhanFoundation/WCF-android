package com.android.wcf.home.Campaign;

import com.android.wcf.R;
import com.android.wcf.home.BasePresenter;
import com.android.wcf.model.Participant;
import com.android.wcf.model.Stats;
import com.android.wcf.model.Team;

import java.util.List;

public class CampaignPresenter extends BasePresenter implements CampaignMvp.Presenter {

    private static final String TAG = CampaignPresenter.class.getSimpleName();
    private CampaignMvp.CampaignView campaignView;

    Participant mParticipant = null;
    Stats mParticipantStats = null;

    List<Team> mTeams = null;
    Team mTeam = null;
    Stats mTeamStats = null;

    public CampaignPresenter(CampaignMvp.CampaignView view) {
        this.campaignView = view;
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    protected void onCreateParticipantSuccess(Participant participant) {
        super.onCreateParticipantSuccess(participant);
        mParticipant = participant;

    }

    @Override
    protected void onCreateParticipantError(Throwable error) {
        super.onCreateParticipantError(error);
        campaignView.showError(R.string.participants_manager_error, error.getMessage());
    }

    @Override
    protected void onGetParticipantSuccess(Participant participant) {
        super.onGetParticipantSuccess(participant);
        mParticipant = participant;
    }

    @Override
    protected void onGetParticipantError(Throwable error) {
        super.onGetParticipantError(error);
        campaignView.showError(R.string.participants_manager_error, error.getMessage());
    }

    @Override
    protected void onGetParticipantStatsSuccess(Stats stats) {
        super.onGetParticipantStatsSuccess(stats);
        mParticipantStats = stats;
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
        getTeams();
    }

    @Override
    protected void onCreateTeamError(Throwable error) {
        super.onCreateTeamError(error);
        campaignView.showError(R.string.teams_manager_error, error.getMessage());
    }

    @Override
    protected void onGetTeamListSuccess(List<Team> teams) {
        super.onGetTeamListSuccess(teams);
        mTeams = teams;
    }

    @Override
    protected void onGetTeamListError(Throwable error) {
        super.onGetTeamListError(error);
        campaignView.showError(R.string.teams_manager_error, error.getMessage());
    }

    @Override
    protected void onGetTeamSuccess(Team team) {
        super.onGetTeamSuccess(team);
        mTeam = team;
    }

    @Override
    protected void onGetTeamError(Throwable error) {
        super.onGetTeamError(error);
        campaignView.showError(R.string.teams_manager_error, error.getMessage());
    }

    @Override
    protected void onGetTeamStatsSuccess(Stats stats) {
        super.onGetTeamStatsSuccess(stats);
        mTeamStats = stats;
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
    protected void onAssignParticipantToTeamSuccess(List<Integer> results, String fbid, final int teamId) {
        super.onAssignParticipantToTeamSuccess(results, fbid, teamId);
        getParticipant(fbid);
        getTeam(teamId);
    }

    @Override
    protected void onAssignParticipantToTeamError(Throwable error, String fbid, final int teamId) {
        super.onAssignParticipantToTeamError(error, fbid, teamId);
        campaignView.showError(R.string.participants_manager_error, error.getMessage());
    }
}
