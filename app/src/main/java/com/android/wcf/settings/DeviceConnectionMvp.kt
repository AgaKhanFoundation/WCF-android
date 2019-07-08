package com.android.wcf.settings

interface DeviceConnectionMvp {
    interface View {

    }

    interface Presenter {

    }

    interface Host {
        fun setToolbarTitle(title: String)
        fun connectAppToFitbit()
    }
}