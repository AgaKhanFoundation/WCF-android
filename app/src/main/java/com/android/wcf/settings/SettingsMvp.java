package com.android.wcf.settings;

public interface SettingsMvp {
    interface View {

    }

    interface Presenter {

    }

    interface Host {
        void setToolbarTitle(String title);

        void showDeviceConnection();

        void signout();
    }
}
