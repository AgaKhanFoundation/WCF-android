package com.android.wcf.model

import com.google.gson.annotations.SerializedName
import java.util.*


data class Notification(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("notification_id") var notification_id: Int = 0,
        @SerializedName("message_date") var messageDate: Date,
        @SerializedName("expiry_date") var expiryDate: Date? = null,
        @SerializedName("priority") var priority: Int = 0,
        @SerializedName("event_id") var eventId: Int = 0,
        @SerializedName("message") var message: String = "",
        @SerializedName("read_flag") var readFlag: Boolean = false  ){

    companion object {
        const val NOTIFICATION_ATTRIBUTE_ID = "id"
        const val NOTIFICATION_ATTRIBUTE_NOTIFICATION_ID = "notification_id"
        const val NOTIFICATION_ATTRIBUTE_MESSAGE_DATE = "message_date"
        const val NOTIFICATION_ATTRIBUTE_EXPIRY_DATE = "expiry_date"
        const val NOTIFICATION_ATTRIBUTE_PRIORITY = "priority"
        const val NOTIFICATION_ATTRIBUTE_EVENT_ID = "event_id"
        const val NOTIFICATION_ATTRIBUTE_MESSAGE = "message"
        const val NOTIFICATION_ATTRIBUTE_READ_FLAG = "read_flag"
    }
}
