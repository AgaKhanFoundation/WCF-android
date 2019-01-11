package com.android.wcf.model

import com.google.gson.annotations.SerializedName
import java.util.ArrayList

/*
    Participant:
    1) has FaceBookId to signup in the app,
    2) has a steps tracking deviceType/source,
    3) joins a team,
    4) supports a cause and participants to walk in an event (campaign)
    5 has 0 or more individial achievements
 */

const val PARTICIPANT_ATTRIBUTE_FBID = "fbid"
const val PARTICIPANT_ATTRIBUTE_TEAM_ID = "team_id"
const val PARTICIPANT_ATTRIBUTE_CAUSE_ID = "cause_id"
const val PARTICIPANT_ATTRIBUTE_EVENT_ID = "event_id"
const val PARTICIPANT_ATTRIBUTE_SOURCE_ID = "source_id"

data class Participant(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("fbid") var fbId: String? = "",
        @SerializedName("team_id") var teamId: Int? = 0,
        @SerializedName("event_id") var eventId: Int? = 0,
        @SerializedName("cause_id") var causeId: Int? = 0,
        @SerializedName("source_id") var sourceId: Int? = 0,
        @SerializedName("team") var team: Team? = Team(),
        @SerializedName("cause") var cause: Cause? = Cause(),
        @SerializedName("event") var event: Event? = Event(),
        @SerializedName("achievements") var achievements: List<Achievement> = ArrayList()) {
}
