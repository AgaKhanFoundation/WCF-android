package com.android.wcf.model

import com.google.gson.annotations.SerializedName

data class Event(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("name") var name: String? = null,
        @SerializedName("description") var description: String? = null,
        @SerializedName("start_date") var startDate: Long? = 0,
        @SerializedName("end_date") var endDate: Long? = 0,
        @SerializedName("team_limit") var teamLimit: Int = 0,
        @SerializedName("team_building_start") var teamBuildingStart: Int = 0,
        @SerializedName("team_building_end") var teamBuildingEnd: Int = 0,
        @SerializedName("locality_id") var localityId: Int = 0,
        @SerializedName("cause_id") var causeId: Int = 0) {
}


// add expanded cause, locality objects ?