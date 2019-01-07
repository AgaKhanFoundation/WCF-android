package com.android.wcf.model

import com.google.gson.annotations.SerializedName
import java.util.ArrayList

const val TEAM_ATTRIBUTE_NAME = "name"

data class Team(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("name") var name: String = "",
        @SerializedName("image") var image: String = "",
        @SerializedName("leaderName") var leaderName: String = "",
        @SerializedName("participants") var participants: List<Participant> = ArrayList(),
        @SerializedName("achievements") var achievements: List<Achievement> = ArrayList()) {

}
