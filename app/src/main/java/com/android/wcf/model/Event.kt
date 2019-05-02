package com.android.wcf.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class Event(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("name") var name: String? = null,
        @SerializedName("description") var description: String? = null,
        @SerializedName("start_date") var startDate: Date? = null,
        @SerializedName("end_date") var endDate: Long? = null,
        @SerializedName("team_limit") var teamLimit: Int = 0,
        @SerializedName("team_building_start") var teamBuildingStart: Date? = null,
        @SerializedName("team_building_end") var teamBuildingEnd: Date? = null,
        @SerializedName("locality_id") var localityId: Int = 0,
        @SerializedName("cause_id") var causeId: Int = 0) {
}
