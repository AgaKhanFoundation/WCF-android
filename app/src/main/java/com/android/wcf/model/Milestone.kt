package com.android.wcf.model

import com.google.gson.annotations.SerializedName

data class Milestone(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("sequence") var sequence: Int = 0,
        @SerializedName("distance") var steps: Int = 9999999,
        @SerializedName("name") var name: String = "",
        @SerializedName("flag_name") var flag_name: String = "",
        @SerializedName("title") var title: String? = null,
        @SerializedName("subtitle") var subtitle: String? = null,
        @SerializedName("description") var description: String? = "",
        @SerializedName("media") var media: String? = null) {

    var reached:Boolean = false

    fun hasReached(stepsCompleted:Int) {
        reached = stepsCompleted >= steps
    }
}
