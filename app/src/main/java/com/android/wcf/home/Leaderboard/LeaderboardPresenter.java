package com.android.wcf.home.Leaderboard;

public class LeaderboardPresenter implements LeaderboardMvp.Presenter {
    private static final String TAG = LeaderboardPresenter.class.getSimpleName();
    LeaderboardMvp.LeaderboardView leaderboardView;

    public LeaderboardPresenter(LeaderboardMvp.LeaderboardView view) {
        this.leaderboardView = view;
    }

    @Override
    public String getTag() {
        return null;
    }
}
