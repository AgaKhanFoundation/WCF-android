package com.android.wcf.home.Leaderboard;

import com.android.wcf.R;
import com.android.wcf.home.BasePresenter;
import com.android.wcf.model.Constants;
import com.android.wcf.model.LeaderboardTeam;

import java.util.Collections;
import java.util.List;

public class LeaderboardPresenter extends BasePresenter implements LeaderboardMvp.Presenter {
    private static final String TAG = LeaderboardPresenter.class.getSimpleName();
    LeaderboardMvp.LeaderboardView leaderboardView;
    private List<LeaderboardTeam> leaderboard;

    private String currentSortColumn = LeaderboardTeam.SORT_COLUMN_AMOUNT_ACCRUED;
    private int currentSortMode = Constants.SORT_MODE_ASCENDING;

    public LeaderboardPresenter(LeaderboardMvp.LeaderboardView view) {
        this.leaderboardView = view;
    }

    @Override
    public String getTag() {
        return null;
    }

    @Override
    protected void onGetLeaderboardSuccess(List<LeaderboardTeam> leaderboard) {
        super.onGetLeaderboardSuccess(leaderboard);
        this.leaderboard = leaderboard;
        sortTeamsBy(currentSortColumn);
    }

    @Override
    protected void onGetLeaderboardError(Throwable error) {
        super.onGetLeaderboardError(error);
        leaderboardView.showError(R.string.teams_manager_error, error.getMessage());
    }

    @Override
    public void toggleSortMode() {
        currentSortMode = (currentSortMode == Constants.SORT_MODE_ASCENDING ? Constants.SORT_MODE_DESCENDING : Constants.SORT_MODE_ASCENDING);
        sortTeamsBy(currentSortColumn);

    }

    @Override
    public void sortTeamsBy(String sortColumn) {
        currentSortColumn = sortColumn;

        if (leaderboard == null || leaderboard.size() == 0) {
            return;
        }

        if (sortColumn == null) {
            return;
        }
        if (LeaderboardTeam.SORT_COLUMN_DISTANCE_COMPLETED.equals(sortColumn)) {
            Collections.sort(leaderboard, LeaderboardTeam.SORT_BY_STEPS_COMPLETED);
        } else if (LeaderboardTeam.SORT_COLUMN_AMOUNT_ACCRUED.equals(sortColumn)) {
            Collections.sort(leaderboard, LeaderboardTeam.SORT_BY_AMOUNT_ACCRUED);
        } else if (LeaderboardTeam.SORT_COLUMN_NAME.equals(sortColumn)) {
            if (currentSortMode == Constants.SORT_MODE_DESCENDING) {
                Collections.sort(leaderboard, LeaderboardTeam.SORT_BY_NAME_DESCENDING);
            } else {
                Collections.sort(leaderboard, LeaderboardTeam.SORT_BY_NAME_ASCENDING);
            }
        }

        leaderboardView.showLeaderboard(leaderboard);
    }

    @Override
    public void sortTeamsBy(String sortColumn, int sortOrder) {
        currentSortMode = (sortOrder == Constants.SORT_MODE_ASCENDING ? Constants.SORT_MODE_ASCENDING : Constants.SORT_MODE_DESCENDING);
        sortTeamsBy(sortColumn);
    }
}
