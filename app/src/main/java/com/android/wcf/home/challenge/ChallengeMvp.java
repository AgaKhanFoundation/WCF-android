package com.android.wcf.home.challenge;

import com.android.wcf.base.BaseMvp;
import com.android.wcf.model.Commitment;
import com.android.wcf.model.Event;
import com.android.wcf.model.Team;
import com.android.wcf.settings.EditTextDialogListener;

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

        void showMilesEditDialog( int currentMiles, final EditTextDialogListener editTextDialogListener);

        void onGetEventError(Throwable error);

        void noActiveEventFound();
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

        void onShowMilesCommitmentSelected( int currentMiles, final EditTextDialogListener editTextDialogListener);

        void createParticipantCommitment(String participantId, int eventId, int stepsCommitted);

        void updateParticipantCommitment(int commitmentId, String participantId, int eventId, int stepsCommitted);
    }

    interface Host extends BaseMvp.Host {

        void noActiveEventFound();

        void showCreateTeam();

        void showJoinTeam();

        void showTeamChallengeProgress(boolean isTeamLead);

        void showSupportersInvite();

    }
}
