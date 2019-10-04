package com.android.wcf.login

import com.android.wcf.web.WebViewMvp

interface AKFParticipantProfileMvp {
    interface View : WebViewMvp.View {

    }

    interface Presenter : WebViewMvp.Presenter {
    }

    interface Host : WebViewMvp.Host {
        fun akfProfileCreationComplete()
        fun showToolbar()
        fun hideToolbar()
        fun restartApp()
    }
}