package com.android.wcf.home.challenge;

import android.util.Log;

import com.android.wcf.R;
import com.android.wcf.home.BasePresenter;
import com.android.wcf.model.Event;
import com.android.wcf.model.Participant;
import com.android.wcf.model.Stats;
import com.android.wcf.model.Team;

import java.util.List;

public class ChallengePresenter extends BasePresenter implements ChallengeMvp.Presenter {

    private static final String TAG = ChallengePresenter.class.getSimpleName();
    private ChallengeMvp.ChallengeView challengeView;

    Event event;
    Participant participant = null;
    Stats participantStats = null;
    List<Team> teams = null;
    Team team = null;
    Stats teamStats = null;

    boolean teamRetrieved = false;
    boolean eventRetrieved = false;

    public ChallengePresenter(ChallengeMvp.ChallengeView view) {
        this.challengeView = view;
    }

    public List<Team> getTeamsList() {
        return teams;
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public void getEvent(int eventId) {
        if (eventId > 0) {
            super.getEvent(eventId);
        } else {
            eventRetrieved = true;
            checkAndShowJourneySection();
            checkAndShowTeamSection();
        }
    }

    @Override
    protected void onGetEventSuccess(Event event) {
        super.onGetEventSuccess(event);
        eventRetrieved = true;
        this.event = event;
        checkAndShowJourneySection();
        checkAndShowTeamSection();
    }

    private void checkAndShowTeamSection() {
        if (teamRetrieved) {
            if (team == null) {
                challengeView.hideTeamCard();
                if (eventRetrieved && event != null) {
                    if (event.daysToStartEvent() > 0 &&  !event.hasTeamBuildingEnded()) {
                        challengeView.showCreateOrJoinTeamCard();
                    } else {
                        challengeView.hideCreateOrJoinTeamCard();
                    }
                }
            } else {
                challengeView.hideCreateOrJoinTeamCard();
                challengeView.showMyTeamCard(team);
            }
        }
    }

    private void checkAndShowJourneySection() {
        if (eventRetrieved) {
            if (event != null) {
                if (event.daysToStartEvent() > 0) {
                    challengeView.hideJourneyDetails();

                    if (team == null && event.hasTeamBuildingStarted() && !event.hasTeamBuildingEnded()) {
                        challengeView.hideJourneyBeforeStartCard();
                    } else {
                        challengeView.showJourneyBeforeStartCard(event);
                    }
                } else {
                    challengeView.showJourneyDetails(event);
                }
            } else {
                challengeView.hideJourneyDetails();
            }
        }
    }

    @Override
    protected void onGetEventError(Throwable error) {
        super.onGetEventError(error);
    }

    @Override
    public void getTeam(int id) {
        if (id > 0) {
            super.getTeam(id);
        } else {
            teamRetrieved = true;
            checkAndShowJourneySection();
            checkAndShowTeamSection();
        }
    }

    @Override
    protected void onGetTeamSuccess(Team team) {
        super.onGetTeamSuccess(team);
        this.team = team;
        teamRetrieved = true;

        checkAndShowJourneySection();
        checkAndShowTeamSection();
    }

    @Override
    protected void onGetTeamError(Throwable error) {
        super.onGetTeamError(error);
        challengeView.showError(R.string.teams_manager_error, error.getMessage());
    }

    @Override
    protected void onCreateTeamSuccess(Team team) {
        super.onCreateTeamSuccess(team);
        challengeView.teamCreated(team);
    }

    @Override
    protected void onCreateTeamError(Throwable error) {
        super.onCreateTeamError(error);
        challengeView.showError(R.string.teams_manager_error, error.getMessage());
    }

    @Override
    protected void onGetTeamListSuccess(List<Team> teams) {
        super.onGetTeamListSuccess(teams);
        this.teams = teams;
        challengeView.enableShowCreateTeam(true);
        if (teams == null || teams.size() == 0) {
            challengeView.enableJoinExistingTeam(false);
        } else {
            challengeView.enableJoinExistingTeam(true);
        }
    }

    @Override
    protected void onGetTeamListError(Throwable error) {
        super.onGetTeamListError(error);

        challengeView.enableShowCreateTeam(true);
        challengeView.enableJoinExistingTeam(false);

        challengeView.showError(R.string.teams_manager_error, error.getMessage());
    }

    @Override
    protected void onGetTeamStatsSuccess(Stats stats) {
        super.onGetTeamStatsSuccess(stats);
        this.teamStats = stats;
    }

    @Override
    protected void onGetTeamStatsError(Throwable error) {
        super.onGetTeamStatsError(error);
        challengeView.showError(R.string.teams_manager_error, error.getMessage());
    }

    @Override
    protected void onDeleteTeamSuccess(Integer count) {
        super.onDeleteTeamSuccess(count);
        getTeams();
    }

    @Override
    protected void onDeleteTeamError(Throwable error) {
        super.onDeleteTeamError(error);
        challengeView.showError(R.string.teams_manager_error, error.getMessage());
    }

    @Override
    public void showCreateTeamClick() {
        challengeView.showCreateNewTeamView();
    }

    @Override
    public void createTeamClick(String teamName) {
        Log.d(TAG, "createTeamClick");
        super.createTeam(teamName);

    }

    @Override
    public void cancelCreateTeamClick() {
        challengeView.hideCreateNewTeamView();
    }

    @Override
    public void showTeamsToJoinClick() {
        challengeView.showTeamList(teams);
    }

    @Override
    protected void onAssignParticipantToTeamSuccess(List<Integer> results, String fbid, final int teamId) {
        super.onAssignParticipantToTeamSuccess(results, fbid, teamId);
        if (results != null && results.size() == 1) {
            challengeView.participantJoinedTeam(fbid, teamId);
        } else {
            challengeView.showError("Unable to assign to team. Please try again");
        }
    }

    @Override
    protected void onAssignParticipantToTeamError(Throwable error, String fbid, final int teamId) {
        super.onAssignParticipantToTeamError(error, fbid, teamId);
        challengeView.showError(R.string.participants_manager_error, error.getMessage());
    }


    @Override
    protected void onCreateParticipantSuccess(Participant participant) {
        super.onCreateParticipantSuccess(participant);
        this.participant = participant;
    }

    @Override
    protected void onCreateParticipantError(Throwable error) {
        super.onCreateParticipantError(error);
        challengeView.showError(R.string.participants_manager_error, error.getMessage());
    }

    @Override
    protected void onGetParticipantSuccess(Participant participant) {
        super.onGetParticipantSuccess(participant);
        this.participant = participant;
    }

    @Override
    protected void onGetParticipantError(Throwable error) {
        super.onGetParticipantError(error);
        challengeView.showError(R.string.participants_manager_error, error.getMessage());
    }

    @Override
    protected void onGetParticipantStatsSuccess(Stats stats) {
        super.onGetParticipantStatsSuccess(stats);
        participantStats = stats;
    }

    @Override
    protected void onGetParticipantStatsError(Throwable error) {
        super.onGetParticipantStatsError(error);
        challengeView.showError(R.string.participants_manager_error, error.getMessage());
    }

    @Override
    protected void onDeleteParticipantSuccess(Integer count) {
        super.onDeleteParticipantSuccess(count);
    }

    @Override
    protected void onDeleteParticipantError(Throwable error) {
        super.onDeleteParticipantError(error);
        challengeView.showError(R.string.participants_manager_error, error.getMessage());
    }

}
