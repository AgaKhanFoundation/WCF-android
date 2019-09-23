package com.android.wcf.home.leaderboard;

import com.android.wcf.base.BaseMvp;

import java.util.List;

public interface LeaderboardAdapterMvp {
    public interface View {
        void updateLeaderboardData(List<LeaderboardTeam> leaderBoard);
        void leaderboardDataUpdated();
    }

    public interface Presenter extends BaseMvp.Presenter {
        void updateLeaderboardData(List<LeaderboardTeam> leaderboard);

        int getTeamsCount();

        LeaderboardTeam getTeam(int pos);

        int getViewType(int pos);

        int getMyTeamId();
    }

    public interface Host {
        void showLeaderboardIsEmpty();
        void hideEmptyLeaderboardView();
    }
}