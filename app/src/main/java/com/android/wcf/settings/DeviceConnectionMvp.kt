package com.android.wcf.settings

interface DeviceConnectionMvp {
    interface View {

    }

    interface Presenter {

    }

    interface Host {
        abstract fun setToolbarTitle(title: String)
    }
}