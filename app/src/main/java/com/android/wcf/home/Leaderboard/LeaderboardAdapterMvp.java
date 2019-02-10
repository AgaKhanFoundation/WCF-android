package com.android.wcf.home.Leaderboard;

import com.android.wcf.model.dto.LeaderboardTeam;

import java.util.List;

public interface LeaderboardAdapterMvp {
    public interface View {
        void leaderboardDataUpdated();
    }

    public interface Presenter {
        void updateLeaderboardData(List<LeaderboardTeam> leaderboard);

        int getTeamsCount();

        LeaderboardTeam getTeam(int pos);

        int getViewType(int pos);
    }

    public interface Host {
    }
}