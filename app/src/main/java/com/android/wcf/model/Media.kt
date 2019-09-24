package com.android.wcf.model

import com.google.gson.annotations.SerializedName

data class Media(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("type") var type: String? = "",
        @SerializedName("url") var url: String? = ""){
}

