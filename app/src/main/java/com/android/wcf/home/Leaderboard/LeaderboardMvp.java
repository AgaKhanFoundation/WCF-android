package com.android.wcf.home.Leaderboard;

import com.android.wcf.base.BaseMvp;
import com.android.wcf.model.Team;

import java.util.List;

public interface LeaderboardMvp {
    interface LeaderboardView extends BaseMvp.BaseView {
        void showLeaderboard(List<Team> teams);
        void showLeaderboardIsEmpty();
    }

    interface Presenter extends BaseMvp.Presenter {
       void getTeams();
       void toggleSortMode();
       void sortTeamsBy(String sortColumn);
    }
}
