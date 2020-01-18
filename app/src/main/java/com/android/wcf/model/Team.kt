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
        @SerializedName("hidden") var hidden: Boolean = false,
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
            //temporary until server creator_id data is fixed
            if (it.size > 0) {
                return it.get(0).name
            }
        }
        return ""
    }

    fun isTeamLeader(participantId:String):Boolean{
        return (!leaderId.isNullOrEmpty() && participantId.equals(leaderId))
    }

    fun geTotalParticipantCommitmentSteps(): Int {
        var totalSteps = 0
        for (participant in participants) {
            totalSteps += participant.commitment?.commitmentSteps ?: 0
        }
        return totalSteps
    }

    fun getCommitmentSteps(fbid:String):Int {
        for (participant in participants) {
            if (participant.fbId.equals(fbid) ) {
                participant.commitment?.let {
                    return it.commitmentSteps
                }
            }
        }
        return 0
    }

    fun geTotalParticipantCompletedSteps():Int {

        var totalSteps = 0
        for (participant in participants) {
            totalSteps += participant.stats?.distance ?: 0
        }
        return totalSteps
    }

    companion object {
        const val TEAM_ATTRIBUTE_NAME = "name"
        const val TEAM_ATTRIBUTE_IMAGE = "image"
        const val TEAM_ATTRIBUTE_LEADER_ID = "creator_id"
        const val TEAM_ATTRIBUTE_VISIBILITY = "hidden"
    }

}
