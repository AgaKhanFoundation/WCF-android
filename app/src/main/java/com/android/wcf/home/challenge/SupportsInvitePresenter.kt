package com.android.wcf.home.challenge

class SupportsInvitePresenter(val view:SupportsInviteMvp.View): SupportsInviteMvp.Presenter {

    companion object {
        val TAG = SupportsInvitePresenter.javaClass.simpleName
    }

    override fun onSupportersInviteClicked() {
        view.inviteSupportersToPledge()
    }

    override fun getTag(): String {
        return TAG
    }
}