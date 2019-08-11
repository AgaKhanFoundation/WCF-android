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

    Stats participantStats = null;
    Stats teamStats = null;

    boolean teamRetrieved = false;
    boolean eventRetrieved = false;

    public ChallengePresenter(ChallengeMvp.ChallengeView view) {
        this.challengeView = view;
    }

    public List<Team> getTeamsList() {
        return challengeView.getTeamList();
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
        challengeView.setEvent(event);
        checkAndShowJourneySection();
        checkAndShowTeamSection();
    }

    private void checkAndShowTeamSection() {
        Event event = challengeView.getEvent();
        if (teamRetrieved) {
            Team team = challengeView.getParticipantTeam();
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
        Event event = challengeView.getEvent();
        if (eventRetrieved) {
            if (event != null) {
                if (event.daysToStartEvent() > 0) {
                    challengeView.hideJourneyActiveView();

                    Team team = challengeView.getParticipantTeam();
                    if (team == null && event.hasTeamBuildingStarted() && !event.hasTeamBuildingEnded()) {
                        challengeView.hideJourneyBeforeStartView();
                    } else {
                        challengeView.showJourneyBeforeStartView(event);
                    }
                } else {
                    challengeView.showJourneyActiveView(event);
                }
            } else {
                challengeView.hideJourneyActiveView();
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
        challengeView.setParticipantTeam(team);
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
    protected void onGetTeamListSuccess(List<Team> teams) {
        super.onGetTeamListSuccess(teams);
        challengeView.setTeamList(teams);
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
    public void showCreateTeamView() {
        challengeView.showCreateNewTeamView();
    }

    @Override
    public void showTeamsToJoinView() {
        challengeView.showTeamList();
    }

    @Override
    protected void onAssignParticipantToTeamSuccess(List<Integer> results, String participantId, final int teamId) {
        super.onAssignParticipantToTeamSuccess(results, participantId, teamId);
        if (results != null && results.size() == 1) {
            challengeView.participantJoinedTeam(participantId, teamId);
        } else {
            challengeView.showError("Unable to assign to team. Please try again");
        }
    }

    @Override
    protected void onAssignParticipantToTeamError(Throwable error, String participantId, final int teamId) {
        super.onAssignParticipantToTeamError(error, participantId, teamId);
        challengeView.showError(R.string.participants_manager_error, error.getMessage());
    }


    @Override
    protected void onCreateParticipantSuccess(Participant participant) {
        super.onCreateParticipantSuccess(participant);
        challengeView.setParticipant(participant);
    }

    @Override
    protected void onCreateParticipantError(Throwable error) {
        super.onCreateParticipantError(error);
        challengeView.showError(R.string.participants_manager_error, error.getMessage());
    }

    @Override
    protected void onGetParticipantSuccess(Participant participant) {
        super.onGetParticipantSuccess(participant);
        challengeView.setParticipant(participant);
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
