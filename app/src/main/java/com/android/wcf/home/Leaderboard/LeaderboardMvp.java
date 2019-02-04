package com.android.wcf.home.Leaderboard;

import com.android.wcf.base.BaseMvp;
import com.android.wcf.model.LeaderboardTeam;

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
}
