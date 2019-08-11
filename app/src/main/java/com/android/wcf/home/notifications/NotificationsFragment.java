package com.android.wcf.home.notifications;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.wcf.R;
import com.android.wcf.base.BaseFragment;


public class NotificationsFragment extends BaseFragment implements NotificationsMvp.NotificationView {
    private static final String TAG = NotificationsFragment.class.getSimpleName();

    private NotificationsMvp.Host mFragmentHost;
    private NotificationsMvp.Presenter notificationPresenter;

    public NotificationsFragment() {
    }

    public static NotificationsFragment newInstance() {
        NotificationsFragment fragment = new NotificationsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationPresenter = new NotificationsPresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
        mFragmentHost.setToolbarTitle(getString(R.string.nav_notifications), false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NotificationsMvp.Host) {
            mFragmentHost = (NotificationsMvp.Host) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement NotificationsMvp.Host");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFragmentHost = null;
    }
}
