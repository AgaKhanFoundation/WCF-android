package com.android.wcf.home.challenge;

import com.android.wcf.application.DataHolder;
import com.android.wcf.home.BasePresenter;
import com.android.wcf.model.Commitment;
import com.android.wcf.model.Event;
import com.android.wcf.model.Milestone;
import com.android.wcf.model.Notification;
import com.android.wcf.model.Participant;
import com.android.wcf.model.Stats;
import com.android.wcf.model.Team;
import com.android.wcf.settings.EditTextDialogListener;

import java.io.IOException;
import java.util.List;
import java.util.Map;
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

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public void onShowMilesCommitmentSelected(int currentMiles, EditTextDialogListener editTextDialogListener) {
        challengeView.showMilesEditDialog(currentMiles, editTextDialogListener);
    }

    @Override
    public void getParticipant(String participantId) {
        challengeView.showLoadingProgressView("getParticipant");
        super.getParticipant(participantId);
    }

    @Override
    public void getParticipantStats(String participantId) {
        challengeView.showLoadingProgressView("getParticipantStats");
        super.getParticipantStats(participantId);
    }

    @Override
    public void getEvent(int eventId) {
        if (eventId > 0) {
            challengeView.showLoadingProgressView("getEvent");
            super.getEvent(eventId);
        } else {
            eventRetrieved = true;
            updateChallengeView();

        }
    }

    // updateJourneySection();
    // updateTeamSection();


    @Override
    protected void onGetEventSuccess(Event event) {
        super.onGetEventSuccess(event);
        eventRetrieved = true;
        challengeView.cacheEvent(event);
        updateChallengeView();
        challengeView.hideLoadingProgressView("onGetEventSuccess");
    }

    @Override
    protected void onGetEventError(Throwable error) {
        super.onGetEventError(error);
        challengeView.onGetEventError(error);
    }

    private void updateChallengeView() {
        if (!challengeView.isAttached()) {
            return;
        }

        if (eventRetrieved && teamRetrieved && participantRetrieved) {

            Event event = challengeView.getEvent();
            Team team = challengeView.getParticipantTeam();
            Participant participant = challengeView.getParticipant();

            if (event == null) {
                challengeView.noActiveEventFound();
                return;
            }

            if (team == null) {
                challengeView.showBeforeTeamContainer();
                return;
            }

            if (event.daysToStartEvent() >= 0) {
                challengeView.showJourneyBeforeStartView(event);
            } else {
                challengeView.showJourneyActiveView(event);
            }

            updateTeamSection(event, team, participant);

            challengeView.showAfterTeamContainer();

            if (participant != null && event != null) {
                getParticipantNotifications(participant.getFbId(), event.getId());
            }
        }
    }

    private void updateTeamSection(Event event, Team team, Participant participant) {

        if (!event.hasChallengeEnded()) {
            challengeView.showFundraisingInvite();
        } else {
            challengeView.hideFundraisingInvite();
        }

        challengeView.showParticipantTeamProfileView();
        challengeView.showParticipantTeamSummaryCard(team);

        boolean teamBuildingEnded = event.hasTeamBuildingEnded();

        boolean showTeamInvite = false;
        int openSlots = 0;
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

    @Override
    public void getTeam(int id) {
        if (id > 0) {
            challengeView.showLoadingProgressView("getTeam id=" + id);
            super.getTeam(id);
        } else {
            teamRetrieved = true;
            updateChallengeView();
        }
    }

    @Override
    protected void onGetTeamSuccess(Team team) {
        super.onGetTeamSuccess(team);
        challengeView.cacheParticipantTeam(team);
        getTeamParticipantsInfoFromServer(team);
//        onGetTeamParticipantsInfoSuccess(team);
        challengeView.hideLoadingProgressView("onGetTeamSuccess");
    }

    @Override
    protected void onGetTeamError(Throwable error) {
        super.onGetTeamError(error);
        if (error instanceof IOException) {
            challengeView.showNetworkErrorMessage();
        } else {
            if (error instanceof NoSuchElementException) {
                challengeView.onGetTeamError(error);
                teamRetrieved = true;
                updateChallengeView();
            }
        }
    }

    @Override
    protected void onGetTeamCommitmentSuccess(Map<String, Commitment> participantCommitmentsMap) {
        Participant cachedParticipant = DataHolder.getParticipant();

        for (Participant participant : DataHolder.getParticipantTeam().getParticipants()) {
            Commitment commitment = participantCommitmentsMap.get(participant.getId() + "");
            participant.setCommitment(commitment);

            if (cachedParticipant != null && participant.getParticipantId().equals(cachedParticipant.getParticipantId())) {
                cachedParticipant.setCommitment(commitment);
            }
        }
        teamRetrieved = true;
        updateChallengeView();
    }

    @Override
    protected void onGetTeamChallengeProgressSuccess(Map<String, Stats> teamChallengeProgress) {

        Participant cachedParticipant = DataHolder.getParticipant();

        for (Participant participant : DataHolder.getParticipantTeam().getParticipants()) {
            Stats stats = teamChallengeProgress.get(participant.getParticipantId());
            participant.setStats(stats);

            if (cachedParticipant != null && participant.getParticipantId().equals(cachedParticipant.getParticipantId())) {
                participant.setStats(stats);
            }
        }
        teamRetrieved = true;
        updateChallengeView();
    }

    @Override
    protected void onGetTeamParticipantsInfoSuccess(Team team){
        challengeView.cacheParticipantTeam(team);
        Event event = challengeView.getEvent();
        getTeamParticipantCommitments(team, event.getId());
        getTeamParticipantsChallengeProgress(team, event.getId());
    }

    @Override
    protected void onGetTeamParticipantsInfoError(Throwable error) {
        super.onGetTeamParticipantsInfoError(error);
        if (error instanceof IOException) {
            challengeView.showNetworkErrorMessage();
        } else {
            teamRetrieved = true;
            updateChallengeView();
        }
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
        challengeView.onGetTeamListError(error);

    }

    @Override
    protected void onGetTeamStatsSuccess(Stats stats) {
        super.onGetTeamStatsSuccess(stats);
        this.teamStats = stats;
    }

    @Override
    protected void onGetTeamStatsError(Throwable error) {
        super.onGetTeamStatsError(error);
        challengeView.onGetTeamStatsError(error);
    }

    @Override
    protected void onDeleteTeamSuccess(List<Integer> result) {
        super.onDeleteTeamSuccess(result);
        getTeamsList();
    }

    @Override
    protected void onDeleteTeamError(Throwable error) {
        super.onDeleteTeamError(error);
        challengeView.onDeleteTeamError(error);
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
        updateChallengeView();
        challengeView.hideLoadingProgressView("onGetParticipantSuccess");
    }

    @Override
    protected void onGetParticipantError(Throwable error) {
        super.onGetParticipantError(error);
        challengeView.onGetParticipantError(error);
    }

    @Override
    protected void onGetParticipantStatsSuccess(Stats stats) {
        super.onGetParticipantStatsSuccess(stats);
        participantStats = stats;
        challengeView.hideLoadingProgressView("onGetParticipantStatsSuccess");
    }

    @Override
    protected void onGetParticipantStatsError(Throwable error) {
        super.onGetParticipantStatsError(error);
        challengeView.onGetParticipantStatsError(error);
    }

    @Override
    protected void onDeleteParticipantSuccess(Integer count) {
        super.onDeleteParticipantSuccess(count);
    }

    @Override
    protected void onDeleteParticipantError(Throwable error) {
        super.onDeleteParticipantError(error);
        challengeView.onDeleteParticipantError(error);
    }

    @Override
    protected void onCreateParticipantCommitmentSuccess(String participantId, Commitment commitment) {
        super.onCreateParticipantCommitmentSuccess(participantId, commitment);
        challengeView.onCreateParticipantCommitmentToEvent(participantId, commitment.getEventId(), commitment);
    }

    @Override
    protected void onUpdateParticipantCommitmentSuccess(String participantId, int eventId, int commitmentSteps) {
        super.onUpdateParticipantCommitmentSuccess(participantId, eventId, commitmentSteps);
        challengeView.onUpdateParticipantCommitmentToEvent(participantId, eventId, commitmentSteps);
    }

    @Override
    public void getJourneyMilestones(int eventId) {
        super.getJourneyMilestones(eventId);
    }

    protected void onGetJourneyMilestonesSuccess(List<Milestone> journeyMilestones) {
        super.onGetJourneyMilestonesSuccess(journeyMilestones);
        challengeView.updateJourneyMilestones(journeyMilestones);
    }

    protected void onGetJourneyMilestoneError(Throwable error) {
        super.onGetJourneyMilestoneError(error);
        challengeView.onGetJourneyMilestoneError(error);
    }

    protected void onParticipantNotificationsRetrieved(List<Notification> notifications) {
        super.onParticipantNotificationsRetrieved((notifications));
        challengeView.onParticipantNotificationsRetrieved(notifications);
    }

    protected void onGetParticipantNotificationsError(Throwable error) {
        super.onGetParticipantNotificationsError(error);
        challengeView.onGetParticipantNotificationsError(error);
    }
}
