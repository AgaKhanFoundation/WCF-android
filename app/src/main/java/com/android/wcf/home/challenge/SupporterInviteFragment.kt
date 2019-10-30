package com.android.wcf.home.challenge

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.android.wcf.R
import com.android.wcf.base.BaseFragment

class SupporterInviteFragment : BaseFragment(), SupportsInviteMvp.View {

    var presenter: SupportsInviteMvp.Presenter? = null
    var host: SupportsInviteMvp.Host? = null

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.btn_invite_supporters -> presenter?.onSupportersInviteClicked()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        host?.setToolbarTitle(getString(R.string.supporters_invite_title), true)
        presenter = SupportsInvitePresenter(this)
        setupView(view)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_supporters_invite, container, false)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var handled = super.onOptionsItemSelected(item)
        if (!handled) {
            when (item.itemId) {
                android.R.id.home -> {
                    closeView()
                    handled = true
                }
            }
        }
        return handled
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SupportsInviteMvp.Host) {
            host = context
        } else {
            throw RuntimeException("$context must implement SupportsInviteMvp.Host")
        }
    }

    override fun onDetach() {
        super.onDetach()
        host = null
    }

    override fun onStop() {
        super.onStop()
        presenter?.onStop()
    }

    fun setupView(fragmentView: View) {
        val supporterInviteButton: Button = fragmentView.findViewById(R.id.btn_invite_supporters)
        supporterInviteButton.setOnClickListener(onClickListener)
    }

    override fun inviteSupportersToPledge() {
        inviteSupporters()
    }

    internal fun closeView() {
        activity?.onBackPressed()
    }

}