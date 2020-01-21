package com.android.wcf.home.notifications

import com.android.wcf.base.BaseMvp
import com.android.wcf.model.Notification

interface NotificationsMvp {
    interface NotificationView : BaseMvp.BaseView {
        fun showNotifications(notifications: List<Notification>)

        fun onGetParticipantNotificationsError(error: Throwable)

        fun onUpdateParticipantNotificationReadSuccess(notificationId: Int)

        fun onUpdateParticipantNotificationReadError(error: Throwable)

        fun showNotificationsIsEmpty()

        fun hideEmptyNotificationsView()

    }

    interface Presenter : BaseMvp.Presenter {
        fun getParticipantNotifications(fbid: String, eventId: Int)

        fun refreshParticipantNotifications(fbid: String, eventId: Int)

        fun updateParticipantNotificationRead(id: Int, readFlag: Boolean)
    }

    interface Host : BaseMvp.Host
}