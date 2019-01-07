package com.android.wcf.model

import com.google.gson.annotations.SerializedName
import java.util.ArrayList

data class Milestone(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("miles") var miles: Int = 9999999,
        @SerializedName("city") var city: String? = "",
        @SerializedName("country") var country: String? = "",
        @SerializedName("text") var text: String? = "",
        @SerializedName("media_list") var achievements: List<Achievement> = ArrayList()) {
}
