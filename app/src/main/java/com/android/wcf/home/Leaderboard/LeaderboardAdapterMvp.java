package com.android.wcf.home.Leaderboard;

import com.android.wcf.model.Team;

import java.util.List;

public interface LeaderboardAdapterMvp {
    public interface View {
        void leaderboardDataUpdated();
    }

    public interface Presenter {
        void updateLeaderboardData(List<Team> teams);
        int getTeamsCount();
        Team getTeam(int pos);
        int getViewType (int pos);
    }

    public interface Host {
    }
}