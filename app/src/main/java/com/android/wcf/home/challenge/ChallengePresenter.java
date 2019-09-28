package com.android.wcf.home.challenge;

import com.android.wcf.R;
import com.android.wcf.home.BasePresenter;
import com.android.wcf.model.Event;
import com.android.wcf.model.Participant;
import com.android.wcf.model.Stats;
import com.android.wcf.model.Team;
import com.android.wcf.settings.EditTextDialogListener;

import java.util.List;
import java.util.NoSuchElementException;

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
    public void onShowMilesCommitmentSelected(int currentMiles, EditTextDialogListener editTextDialogListener) {
        challengeView.showMilesEditDialog(currentMiles, editTextDialogListener);
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
        challengeView.cacheEvent(event);
        eventRetrieved = true;
        updateJourneySection();
        updateTeamSection();
    }

    @Override
    protected void onGetEventError(Throwable error) {
        super.onGetEventError(error);
        challengeView.onGetEventError(error);
    }

    private void updateTeamSection() {
        if (eventRetrieved && teamRetrieved && participantRetrieved) {
            Event event = challengeView.getEvent();

            if (!event.hasChallengeEnded()) {
                challengeView.showFundraisingInvite();
            }
            else {
                challengeView.hideFundraisingInvite();
            }

            boolean teamBuildingEnded = event.hasTeamBuildingEnded();

            Team team = challengeView.getParticipantTeam();
            if (team == null) {
                if (eventRetrieved && event != null) {
                    if (!teamBuildingEnded) {
                        challengeView.showCreateOrJoinTeamView();
                    } else {
                        challengeView.hideCreateOrJoinTeamView();
                    }
                    challengeView.hideInviteTeamMembersCard();
                    challengeView.hideParticipantTeamProfileView();
                    challengeView.showParticipantTeamSummaryCard(team);
                }
            } else {
                challengeView.hideCreateOrJoinTeamView();
                challengeView.showParticipantTeamProfileView();
                challengeView.showParticipantTeamSummaryCard(team);

                boolean showTeamInvite = false;
                int openSlots = 0;
                if (eventRetrieved && event != null) {
                    Participant participant = challengeView.getParticipant();
                    if (participant != null) {
                        if (team.isTeamLeader(participant.getParticipantId())) {
                            if (!teamBuildingEnded) {
                                List<Participant> participants = team.getParticipants();
                                if (participants != null) {
                                    openSlots = event.getTeamLimit() - participants.size();
                                    if (openSlots > 0) {
                                        showTeamInvite = true;
                                    }
                                }
                            }
                        }
                    }
                    if (showTeamInvite && openSlots > 0) {
                        challengeView.showInviteTeamMembersCard(openSlots);
                    } else {
                        challengeView.hideInviteTeamMembersCard();
                    }
                }
            }
        }
    }

    private void updateJourneySection() {
        if (eventRetrieved && teamRetrieved) {
            Event event = challengeView.getEvent();
            Team team = challengeView.getParticipantTeam();

            if (event != null) {
                if (event.daysToStartEvent() >= 0) {
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
        challengeView.cacheParticipantTeam(team);
        getTeamParticipantsInfoFromFacebook(team);
    }

    @Override
    protected void onGetTeamError(Throwable error) {
        super.onGetTeamError(error);
        if (error instanceof NoSuchElementException) {
            challengeView.onGetTeamError(error);
            teamRetrieved = true;
            updateJourneySection();
            updateTeamSection();
        }
    }

    @Override
    protected void onGetTeamParticipantsInfoSuccess(Team team){
        challengeView.cacheParticipantTeam(team);
        teamRetrieved = true;
        if (challengeView.isAttached()) {
            updateJourneySection();
            updateTeamSection();
        }
    }

    @Override
    protected void onGetTeamParticipantsInfoError(Throwable error) {
        super.onGetTeamParticipantsInfoError(error);
        teamRetrieved = true;
        updateJourneySection();
        updateTeamSection();
    }

    @Override
    protected void onGetTeamListSuccess(List<Team> teams) {
        super.onGetTeamListSuccess(teams);
        challengeView.cacheTeamList(teams);
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
        challengeView.cacheParticipant(participant);
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
