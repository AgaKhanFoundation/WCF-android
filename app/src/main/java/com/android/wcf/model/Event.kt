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
                val sdf = SimpleDateFormat("yyyy-MM-dd")
                val today = sdf.format(Date().time)
                val start = sdf.format(it.time)
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

    fun getDefaultParticipantCommitment(): Int {

        val days = getDaysInChallenge()
        if (days > 0) {
            return (days * Constants.PARTICIPANT_COMMITMENT_MILES_PER_DAY)
        }

        return Constants.PARTICIPANT_COMMITMENT_MILES_DEFAULT
    }

    fun getDaysInChallenge():Int {
        startDate?.let { startDate ->
            endDate?.let { endDate ->
                val daysInChallenge = TimeUnit.DAYS.convert(endDate.time - startDate.time, TimeUnit.MILLISECONDS) + 1
                return daysInChallenge.toInt()
            }
        }
        return 0
    }

    fun getTeamDistanceGoal():Int {
        return teamLimit * getDefaultParticipantCommitment()
    }

    fun getTeamStepsGoal():Int {
        return teamLimit * getDefaultParticipantCommitment() * Constants.STEPS_IN_A_MILE
    }

    fun calculateTime(timeIn: Long?): String {
        val day = TimeUnit.MILLISECONDS.toDays(timeIn!!).toInt()
        val hours = TimeUnit.MILLISECONDS.toHours(timeIn) - day * 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeIn) - TimeUnit.MILLISECONDS.toHours(timeIn) * 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeIn) - TimeUnit.MILLISECONDS.toMinutes(timeIn) * 60

        return day.toString() + " Days " + hours.toString() + " Hours " +
                minutes.toString() + " Minutes " + seconds.toString() + " Seconds."
    }
}

