package com.android.wcf.fragments.child;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.wcf.R;

import butterknife.ButterKnife;

/**
 * Created by Malik Khoja on 4/27/2017.
 */

public class MyTeamFragment extends Fragment {

    private View mView;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_myteam, container, false);
        mContext = getActivity();
        ButterKnife.bind(this, mView);
        return mView;
    }

}
