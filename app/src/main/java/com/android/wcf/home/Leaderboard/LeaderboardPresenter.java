package com.android.wcf.home.Leaderboard;

import com.android.wcf.R;
import com.android.wcf.home.BasePresenter;
import com.android.wcf.model.Team;

import java.util.List;

public class LeaderboardPresenter extends BasePresenter implements LeaderboardMvp.Presenter {
    private static final String TAG = LeaderboardPresenter.class.getSimpleName();
    LeaderboardMvp.LeaderboardView leaderboardView;
    private List<Team> teams;

    public static final String SORT_COLUMN_NAME = "name";
    public static final String SORT_COLUMN_AMOUNT = "amount";
    public static final String SORT_COLUMN_MILES = "miles";

    public static final int SORT_MODE_ASCENDING = 0;
    public static final int SORT_MODE_DESCENDING = 1;

    private String currentSortColumn = SORT_COLUMN_AMOUNT;
    private int currentSortMode = SORT_MODE_ASCENDING;


    public LeaderboardPresenter(LeaderboardMvp.LeaderboardView view) {
        this.leaderboardView = view;
    }

    @Override
    public String getTag() {
        return null;
    }

    @Override
    protected void onGetTeamListSuccess(List<Team> teams) {
        super.onGetTeamListSuccess(teams);
        this.teams = teams;
        sortTeamsBy(currentSortColumn);
    }

    @Override
    protected void onGetTeamListError(Throwable error) {
        super.onGetTeamListError(error);
        leaderboardView.showError(R.string.teams_manager_error, error.getMessage());
    }

    @Override
    public void toggleSortMode() {
        currentSortMode = (currentSortMode == SORT_MODE_ASCENDING ? SORT_MODE_DESCENDING : SORT_MODE_ASCENDING);
        sortTeamsBy(currentSortColumn);

    }

    @Override
    public void sortTeamsBy(String sortColumn) {
        currentSortColumn = sortColumn;
        if (teams == null || teams.size() == 0) {
            leaderboardView.showLeaderboardIsEmpty();
            return;
        }

        //TODO implement sorting of teams based on the column

        leaderboardView.showLeaderboard(teams);
    }

    @Override
    public void sortTeamsBy(String sortColumn, int sortOrder) {
        currentSortMode = (sortOrder == SORT_MODE_ASCENDING ? SORT_MODE_ASCENDING : SORT_MODE_DESCENDING);
        sortTeamsBy(sortColumn);
    }
}
