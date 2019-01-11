package com.android.wcf.model

import com.google.gson.annotations.SerializedName

data class Record(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("name") var name: String? = null,
        @SerializedName("date") var date: Long? = 0,
        @SerializedName("distance") var distance: Int? = 0,
        @SerializedName("participant_id") var participantId: Int? = 0,
        @SerializedName("source_id") var sourceId: Int? = 0 ) {
}
