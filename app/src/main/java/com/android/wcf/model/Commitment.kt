package com.android.wcf.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Commitment(
        @SerializedName(COMMITMENT_ATTRIBUTE_ID) var id: Int,
        @SerializedName(COMMITMENT_ATTRIBUTE_FBID) var fbid: String,
        @SerializedName(COMMITMENT_ATTRIBUTE_EVENT_ID) var eventId: Int,
        @SerializedName(COMMITMENT_ATTRIBUTE_COMMITMENT) var commitmentSteps: Int = 0
) : Parcelable {

    companion object {
        const val COMMITMENT_ATTRIBUTE_ID = "id"
        const val COMMITMENT_ATTRIBUTE_FBID = "fbid"
        const val COMMITMENT_ATTRIBUTE_EVENT_ID = "event_id"
        const val COMMITMENT_ATTRIBUTE_COMMITMENT = "commitment"
    }
}