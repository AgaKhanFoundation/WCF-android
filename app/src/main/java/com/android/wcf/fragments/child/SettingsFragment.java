package com.android.wcf.fragments.child;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.wcf.R;

import butterknife.ButterKnife;

/**
 * Created by Malik Khoja on 4/27/2017.
 */

public class SettingsFragment extends Fragment {

    private View mView;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_settings, container, false);
        mContext = getActivity();
        ButterKnife.bind(this, mView);
        return mView;
    }
}
