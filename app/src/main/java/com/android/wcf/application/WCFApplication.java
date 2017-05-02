package com.android.wcf.application;

import android.app.Application;

/**
 * Created by Malik Khoja on 8/23/2016.
 */
public class WCFApplication extends Application {

    private WCFApplication mWcfApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mWcfApplication = this;
    }

    private WCFApplication getWcfApplication() {
        if (mWcfApplication == null) {
            mWcfApplication = this;
        }
        return mWcfApplication;
    }
}
