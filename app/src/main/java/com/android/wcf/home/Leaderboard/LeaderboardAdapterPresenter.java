package com.android.wcf.home.Leaderboard;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardAdapterPresenter implements LeaderboardAdapterMvp.Presenter {

    private LeaderboardAdapterMvp.View mView;
    List<LeaderboardTeam> mLeaderboard = new ArrayList<>();

    public LeaderboardAdapterPresenter(LeaderboardAdapterMvp.View view) {
        this.mView = view;
    }

    @Override
    public void updateLeaderboardData(List<LeaderboardTeam> leaderboard) {
        this.mLeaderboard.clear();
        this.mLeaderboard.addAll(leaderboard);
        mView.leaderboardDataUpdated();
    }

    @Override
    public int getTeamsCount() {
        return mLeaderboard != null ? mLeaderboard.size() : 0;
    }

    @Override
    public int getViewType(int pos) {
        return 0;
    }

    @Override
    public LeaderboardTeam getTeam(int pos) {
        if (mLeaderboard != null && pos < mLeaderboard.size() && pos >= 0) {
            return mLeaderboard.get(pos);
        }
        return null;
    }
}
