package com.android.wcf.home.challenge;

import com.android.wcf.base.BaseMvp;
import com.android.wcf.model.Event;
import com.android.wcf.model.Team;
import com.android.wcf.settings.EditTextDialogListener;

public interface ChallengeMvp {
    interface ChallengeView extends BaseMvp.BaseView {
        void onParticipantIdMissing();

        void hideJourneyBeforeStartView();

        void showJourneyBeforeStartView(Event event);

        void hideJourneyActiveView();

        void showJourneyActiveView(Event event);

        void showParticipantTeamSummaryCard(Team team);
        void hideParticipantTeamSummaryCard();

        void hideCreateOrJoinTeamView();
        void showCreateOrJoinTeamView();

        void enableShowCreateTeam(boolean enabledFlag);
        void enableJoinExistingTeam(boolean enabledFlag);

        void showCreateNewTeamView();
        void showJoinTeamView();

        void onGetTeamError(Throwable error);

        void hideParticipantTeamProfileView();
        void showParticipantTeamProfileView();

        void hideInviteTeamMembersCard();

        void showInviteTeamMembersCard(int openSlots);

        void showFundraisingInvite();

        void showMilesEditDialog( int currentMiles, final EditTextDialogListener editTextDialogListener);

        void onGetEventError(Throwable error);
    }

    interface Presenter extends BaseMvp.Presenter {
        void getEvent(int eventId);

        void getTeams();

        void getTeam(int teamId);

        void getTeamStats(int teamId);

        void deleteTeam(int teamId);

        void getParticipant(String participantId);

        void getParticipantStats(String participantId);

        void deleteParticipant(String participantId);

        void showCreateTeamView();

        void showTeamsToJoinView();

        void onShowMilesCommitmentSelected( int currentMiles, final EditTextDialogListener editTextDialogListener);

    }

    interface Host extends BaseMvp.Host {

        void showCreateTeam();

        void showJoinTeam();

        void showTeamChallengeProgress(boolean isTeamLead);

        void showSupportersInvite();

    }
}
