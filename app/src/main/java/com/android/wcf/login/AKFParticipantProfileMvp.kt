package com.android.wcf.login

import com.android.wcf.web.WebViewMvp

interface AKFParticipantProfileMvp {
    interface View : WebViewMvp.View {
        fun akfProfileRegistered()
        fun akfProfileRegistrationError(error:Throwable, participantId: String)
    }

    interface Presenter : WebViewMvp.Presenter {
        fun updateParticipantProfileRegistered(participantId:String)
    }

    interface Host : WebViewMvp.Host {
        fun akfProfileCreationComplete()
        fun akfProfileCreationSkipped()
        fun showToolbar()
        fun hideToolbar()
        fun restartApp()
    }
}