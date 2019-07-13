package com.android.wcf.settings

interface FitnessTrackerConnectionMvp {
    interface View {

    }

    interface Presenter {

    }

    interface Host {
        fun setToolbarTitle(title: String)
        fun connectAppToFitbit()
        fun disconnectAppFromFitbit()
    }
}