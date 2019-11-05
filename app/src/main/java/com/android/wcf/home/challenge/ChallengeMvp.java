package com.android.wcf.home.challenge;

import androidx.annotation.IdRes;
import androidx.annotation.StringRes;

import com.android.wcf.base.BaseMvp;
import com.android.wcf.model.Commitment;
import com.android.wcf.model.Event;
import com.android.wcf.model.Milestone;
import com.android.wcf.model.Team;
import com.android.wcf.settings.EditTextDialogListener;

import java.util.List;

public interface ChallengeMvp {
    interface ChallengeView extends BaseMvp.BaseView {
        void onParticipantIdMissing();

        void showBeforeTeamContainer();

        void showAfterTeamContainer();

        void showJourneyBeforeStartView(Event event);

        void showJourneyActiveView(Event event);

        void showParticipantTeamSummaryCard(Team team);

        void enableShowCreateTeam(boolean enabledFlag);

        void enableJoinExistingTeam(boolean enabledFlag);

        void showCreateNewTeamView();

        void showJoinTeamView();

        void onGetTeamError(Throwable error);

        void showParticipantTeamProfileView();

        void hideInviteTeamMembersCard();

        void showInviteTeamMembersCard(int openSlots);

        void showFundraisingInvite();

        void hideFundraisingInvite();

        void showMilesEditDialog(int currentMiles, final EditTextDialogListener editTextDialogListener);

        void onGetEventError(Throwable error);

        void noActiveEventFound();

        void onCreateParticipantCommitmentToEvent(String participantId, int eventId, Commitment commitment);

        void onUpdateParticipantCommitmentToEvent(String participantId, int eventId, int commitmentSteps);

        void showNetworkErrorMessage(@StringRes int participants_manager_error);

        void showNetworkErrorMessage();

        void onGetParticipantError(Throwable error);

        void onGetParticipantStatsError(Throwable error);

        void onDeleteParticipantError(Throwable error);

        void onGetTeamListError(Throwable error);

        void onGetTeamStatsError(Throwable error);

        void onDeleteTeamError(Throwable error);

        void updateJourneyMilestones(List<Milestone> journeyMilestones);

        void onGetJourneyMilestoneError(Throwable error);
    }

    interface Presenter extends BaseMvp.Presenter {
        void getEvent(int eventId);

        void getTeamsList();

        void getTeam(int teamId);

        void getTeamStats(int teamId);

        void getParticipant(String participantId);

        void getParticipantStats(String participantId);

        void showCreateTeamView();

        void showTeamsToJoinView();

        void onShowMilesCommitmentSelected(int currentMiles, final EditTextDialogListener editTextDialogListener);

        void createParticipantCommitment(String participantId, int eventId, int stepsCommitted);

        void updateParticipantCommitment(int commitmentId, String participantId, int eventId, int stepsCommitted);

        void getTeamParticipantCommitments(final Team team, final int eventId);

        void getTeamParticipantsChallengeProgress(final Team team, final int eventId);

        void getJourneyMilestones(int id);
    }

    interface Host extends BaseMvp.Host {

        void noActiveEventFound();

        void showCreateTeam();

        void showJoinTeam();

        void showTeamChallengeProgress(boolean isTeamLead);

        void showSupportersInvite();

        void showMilestones();
    }
}
