package com.android.wcf.home.notifications

import com.android.wcf.model.Notification
import java.util.*

class NotificationsAdapterPresenter(val mView: NotificationsAdapterMvp.View, val mHost: NotificationsAdapterMvp.Host)
    : NotificationsAdapterMvp.Presenter {
    private var notificationList: MutableList<Notification>? = ArrayList()

    override fun getNotificationsCount(): Int {
        return notificationList?.size ?: 0
    }

    override fun updateNotificationsData(notifications: List<Notification>) {
        notificationList?.let {
            it.clear()
            it.addAll(notifications)
        }
        mView.notificationsUpdated()
    }

    override fun getNotification(pos: Int): Notification? {
        notificationList?.let {
            if (pos < it.size) return it.get(pos)
        }
        return null
    }

    override fun messageClicked(pos: Int) {
       val notification =  notificationList?.get(pos)
        notification?.let {
            mHost.messageSelected(it)
        }
    }

    override fun notificationUpdated(participantNotificationId: Int) {
        notificationList?.let {
            val pos = it.indexOfFirst({ it.id == participantNotificationId }) ?: -1
            if (pos >= 0) {
                it.get(pos).readFlag = true
                mView.notificationItemChanged(pos)
            }
        }
    }

    override fun getViewType(pos: Int): Int {
        return 0
    }

    override fun getTag(): String {
        return NotificationsAdapterPresenter::class.java.simpleName
    }

    override fun onStop() {
    }

}