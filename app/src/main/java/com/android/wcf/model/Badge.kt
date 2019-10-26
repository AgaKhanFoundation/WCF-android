package com.android.wcf.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

enum class BadgeType {
    DAILY_10K_STEPS, DAILY_15K_STEPS, DAILY_20K_STEPS, DAILY_25K_STEPS,
    STREAK_10K_10_DAYS, STREAK_10K_20_DAYS, STREAK_10K_30_DAYS, STREAK_10K_40_DAYS, STREAK_10K_50_DAYS,
    STREAK_10K_60_DAYS, STREAK_10K_70_DAYS, STREAK_10K_80_DAYS, STREAK_10K_90_DAYS, STREAK_10K_100_DAYS,
    TEAM_GOAL_25_PCT, TEAM_GOAL_50_PCT, TEAM_GOAL_75_PCT, TEAM_GOAL_100_PCT,
    LEVEL_SILVER, LEVEL_GOLD, LEVEL_PLATINUM, LEVEL_CHAMPION
}


@Parcelize
data class Badge(
        @SerializedName("type")  var type: BadgeType = BadgeType.DAILY_10K_STEPS,
        @SerializedName("title") var title: String = "",
        @SerializedName("date") var date: Int = 0
) : Parcelable

