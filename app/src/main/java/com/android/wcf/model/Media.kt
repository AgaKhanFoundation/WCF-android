package com.android.wcf.model

import com.google.gson.annotations.SerializedName


enum class MediaType {
    PHOTO, VIDEO
}

data class Media(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("sequence") var sequence: Int = 0,
        @SerializedName("type") var type: MediaType,
        @SerializedName("url") var url: String? = null){
}
