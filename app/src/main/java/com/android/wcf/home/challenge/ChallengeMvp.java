package com.android.wcf.home.challenge;

import com.android.wcf.base.BaseMvp;
import com.android.wcf.model.Event;
import com.android.wcf.model.Team;

public interface ChallengeMvp {
    interface ChallengeView extends BaseMvp.BaseView {

        void hideJourneyBeforeStartView();

        void showJourneyBeforeStartView(Event event);

        void hideJourneyActiveView();

        void showJourneyActiveView(Event event);

        void hideCreateOrJoinTeamCard();

        void showCreateOrJoinTeamCard();

        void onParticipantIdMissing();

        void showCreateNewTeamView();

        void showJoinTeamView();

        void showMyTeamSummaryCard(Team team);

        void hideMyTeamSummaryCard();

        void enableShowCreateTeam(boolean enabledFlag);

        void enableJoinExistingTeam(boolean enabledFlag);

        void hideInviteTeamMembersCard();

        void showInviteTeamMembersCard(int openSlots);

        void showFundraisingInvite();
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

    }

    interface Host extends BaseMvp.Host {

        void showCreateTeam();

        void showJoinTeam();

    }
}
