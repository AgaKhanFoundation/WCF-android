package com.android.wcf.home.leaderboard;

import com.android.wcf.base.BaseMvp;

import java.util.List;

public interface LeaderboardMvp {
    interface LeaderboardView extends BaseMvp.BaseView {
        void showLeaderboard(List<LeaderboardTeam> leaderboard);
        void showLeaderboardIsEmpty();
    }

    interface Presenter extends BaseMvp.Presenter {
       void getLeaderboard();
       void toggleSortMode();
        void sortTeamsBy(String sortColumn);
        void sortTeamsBy(String sortColumn, int sortMode);
    }
    interface Host extends BaseMvp.Host {
        void setViewTitle(String title);
    }
}
