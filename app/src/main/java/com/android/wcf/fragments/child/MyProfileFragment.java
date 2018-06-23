package com.android.wcf.fragments.child;
/**
 * Copyright © 2017 Aga Khan Foundation
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p>
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * <p>
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p>
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.wcf.R;
import com.android.wcf.activity.MainTabActivity;
import com.android.wcf.utils.AppUtil;
import com.android.wcf.utils.CircleImageView;
import com.android.wcf.utils.Preferences;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyProfileFragment extends Fragment implements View.OnClickListener {

    private View mView;
    private Context mContext;
    @BindView(R.id.imageMyProfile)
    CircleImageView imageMyProfile;
    @BindView(R.id.textMyName)
    TextView textMyName;
    @BindView(R.id.textTeamName)
    TextView textTeamName;
    @BindView(R.id.spinnerSorting)
    Spinner spinnerSorting;
    @BindView(R.id.textTeamMoneyCurrency)
    TextView textTeamMoneyCurrency;
    @BindView(R.id.textTeamMoney)
    TextView textTeamMoney;
    @BindView(R.id.progressBarMyTeamMiles)
    ProgressBar progressBarMyTeamMiles;
    @BindView(R.id.textCurrentMiles)
    TextView textCurrentMiles;
    @BindView(R.id.textOutofMiles)
    TextView textOutofMiles;
    @BindView(R.id.imageDaysCount)
    ImageView imageDaysCount;
    @BindView(R.id.textDays)
    TextView textDays;
    @BindView(R.id.textMilestone)
    TextView textMilestone;
    @BindView(R.id.textCurrentSupportersCount)
    TextView textCurrentSupportersCount;
    @BindView(R.id.textNameFirstUser)
    TextView textNameFirstUser;
    @BindView(R.id.textCurrentMoneyFirstUser)
    TextView textCurrentMoneyFirstUser;
    @BindView(R.id.textPledgedFirstUser)
    TextView textPledgedFirstUser;
    @BindView(R.id.textNameSecondUser)
    TextView textNameSecondUser;
    @BindView(R.id.textCurrentMoneySecondUser)
    TextView textCurrentMoneySecondUser;
    @BindView(R.id.textPledgedSecondUser)
    TextView textPledgedSecondUser;
    @BindView(R.id.textCurrentSupporterSeeMore)
    TextView textCurrentSupporterSeeMore;
    @BindView(R.id.textPastEventsCount)
    TextView textPastEventsCount;
    @BindView(R.id.textPastEventsSeeMore)
    TextView textPastEventsSeeMore;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_myprofile, container, false);
        mContext = getActivity();
        ButterKnife.bind(this, mView);
        ((MainTabActivity) getActivity()).mTextBack.setVisibility(View.GONE);
        ((MainTabActivity) getActivity()).mImageSettings.setVisibility(View.VISIBLE);
        ((MainTabActivity) getActivity()).textAppName.setText(getResources().getString(R.string.myprofileheadertext));
        textCurrentSupporterSeeMore.setOnClickListener(this);
        textPastEventsSeeMore.setOnClickListener(this);
        textMyName.setText(Preferences.getPreferencesString("userName", mContext));
        String profilePicUrl = Preferences.getPreferencesString("userProfileUrl", mContext);
        Glide.with(this)
                .load(profilePicUrl)
//                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imageMyProfile);
        return mView;
    }

    @Override
    public void onClick(View view) {
        FragmentTransaction ft = getParentFragment()
                 .getChildFragmentManager().beginTransaction();
        switch (view.getId()) {
            case R.id.textCurrentSupporterSeeMore:
                SupportersSponsersFragment supportersSponsersFragment = new SupportersSponsersFragment();
                ft.replace(R.id.frame_container_child, supportersSponsersFragment, AppUtil.SPONSERERS_FRAGMENT);
                ft.addToBackStack(AppUtil.MYPROFILE_FRAGMENT);
                ft.commit();
                break;
            case R.id.textPastEventsSeeMore:
                PastEventFragment pastEventFragment = new PastEventFragment();
                ft.replace(R.id.frame_container_child, pastEventFragment, AppUtil.PASTEVENT_FRAGMENT);
                ft.addToBackStack(AppUtil.MYPROFILE_FRAGMENT);
                ft.commit();
                break;


        }
    }
}
