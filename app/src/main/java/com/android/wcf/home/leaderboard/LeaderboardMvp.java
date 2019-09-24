package com.android.wcf.home.leaderboard;

import com.android.wcf.base.BaseMvp;
import com.android.wcf.model.Event;
import com.android.wcf.model.Team;

import java.util.List;

public interface LeaderboardMvp {
    interface LeaderboardView extends BaseMvp.BaseView {
        void showLeaderboard(List<LeaderboardTeam> leaderboard);

        void showMedalWinners(LeaderboardTeam gold, LeaderboardTeam silver, LeaderboardTeam bronze);

        void noMedalWinners();

        void showLeaderboardIsEmpty();
    }

    interface Presenter extends BaseMvp.Presenter {
        void setMyTeamId(int myTeamId);

        void setChallengeStarted(boolean challengeStarted);

        void getLeaderboard(Event event, int myTeamId);
        void refreshLeaderboard(Event event);

        void toggleSortMode();

        void sortTeamsBy(String sortColumn);

        void sortTeamsBy(String sortColumn, int sortMode);
    }

    interface Host extends BaseMvp.Host {
    }
}
