package com.android.wcf.home.Campaign;

import com.android.wcf.base.BaseMvp;

public interface CampaignMvp {
    interface CampaignView extends BaseMvp.BaseView {

    }

    interface Presenter extends BaseMvp.Presenter {
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

    }
}
