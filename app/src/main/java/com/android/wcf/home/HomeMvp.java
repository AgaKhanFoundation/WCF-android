package com.android.wcf.home;

import com.android.wcf.base.BaseMvp;

public interface HomeMvp {

    interface HomeView extends BaseMvp.BaseView {

    }

    interface HomePresenter {
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
