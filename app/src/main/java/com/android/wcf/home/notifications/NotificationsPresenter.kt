package com.android.wcf.home.notifications

import android.util.Log
import com.android.wcf.home.BasePresenter
import com.android.wcf.model.Notification

class NotificationsPresenter(private val notificationView: NotificationsMvp.NotificationView) :
        BasePresenter(), NotificationsMvp.Presenter {

    override fun getParticipantNotifications(fbid: String, eventId: Int) {
        super.getParticipantNotifications(fbid, eventId)
    }

    override fun refreshParticipantNotifications(fbid: String, eventId: Int) {

    }

    override fun onParticipantNotificationsRetrieved(notifications: List<Notification>) {
        notificationView.showNotifications(notifications)
    }

    override fun onGetParticipantNotificationsError(error: Throwable) {
        super.onGetParticipantNotificationsError(error)
        notificationView.onGetParticipantNotificationsError(error)
    }

    override fun updateParticipantNotificationRead(participantNotificationId: Int, readFlag: Boolean) {
        super.updateParticipantNotificationRead(participantNotificationId, readFlag)

    }

    override fun onUpdateParticipantNotificationReadSuccess(participantNotificationId: Int, readFlag: Boolean) {
        notificationView.onUpdateParticipantNotificationReadSuccess(participantNotificationId)
    }

    override fun onUpdateParticipantNotificationReadError(error: Throwable) {
        notificationView.onUpdateParticipantNotificationReadError(error)
    }

    override fun getTag(): String {
        return TAG
    }

    override fun onStop() {

    }

    companion object {
        private val TAG = NotificationsPresenter::class.java.simpleName
    }
}
