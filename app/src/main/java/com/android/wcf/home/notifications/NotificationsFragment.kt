package com.android.wcf.home.notifications

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.annotation.StringRes
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.wcf.R
import com.android.wcf.base.BaseFragment
import com.android.wcf.model.Event
import com.android.wcf.model.Notification
import com.android.wcf.model.Participant
import java.io.IOException


class NotificationsFragment : BaseFragment(), NotificationsMvp.NotificationView, NotificationsAdapterMvp.Host {

    private var mFragmentHost: NotificationsMvp.Host? = null
    private var notificationPresenter: NotificationsMvp.Presenter? = null
    private var currentParticipant: Participant? = null
    private var currentEvent: Event? = null

    lateinit var emptyNotificationView: View
    lateinit var notificationListContainer: View
    lateinit var notificationRecyclerView: RecyclerView
    private var notificationsAdapter: NotificationsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationPresenter = NotificationsPresenter(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_notification, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView(view)
    }

    override fun onStart() {
        Log.d(TAG, "onStart")
        super.onStart()
        mFragmentHost?.setToolbarTitle(getString(R.string.nav_notifications), false)
        currentParticipant = getParticipant()
        currentEvent = getEvent()
        currentParticipant?.let { thisParticipant ->
            currentEvent.let { thisEvent ->
                notificationPresenter?.getParticipantNotifications(thisParticipant.fbId!!, thisEvent!!.id)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is NotificationsMvp.Host) {
            mFragmentHost = context
        } else {
            throw RuntimeException("$context must implement NotificationsMvp.Host")
        }
    }

    override fun onStop() {
        super.onStop()
        notificationPresenter!!.onStop()
    }

    override fun onDetach() {
        super.onDetach()
        mFragmentHost = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var handled = super.onOptionsItemSelected(item)
        if (!handled) {
            when (item.itemId) {
                R.id.menu_item_refresh -> {
                    handled = true
                    notificationPresenter?.refreshParticipantNotifications(participant!!.fbId!!, event!!.id)
                }
            }
        }
        return handled
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
    }

    override fun onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu()
    }

    override fun onOptionsMenuClosed(menu: Menu) {
        super.onOptionsMenuClosed(menu)
    }

    private fun setupView(view: View) {

        emptyNotificationView = view.findViewById(R.id.empty_view_container)
        emptyNotificationView.visibility = View.VISIBLE

        notificationListContainer = view.findViewById(R.id.notification_list_container)
        notificationListContainer.visibility = View.VISIBLE

        setupNotificationList(view)
    }

    private fun setupNotificationList(view: View) {
        notificationRecyclerView = view.findViewById(R.id.notifications_list)
        notificationRecyclerView.setLayoutManager(LinearLayoutManager(view.getContext()))
        notificationRecyclerView.addItemDecoration(DividerItemDecoration(view.getContext(),
                DividerItemDecoration.VERTICAL))

        notificationsAdapter = NotificationsAdapter(this)
        notificationRecyclerView.setAdapter(notificationsAdapter)
    }

    override fun showNotificationsIsEmpty() {
        emptyNotificationView.visibility = View.VISIBLE
        notificationListContainer.visibility = View.GONE
    }

    override fun hideEmptyNotificationsView() {
        emptyNotificationView.visibility = View.GONE
        notificationListContainer.visibility = View.VISIBLE
    }

    override fun showNotifications(notifications: List<Notification>) {
        notificationsAdapter?.updateNotificationsData(notifications)
    }

    override fun onGetParticipantNotificationsError(error: Throwable) {
        if (error is IOException) {
            showNetworkErrorMessage(R.string.notifications_data_error)
        } else {
            showError(R.string.notifications_data_error, error.message, null)
        }
    }

    override fun showNetworkErrorMessage(@StringRes error_title_res_id: Int) {
        showError(getString(error_title_res_id), getString(R.string.no_network_message)) {
            notificationPresenter?.refreshParticipantNotifications(participant!!.fbId!!, event!!.id)
        }
    }

    override fun messageSelected(notification: Notification) {
        if (!notification.readFlag){
            notificationPresenter?.updateParticipantNotificationRead(notification.id, true)
        }
    }

    override fun onUpdateParticipantNotificationReadSuccess(notificationId: Int) {
        notificationsAdapter?.notificationUpdated(notificationId)
    }

    override fun onUpdateParticipantNotificationReadError(error: Throwable) {

    }

    companion object {
        private val TAG = NotificationsFragment::class.java.simpleName

        fun newInstance(): NotificationsFragment {
            return NotificationsFragment()
        }
    }
}
