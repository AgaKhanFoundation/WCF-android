package com.android.wcf.home.campaign;

import com.android.wcf.model.Team;

import java.util.List;

public interface TeamsAdapterMvp {
    public interface View {
        void teamsDataUpdated();
        void teamRowSelected(int pos);

        void clearTeamSelectionPosition();

        void updateTeamsData(List<Team> teams);

        Team getSelectedTeam();
    }

    public interface Presenter {
        void updateTeamsData(List<Team> teams);
        int getTeamsCount();
        Team getTeam(int pos);
        int getViewType (int pos);
        void onTeamSelected(int pos);
        int getSelectedTeamPosition();
        void clearTeamSelectionPosition();
        void setSelectedTeamPosition(int pos);
        Team getSelectedTeam();

        void teamRowSelected(int pos);
    }

    public interface Host {
        void teamRowSelected(int pos);
    }
}