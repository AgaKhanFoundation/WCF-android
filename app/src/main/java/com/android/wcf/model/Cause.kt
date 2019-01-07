package com.android.wcf.model

import com.google.gson.annotations.SerializedName

data class Cause(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("name") var name: String? = null) {
}
