package com.android.wcf.model

import com.google.gson.annotations.SerializedName

/**
 *
 * Steps tracking source from a supported list of devices
 */

data class Source(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("name") var name: String? = null) {
}
