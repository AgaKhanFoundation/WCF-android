package com.android.wcf.home.Campaign;

import com.android.wcf.base.BaseMvp;
import com.android.wcf.model.Event;
import com.android.wcf.model.Team;

import java.util.List;

public interface CampaignMvp {
    interface CampaignView extends BaseMvp.BaseView {

        void onFacebookIdMissing();

        void showJourney(Event event);

        void enableShowCreateTeam(boolean enabledFlag);

        void enableJoinExistingTeam(boolean enabledFlag);

        void showCreateNewTeamView();

        void showTeamList(List<Team> teams);

        void participantJoinedTeam(String fbid, int teamId);

        void hideCreateNewTeamView();

        void teamCreated(Team team);
    }

    interface Presenter extends BaseMvp.Presenter {
        void getEvent(int eventId);

        void createTeam(String teamName);

        void getTeams();

        void getTeam(int teamId);

        void getTeamStats(int teamId);

        void deleteTeam(int teamId);

        void createParticipant(String fbid);

        void getParticipant(String fbid);

        void assignParticipantToTeam(String fbid, int teamId);

        void getParticipantStats(String fbid);

        void deleteParticipant(String fbid);

        void showCreateTeamClick();

        void showTeamsToJoinClick();

        void createTeamClick(String teamName);

        void cancelCreateTeamClick();
    }
}
