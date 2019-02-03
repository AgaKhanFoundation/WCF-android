package com.android.wcf.home.Leaderboard;

import com.android.wcf.model.Team;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardAdapterPresenter implements LeaderboardAdapterMvp.Presenter {

    private LeaderboardAdapterMvp.View mView;
    List<Team> mTeams = new ArrayList<>();

    public LeaderboardAdapterPresenter(LeaderboardAdapterMvp.View view) {
        this.mView = view;
    }

    @Override
    public void updateLeaderboardData(List<Team> teams) {
        this.mTeams.clear();
        this.mTeams.addAll(teams);
        mView.leaderboardDataUpdated();
    }

    @Override
    public int getTeamsCount() {
        return mTeams != null ? mTeams.size() : 0;
    }

    @Override
    public int getViewType(int pos) {
        return 0;
    }

    @Override
    public Team getTeam(int pos) {
        if (mTeams != null && pos < mTeams.size() && pos >= 0) {
            return mTeams.get(pos);
        }
        return null;
    }
}
