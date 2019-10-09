package com.android.wcf.model

import com.android.wcf.helper.DistanceConverter
import com.google.gson.annotations.SerializedName
import java.util.ArrayList

/*
    Participant:
    1) has FaceBookId to signup in the app,
    2) has a steps tracking deviceType/source,
    3) joins a team,
    4) supports a cause and participants to walk in an event (challenge)
    5 has 0 or more individual achievements
 */

data class Participant(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("fbid") var fbId: String? = "",
        @SerializedName("team_id") var teamId: Int? = null,
        @SerializedName("cause_id") var causeId: Int? = null,
        @SerializedName("source_id") var sourceId: Int = 0,
        @SerializedName("team") var team: Team? = Team(),
        @SerializedName("cause") var cause: Cause? = Cause(),
        @SerializedName("events") var events: List<Event> = arrayListOf(),
        @SerializedName("achievements") var achievements: List<Achievement> = arrayListOf()) {

    fun getEvent(eventId: Int): Event? {
        for (event in events) {
            if (event.id == eventId) {
                return event;
            }
        }
        return null
    }
    var commitment:Commitment? = null
    var stats:Stats? = null

    fun getCompletedSteps(): Int {
       return stats?.distance ?: 0
    }

    fun getCommittedSteps(): Int {
        commitment?.let {
            return it.commitmentSteps
        }
        return 0
    }

    var participantId: String? = ""
            get() = this.fbId

        var participantProfile:String? = ""
        var name:String?  = ""
        var fundsCommitted:Double = 0.0
        var fundsAccrued:Double = 0.0

    companion object {
        const val PARTICIPANT_ATTRIBUTE_ID = "id"
        const val PARTICIPANT_ATTRIBUTE_FBID = "fbid"
        const val PARTICIPANT_ATTRIBUTE_TEAM_ID = "team_id"
        const val PARTICIPANT_ATTRIBUTE_CAUSE_ID = "cause_id"
        const val PARTICIPANT_ATTRIBUTE_LOCALITY_ID = "locality_id"
        const val PARTICIPANT_ATTRIBUTE_EVENT_ID = "event_id"
        const val PARTICIPANT_ATTRIBUTE_SOURCE_ID = "source_id"
    }
}
