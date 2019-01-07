package com.android.wcf.model

import com.google.gson.annotations.SerializedName

data class Achievement(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("name") var name: String? = null,
        @SerializedName("distance") var distance: Int = 0) {

}
