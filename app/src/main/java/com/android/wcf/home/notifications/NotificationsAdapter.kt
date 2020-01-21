package com.android.wcf.home.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.wcf.R
import com.android.wcf.model.Notification
import java.text.SimpleDateFormat
import java.util.*

class NotificationsAdapter(var mHost: NotificationsAdapterMvp.Host) :
        RecyclerView.Adapter<NotificationsAdapter.NotificationHolder>(), NotificationsAdapterMvp.View {
    var mAdapterPresenter: NotificationsAdapterMvp.Presenter

    init {
        mAdapterPresenter = NotificationsAdapterPresenter(this, mHost)
    }

    override fun updateNotificationsData(notifications: List<Notification>) {
        if (notifications.size == 0) {
            mHost.showNotificationsIsEmpty()
            return
        }
        mHost.hideEmptyNotificationsView()
        mAdapterPresenter.updateNotificationsData(notifications)
    }

    override fun notificationUpdated(participantNotificationId: Int) {
        mAdapterPresenter.notificationUpdated(participantNotificationId)
    }

    override fun notificationsUpdated() {
        notifyDataSetChanged()
    }

    override fun notificationItemChanged(pos: Int) {
        notifyItemChanged(pos)
    }

    override fun getItemCount(): Int {
        return mAdapterPresenter?.getNotificationsCount() ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, pos: Int): NotificationHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.view_notification_item, parent, false)

        return NotificationHolder(view, mAdapterPresenter)
    }

    override fun onBindViewHolder(notificationHolder: NotificationHolder, pos: Int) {
        val context = notificationHolder.itemView.context
        val res = context.resources
        val notification = mAdapterPresenter?.getNotification(pos)
        notificationHolder.message.text = notification?.message ?: ""
        notificationHolder.messageDate.text = formatMessageDate(notification?.messageDate)
        val readFlag = notification?.readFlag ?: false
        val bgColor =
                if (readFlag) res.getColor(R.color.color_notification_read, null)
                else res.getColor(R.color.color_notification_unread, null)
        notificationHolder.itemView.setBackgroundColor(bgColor)
        if (!readFlag)
            notificationHolder.itemView.setOnClickListener(notificationHolder)
        else
            notificationHolder.itemView.setOnClickListener(null)
    }

    private fun formatMessageDate(messageDate: Date?): String {

        val now = Date()
        val minute = 60 * 1000L
        val hour = 60 * 60 * 1000L
        val day = 24 * 60 * 60 * 1000L
        val week = 7 * 24 * 60 * 60 * 1000L
        val month = 4 * 7 * 24 * 60 * 60 * 1000L
        val dateFormatter = SimpleDateFormat("yyyy MMM d, HH:mm:ss")

        messageDate?.let {
            val delta = now.time - it.time
            when {
                delta < minute -> return "now"
                delta < hour -> return String.format("%dm", delta / minute)
                delta < day -> return String.format("%dh", delta / hour)
                delta < week -> return String.format("%dd", delta / day)
                delta < month -> return String.format("%dw", delta / week)
                else -> return dateFormatter.format(it.time)
            }
        }
        return ""
    }

    class NotificationHolder(itemView: View, val presenter: NotificationsAdapterMvp.Presenter) :
            RecyclerView.ViewHolder(itemView), View.OnClickListener {
        lateinit var message: TextView
        lateinit var messageDate: TextView

        init {
            setupView(itemView)
        }

        override fun onClick(view: View) {
            val pos = adapterPosition
            if (pos >= 0 && pos < presenter.getNotificationsCount()) {
                presenter.messageClicked(pos)
            }
        }

        private fun setupView(view: View) {
            message = view.findViewById(R.id.message)
            messageDate = view.findViewById(R.id.message_date)
        }
    }
}
