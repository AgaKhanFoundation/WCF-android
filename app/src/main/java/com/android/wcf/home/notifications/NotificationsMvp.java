package com.android.wcf.home.notifications;

import com.android.wcf.base.BaseMvp;

public interface NotificationsMvp {
     interface NotificationView extends BaseMvp.BaseView {

    }

    interface Presenter extends BaseMvp.Presenter  {
    }

    interface Host {
        void setViewTitle(String title);
    }
}