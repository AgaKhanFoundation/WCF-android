package com.android.wcf.home.challenge

import com.android.wcf.base.BaseMvp

interface SupportsInviteMvp {
    interface View : BaseMvp.BaseView {

        fun inviteSupportersToPledge()
    }

    interface Presenter : BaseMvp.Presenter {
        fun onSupportersInviteClicked()
    }

    interface Host : BaseMvp.Host {

    }

}
