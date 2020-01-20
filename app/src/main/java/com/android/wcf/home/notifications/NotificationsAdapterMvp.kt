package com.android.wcf.home.notifications

import com.android.wcf.base.BaseMvp
import com.android.wcf.model.Notification

interface NotificationsAdapterMvp {
    interface View {
        fun updateNotificationsData(notifications: List<Notification>)

        fun notificationsUpdated()
    }

    interface Presenter : BaseMvp.Presenter {

        fun getNotificationsCount(): Int

        fun updateNotificationsData(notifications: List<Notification>)

        fun getNotification(pos: Int): Notification?

        fun getViewType(pos: Int): Int

        fun messageClicked(pos: Int)
    }

    interface Host {
        fun showNotificationsIsEmpty()

        fun hideEmptyNotificationsView()

        fun messageSelected(notification: Notification)
    }
}