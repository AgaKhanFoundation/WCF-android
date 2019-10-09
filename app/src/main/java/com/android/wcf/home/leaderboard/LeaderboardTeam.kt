package com.android.wcf.home.leaderboard

import java.util.Comparator

data internal class LeaderboardTeam(
        var rank: Int = 0,
        var id: Int = 0,
        var name: String = "",
        var image: String? = null,
        var leaderId: String? = null,
        var leaderName: String? = null,
        var participantsCount: Int = 0,
        var stepsPledged: Int = 0,
        var stepsCompleted: Int = 0,
        var amountPledged: Double = 0.0,
        var amountAccrued: Double = 0.0
) {


    companion object {

        const val SORT_COLUMN_NAME = "name"
        const val SORT_COLUMN_AMOUNT_ACCRUED = "amountAccrued"
        const val SORT_COLUMN_DISTANCE_COMPLETED = "distanceCompleted"

        @JvmField
        val SORT_BY_STEPS_COMPLETED = Comparator<LeaderboardTeam> { team0: LeaderboardTeam, team1: LeaderboardTeam -> team1.stepsCompleted - team0.stepsCompleted }

        @JvmField
        val SORT_BY_AMOUNT_ACCRUED = Comparator<LeaderboardTeam> { team0: LeaderboardTeam, team1: LeaderboardTeam -> team1.amountAccrued.compareTo(team0.amountAccrued) }

        @JvmField
        val SORT_BY_NAME_DESCENDING = Comparator<LeaderboardTeam> { team0: LeaderboardTeam, team1: LeaderboardTeam -> team1.name.compareTo(team0.name) }

        @JvmField
        val SORT_BY_NAME_ASCENDING = Comparator<LeaderboardTeam> { team0: LeaderboardTeam, team1: LeaderboardTeam -> team0.name.compareTo(team1.name) }

    }
}
