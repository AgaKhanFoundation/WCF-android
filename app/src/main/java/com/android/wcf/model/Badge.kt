package com.android.wcf.model


import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.android.wcf.R
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

enum class BadgeType (@DrawableRes val imageRes:Int, @StringRes val titleRes:Int, val threshold:Int) {
    UNKNOWN(R.drawable.ic_badge_distance, R.string.badge_title_unknown, 0)
    ,DISTANCE_COMPLETED_50(R.drawable.ic_badge_distance, R.string.badge_title_distance_completed, 50)
    ,DISTANCE_COMPLETED_100(R.drawable.ic_badge_distance, R.string.badge_title_distance_completed, 100)
    ,DISTANCE_COMPLETED_150(R.drawable.ic_badge_distance, R.string.badge_title_distance_completed, 150)
    ,DISTANCE_COMPLETED_200(R.drawable.ic_badge_distance, R.string.badge_title_distance_completed, 300)
    ,DISTANCE_COMPLETED_250(R.drawable.ic_badge_distance, R.string.badge_title_distance_completed, 250)
    ,DISTANCE_COMPLETED_300(R.drawable.ic_badge_distance, R.string.badge_title_distance_completed, 300)
    ,DISTANCE_COMPLETED_350(R.drawable.ic_badge_distance, R.string.badge_title_distance_completed, 350)
    ,DISTANCE_COMPLETED_400(R.drawable.ic_badge_distance, R.string.badge_title_distance_completed, 400)
    ,DISTANCE_COMPLETED_450(R.drawable.ic_badge_distance, R.string.badge_title_distance_completed, 450)
    ,DISTANCE_COMPLETED_500(R.drawable.ic_badge_distance, R.string.badge_title_distance_completed, 500)
    ,DAILY_10K_STEPS(R.drawable.ic_badge_10k_steps, R.string.badge_title_daily_10k, 10000)
    ,DAILY_15K_STEPS(R.drawable.ic_badge_15k_steps, R.string.badge_title_daily_15k, 15000)
    ,DAILY_20K_STEPS(R.drawable.ic_badge_20k_steps, R.string.badge_title_daily_20k, 20000)
    ,DAILY_25K_STEPS(R.drawable.ic_badge_25k_steps, R.string.badge_title_daily_25k, 25000)
    ,STREAK_10_DAYS_10K_STEPS_PER_DAY(R.drawable.ic_badge_streak_10k, R.string.badge_title_streak_10_days_10k_each, 10)
    ,STREAK_20_DAYS_10K_STEPS_PER_DAY(R.drawable.ic_badge_streak_10k, R.string.badge_title_streak_20_days_10k_each, 20)
    ,STREAK_30_DAYS_10K_STEPS_PER_DAY(R.drawable.ic_badge_streak_10k, R.string.badge_title_streak_30_days_10k_each, 30)
    ,STREAK_40_DAYS_10K_STEPS_PER_DAY(R.drawable.ic_badge_streak_10k, R.string.badge_title_streak_40_days_10k_each, 40)
    ,STREAK_50_DAYS_10K_STEPS_PER_DAY(R.drawable.ic_badge_streak_10k, R.string.badge_title_streak_50_days_10k_each, 50)
    ,STREAK_60_DAYS_10K_STEPS_PER_DAY(R.drawable.ic_badge_streak_10k, R.string.badge_title_streak_60_days_10k_each, 60)
    ,STREAK_70_DAYS_10K_STEPS_PER_DAY(R.drawable.ic_badge_streak_10k, R.string.badge_title_streak_70_days_10k_each, 70)
    ,STREAK_80_DAYS_10K_STEPS_PER_DAY(R.drawable.ic_badge_streak_10k, R.string.badge_title_streak_80_days_10k_each, 80)
    ,STREAK_90_DAYS_10K_STEPS_PER_DAY(R.drawable.ic_badge_streak_10k, R.string.badge_title_streak_90_days_10k_each, 90)
    ,TEAM_GOAL_25_PCT(R.drawable.ic_badge_team_goal, R.string.badge_title_team_completed_25_pct, 25)
    ,TEAM_GOAL_50_PCT(R.drawable.ic_badge_team_goal, R.string.badge_title_team_completed_50_pct, 50)
    ,TEAM_GOAL_75_PCT(R.drawable.ic_badge_team_goal, R.string.badge_title_team_completed_75_pct, 75)
    ,LEVEL_CHAMPION(R.drawable.ic_badge_level_champion, R.string.badge_title_level_champion, 100)
    ,LEVEL_PLATINUM(R.drawable.ic_badge_level_platinum, R.string.badge_title_level_platinum, 75)
    ,LEVEL_GOLD(R.drawable.ic_badge_level_gold, R.string.badge_title_level_gold, 50)
    ,LEVEL_SILVER(R.drawable.ic_badge_level_silver, R.string.badge_title_level_silver, 25)
}


@Parcelize
data class Badge(
        @SerializedName("type")  var type: BadgeType = BadgeType.UNKNOWN,
        @SerializedName("date") var date: Long? = null,
        @SerializedName("title") var title: String? = null
) : Parcelable

