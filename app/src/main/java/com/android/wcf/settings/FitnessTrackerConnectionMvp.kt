package com.android.wcf.settings

import com.android.wcf.base.BaseMvp

interface FitnessTrackerConnectionMvp {
    interface View: BaseMvp.BaseView {

    }

    interface Presenter : BaseMvp.Presenter {

    }

    interface Host : BaseMvp.Host {
        fun connectAppToFitbit()
        fun connectAppToGoogleFit()
        fun disconnectAppFromFitbit()
        fun disconnectAppFromGoogleFit()
    }
}