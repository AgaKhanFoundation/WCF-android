package com.android.wcf.model

import com.google.gson.annotations.SerializedName

data class Record(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("date") var date: Long? = 0,
        @SerializedName("distance") var distance: Int? = 0,
        @SerializedName("participant_id") var participantId: Int? = 0,
        @SerializedName("source_id") var sourceId: Int? = 0 ) {

    companion object {
        const val RECORD_ATTRIBUTE_PARTICIPANT_ID = "participant_id"
        const val RECORD_ATTRIBUTE_SOURCE_ID = "source_id"
        const val RECORD_ATTRIBUTE_DATE_ID = "date"
        const val RECORD_ATTRIBUTE_DISTANCE_ID = "distance"
    }
}
