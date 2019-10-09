package com.android.wcf.home.leaderboard;

import com.android.wcf.R;
import com.android.wcf.home.BasePresenter;
import com.android.wcf.model.Constants;
import com.android.wcf.model.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class LeaderboardPresenter extends BasePresenter implements LeaderboardMvp.Presenter {
    private static final String TAG = LeaderboardPresenter.class.getSimpleName();

    LeaderboardMvp.LeaderboardView leaderboardView;
    private List<LeaderboardTeam> leaderboard;
    private int myTeamId = 0;
    boolean challengeStarted = false;

    private LeaderboardTeam teamGold;
    private LeaderboardTeam teamSilver;
    private LeaderboardTeam teamBronze;

    private String currentSortColumn = LeaderboardTeam.SORT_COLUMN_DISTANCE_COMPLETED;
    private int currentSortMode = Constants.SORT_MODE_ASCENDING;

    public LeaderboardPresenter(LeaderboardMvp.LeaderboardView view) {
        this.leaderboardView = view;
    }

    @Override
    public String getTag() {
        return null;
    }

    @Override
    public void getLeaderboard(Event event, int myTeamId) {
        setChallengeStarted(event.hasChallengeStarted());
        if (!challengeStarted) {
            leaderboardView.showLeaderboardIsEmpty();
            return;
        }
        setMyTeamId(myTeamId);
        super.getLeaderboard();
    }

    @Override
    public void refreshLeaderboard(Event event) {
        setChallengeStarted(event.hasChallengeStarted());
        if (!challengeStarted) {
            leaderboardView.showLeaderboardIsEmpty();
            return;
        }
        super.getLeaderboard();
    }

    @Override
    public void setMyTeamId(int myTeamId) {
        this.myTeamId = myTeamId;
    }

    @Override
    public void setChallengeStarted(boolean challengeStarted) {
        this.challengeStarted = challengeStarted;

        // enable for test/showing leaderboard while challenge has not started
        /*
        if (!challengeStarted) {
            int rand = new Random().nextInt(3);
            this.challengeStarted = (rand == 0 ? true : false);
        }
        */
    }

    @Override
    protected void onGetLeaderboardSuccess(List<LeaderboardTeam> leaderboard) {
        super.onGetLeaderboardSuccess(leaderboard);
        this.leaderboard = leaderboard;
        getMedalWinners();
        if (challengeStarted && teamGold != null) {
            currentSortColumn = LeaderboardTeam.SORT_COLUMN_DISTANCE_COMPLETED;
        }
        else {
            currentSortColumn = LeaderboardTeam.SORT_COLUMN_NAME;
        }
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

        if (challengeStarted) {
            leaderboardView.showMedalWinners(teamGold, teamSilver, teamBronze);
        }
        else {
            leaderboardView.showMedalWinners(null, null, null);
        }

        leaderboardView.showLeaderboard(leaderboard);
    }

    @Override
    public void sortTeamsBy(String sortColumn, int sortOrder) {
        currentSortMode = (sortOrder == Constants.SORT_MODE_ASCENDING ? Constants.SORT_MODE_ASCENDING : Constants.SORT_MODE_DESCENDING);
        sortTeamsBy(sortColumn);
    }

    private void getMedalWinners() {
        if (challengeStarted) {
            List<LeaderboardTeam> sortedByDistance = new ArrayList<>(leaderboard);

            LeaderboardTeam team = null;

            if (sortedByDistance.size() > 0) {
                team = sortedByDistance.get(0);

                if (team.getStepsCompleted() == 0) {
                    teamGold = null;
                    teamSilver = null;
                    teamBronze = null;
                    return;
                }
                teamGold = team;
            }
            if (sortedByDistance.size() > 1) {
                team = sortedByDistance.get(1);

                if (team.getStepsCompleted() == 0) {
                    teamSilver = null;
                    teamBronze = null;
                    return;
                }
                teamSilver = team;

            }
            if (sortedByDistance.size() > 2) {
                team = sortedByDistance.get(2);

                if (team.getStepsCompleted() == 0) {
                    teamBronze = null;
                    return;
                }
                teamBronze = team;
            }
        }
        else {
            teamGold = null;
            teamSilver = null;
            teamBronze = null;
        }
    }

    public int getMyTeamId() {
        return myTeamId;
    }

}
