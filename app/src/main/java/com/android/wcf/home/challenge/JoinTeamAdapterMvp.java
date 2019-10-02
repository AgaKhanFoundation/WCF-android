package com.android.wcf.home.challenge;

import androidx.recyclerview.widget.RecyclerView;

import com.android.wcf.model.Team;

import java.util.List;

public interface JoinTeamAdapterMvp {
    public interface View {
        void teamsDataUpdated();

        void teamRowSelected(int pos);

        void clearTeamSelectionPosition();

        void updateTeamsData(List<Team> teams);

        Team getSelectedTeam();

        void teamRowUnselected(int position);

    }

    public interface Presenter {
        void updateTeamsData(List<Team> teams);

        int getTeamsCount();

        Team getTeam(int pos);

        void onTeamSelected(int pos);

        int getSelectedTeamPosition();

        void clearTeamSelectionPosition();

        Team getSelectedTeam();

        void teamRowSelected(int pos);

        boolean isTeamSelectable(int pos, int teamSizeLimit);
    }

    public interface Host {
        void teamRowSelected(int pos);
        void removeFocusFromSearch();
    }
}