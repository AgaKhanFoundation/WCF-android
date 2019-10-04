package com.android.wcf.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

data class Team(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("name") var name: String = "",
        @SerializedName("image") var image: String = "",
        @SerializedName("creator_id") var leaderId: String = "",
        @SerializedName("visibility") var visibility: Boolean = true,
        @SerializedName("participants") var participants: List<Participant> = arrayListOf(),
        @SerializedName("achievements") var achievements: List<Achievement> = arrayListOf()) {

        var selectedToJoin = false        //used by JoinTeam to select

    fun getLeaderName():String? {
        this.participants.let {
            for (participant in it) {
                if (participant.participantId.equals(leaderId)) {
                    return participant.name
                }
            }
            if (it.size > 0) {
                return it.get(0).name
            }
        }
        return ""
    }

    //TODO: delete this method after creator_id not matching participant is resolved
    fun getLeaderParticipantId():String? {
        this.participants.let {
            if (it.size > 0) {
                return it.get(0).participantId
            }
        }
        return ""
    }
    fun isTeamLeader(participantId:String):Boolean{

        // return (!leaderId.isNullOrEmpty() && participantId.equals(leaderId))
        //TODO: use the above and remove the if/else below after creator_id not matching participant is resolved

        if (!leaderId.isNullOrEmpty() && participantId.equals(leaderId)){
            return true;
        }
        else {
            val leaderId = getLeaderParticipantId();
            return (!leaderId.isNullOrEmpty() && participantId.equals(leaderId))
        }
    }

    companion object {
        const val TEAM_ATTRIBUTE_NAME = "name"
        const val TEAM_ATTRIBUTE_LEADER_ID = "creator_id"
        const val TEAM_ATTRIBUTE_VISIBILITY = "visibility"
    }
}
