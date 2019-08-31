package com.android.wcf.model

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

data class Event(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("name") var name: String? = null,
        @SerializedName("description") var description: String? = null,
        @SerializedName("start_date") var startDate: Date? = null,
        @SerializedName("end_date") var endDate: Date? = null,
        @SerializedName("team_limit") var teamLimit: Int = 0,
        @SerializedName("team_building_start") var teamBuildingStart: Date? = null,
        @SerializedName("team_building_end") var teamBuildingEnd: Date? = null,
        @SerializedName("locality_id") var localityId: Int = 0,
        @SerializedName("cause_id") var causeId: Int = 0) {

    fun hasTeamBuildingStarted(): Boolean {
        teamBuildingStart?.let {
            return (TimeUnit.MILLISECONDS.toDays(Date().time - it.time)).toInt() >= 0;
        }
        return false
    }

    fun hasTeamBuildingEnded(): Boolean {
        teamBuildingEnd?.let {
            return (TimeUnit.MILLISECONDS.toDays(Date().time - it.time)).toInt() > 0;
        }
        return false
    }

    fun daysToStartEvent(): Int {
        startDate?.let {
            val difference = it.time - Date().time
            if (difference < 0)
                return -1

            val days = TimeUnit.DAYS.convert(it.time - Date().time, TimeUnit.MILLISECONDS)
            val days1 = TimeUnit.MILLISECONDS.toDays(it.time - Date().time);
            if (days > 0) {
                return days.toInt()
            }
            if (difference > 0) {
                val sdf = SimpleDateFormat("yyyy-mm-dd")
                val today = sdf.format(Date())
                val start = sdf.format(it)
                if (today.equals(start)) {
                    return 0
                }
                return 1
            }
        }
        return -1
    }

    fun daysToEndEvent(): Int {
        endDate?.let {
            val difference = it.time - Date().time
            if (difference < 0)
                return -1

            val days = TimeUnit.DAYS.convert(it.time - Date().time, TimeUnit.MILLISECONDS)
            val days1 = TimeUnit.MILLISECONDS.toDays(it.time - Date().time);
            if (days > 0) {
                return days.toInt()
            }
            if (difference > 0) {
                val sdf = SimpleDateFormat("yyyy-mm-dd")
                val today = sdf.format(Date())
                val end = sdf.format(it)
                if (today.equals(end)) {
                    return 0
                }
                return 1
            }
        }
        return -1
    }

    fun hasChallengeStarted(): Boolean {
        return daysToStartEvent() < 0
    }

    fun hasChallengeEnded(): Boolean {
        return daysToEndEvent() < 0
    }
}

