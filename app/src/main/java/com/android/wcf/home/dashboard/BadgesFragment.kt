package com.android.wcf.home.dashboard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.android.wcf.R
import com.android.wcf.base.BaseFragment

class BadgesFragment : BaseFragment(), BadgesMvp.View {

    val TAG = BadgesFragment::class.java.simpleName

    lateinit var host: BadgesMvp.Host
    lateinit var presenter: BadgesMvp.Presenter
    lateinit var mainContainer: View
    lateinit var emptyContainer: View

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is DashboardMvp.Host) {
            host = context as BadgesMvp.Host
        } else {
            throw RuntimeException("$context must implement BadgesMvp.Host")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = BadgesPresenter(this)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_badges, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupview(view)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                closeView()
                return true
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onStart() {
        super.onStart()
        host.setToolbarTitle(getString(R.string.badges_card_label), true)

        presenter.getBadgesData(event, participantTeam, participant)
    }

     fun closeView() {
        activity?.onBackPressed()
    }
    fun setupview(view: View) {
        mainContainer = view.findViewById(R.id.badges_main_container)
        emptyContainer = view.findViewById(R.id.badges_empty_view_container)
    }

    override fun onNoBadgesData() {
        emptyContainer.visibility = View.VISIBLE
        mainContainer.visibility = View.GONE
    }
}