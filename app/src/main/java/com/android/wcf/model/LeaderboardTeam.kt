package com.android.wcf.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LeaderboardTeam(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("name") var name: String = "",
        @SerializedName("image") var image: String = "",
        @SerializedName("creator_id") var leaderId: String = "",
        @SerializedName("hidden") var hidden: Boolean = false,
        @SerializedName("distance") var distance: Int = 0,
        @SerializedName("commitment") var commitment: Int = 0
) : Parcelable {
    var rank: Int = 0
    var amountPledged: Double = 0.0
    var amountAccrued: Double = 0.0

        companion object {

        const val SORT_COLUMN_NAME = "name"
        const val SORT_COLUMN_AMOUNT_ACCRUED = "amountAccrued"
        const val SORT_COLUMN_DISTANCE_COMPLETED = "distanceCompleted"

        @JvmField
        val SORT_BY_STEPS_COMPLETED = Comparator<LeaderboardTeam> { team0: LeaderboardTeam, team1: LeaderboardTeam -> team1.distance - team0.distance }

        @JvmField
        val SORT_BY_AMOUNT_ACCRUED = Comparator<LeaderboardTeam> { team0: LeaderboardTeam, team1: LeaderboardTeam -> team1.amountAccrued.compareTo(team0.amountAccrued) }

        @JvmField
        val SORT_BY_NAME_DESCENDING = Comparator<LeaderboardTeam> { team0: LeaderboardTeam, team1: LeaderboardTeam -> team1.name.compareTo(team0.name) }

        @JvmField
        val SORT_BY_NAME_ASCENDING = Comparator<LeaderboardTeam> { team0: LeaderboardTeam, team1: LeaderboardTeam -> team0.name.compareTo(team1.name) }

    }
}

/*
var leaderId: String? = null,
var leaderName: String? = null,
var participantsCount: Int = 0,
*/
