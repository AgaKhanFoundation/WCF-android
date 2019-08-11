package com.android.wcf.model

import com.google.gson.annotations.SerializedName
import java.util.*


data class Team(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("name") var name: String = "",
        @SerializedName("image") var image: String = "",
        @SerializedName("leaderId") var leaderId: String = "",
        @SerializedName("visibilty") var visibility: Boolean = true,
        @SerializedName("participants") var participants: List<Participant> = arrayListOf(),
        @SerializedName("achievements") var achievements: List<Achievement> = arrayListOf()) {

    var leaderName:String? = ""

    companion object {
        const val TEAM_ATTRIBUTE_NAME = "name"
        const val TEAM_LEADER_ID_ATTRIBUTE_NAME = "leaderId"
        const val TEAM_VISIBILITY_ATTRIBUTE_NAME = "visibility"
    }
}
