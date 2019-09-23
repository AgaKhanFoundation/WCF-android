package com.android.wcf.model

import com.google.gson.annotations.SerializedName
import java.util.*


data class Team(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("name") var name: String = "",
        @SerializedName("image") var image: String = "",
        @SerializedName("creator_id") var leaderId: String = "",
        @SerializedName("visibilty") var visibility: Boolean = true,
        @SerializedName("participants") var participants: List<Participant> = arrayListOf(),
        @SerializedName("achievements") var achievements: List<Achievement> = arrayListOf()) {

    fun getLeaderName():String? {
        this.participants?.let {
            if (it.size > 0) {
                return it.get(0).name
            }
        }
        return ""
    }

    //TODO: refactor to use the attribute leaderId (creator_id) instead of participants[0]
    fun getLeaderParticipantId():String? {
        this.participants?.let {
            if (it.size > 0) {
                return it.get(0).participantId
            }
        }
        return ""
    }
    fun isTeamLeader(participantId:String):Boolean{
        val leaderId = getLeaderParticipantId();
        return (!leaderId.isNullOrEmpty() && participantId.equals(leaderId))
    }

    companion object {
        const val TEAM_ATTRIBUTE_NAME = "name"
        const val TEAM_LEADER_ID_ATTRIBUTE_NAME = "creator_id"
        const val TEAM_VISIBILITY_ATTRIBUTE_NAME = "visibility"
    }
}
