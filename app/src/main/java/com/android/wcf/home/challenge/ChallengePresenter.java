package com.android.wcf.home.challenge;

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
    boolean participantRetrieved = false;

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
            updateJourneySection();
            updateTeamSection();
        }
    }

    @Override
    protected void onGetEventSuccess(Event event) {
        super.onGetEventSuccess(event);
        challengeView.setEvent(event);
        eventRetrieved = true;
        updateJourneySection();
        updateTeamSection();
    }

    @Override
    protected void onGetEventError(Throwable error) {
        super.onGetEventError(error);
    }

    private void updateTeamSection() {
        if (eventRetrieved && teamRetrieved && participantRetrieved) {
            Event event = challengeView.getEvent();
            Team team = challengeView.getParticipantTeam();
            if (team == null) {
                challengeView.hideMyTeamSummaryCard();
                if (eventRetrieved && event != null) {
                    if (event.daysToStartEvent() > 0 &&  !event.hasTeamBuildingEnded()) {
                        challengeView.showCreateOrJoinTeamCard();
                    } else {
                        challengeView.hideCreateOrJoinTeamCard();
                    }
                    challengeView.hideInviteTeamMembersCard();
                    challengeView.showFundraisingInvite();
                }
            } else {
                challengeView.hideCreateOrJoinTeamCard();
                challengeView.showMyTeamSummaryCard(team);
                List<Participant> participants = team.getParticipants();
                if (participants != null) {
                  int openSlots = event.getTeamLimit() - participants.size();
                    if (openSlots > 0) {
                        challengeView.showInviteTeamMembersCard(openSlots);
                    }
                    else {
                        challengeView.hideInviteTeamMembersCard();
                    }
                }
                else {
                    challengeView.hideInviteTeamMembersCard();
                }
            }
        }
    }

    private void updateJourneySection() {
        if (eventRetrieved && teamRetrieved) {
            Event event = challengeView.getEvent();
            Team team = challengeView.getParticipantTeam();

            if (event != null) {
                if (event.daysToStartEvent() > 0) {
                    challengeView.hideJourneyActiveView();

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
    public void getTeam(int id) {
        if (id > 0) {
            super.getTeam(id);
        } else {
            teamRetrieved = true;
            updateJourneySection();
            updateTeamSection();
        }
    }

    @Override
    protected void onGetTeamSuccess(Team team) {
        super.onGetTeamSuccess(team);
        teamRetrieved = true;
        challengeView.setParticipantTeam(team);
        updateJourneySection();
        updateTeamSection();
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
        challengeView.showJoinTeamView();
    }

    @Override
    protected void onGetParticipantSuccess(Participant participant) {
        super.onGetParticipantSuccess(participant);
        challengeView.setParticipant(participant);
        participantRetrieved = true;
        updateTeamSection();
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
