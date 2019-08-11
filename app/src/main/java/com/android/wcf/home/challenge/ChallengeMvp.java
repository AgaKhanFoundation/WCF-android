package com.android.wcf.home.challenge;

import android.net.Uri;

import com.android.wcf.base.BaseMvp;
import com.android.wcf.model.Event;
import com.android.wcf.model.Team;

import java.util.List;

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

        void showMyTeamCard(Team team);

        void hideTeamCard();

        void showTeamList();

        void enableShowCreateTeam(boolean enabledFlag);

        void enableJoinExistingTeam(boolean enabledFlag);

        void participantJoinedTeam(String participantId, int teamId);

    }

    interface Presenter extends BaseMvp.Presenter {
        void getEvent(int eventId);

        void createTeam(String teamName, String teamLeadParticipantId, boolean teamVisibility);

        void getTeams();

        void getTeam(int teamId);

        void getTeamStats(int teamId);

        void deleteTeam(int teamId);

        void createParticipant(String participantId);

        void getParticipant(String participantId);

        void assignParticipantToTeam(String participantId, int teamId);

        void getParticipantStats(String participantId);

        void deleteParticipant(String participantId);

        void showCreateTeamView();

        void showTeamsToJoinView();

    }

    interface Host extends BaseMvp.Host {

        void showCreateTeam();
    }
}
