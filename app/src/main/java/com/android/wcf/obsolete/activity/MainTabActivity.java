package com.android.wcf.obsolete.activity;
/**
 * Copyright © 2017 Aga Khan Foundation
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
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
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.wcf.R;
import com.android.wcf.obsolete.fragments.child.SettingsFragment;
import com.android.wcf.obsolete.fragments.parent.ParentFragment;
import com.android.wcf.utils.AppUtil;
import com.android.wcf.utils.Build;
import com.android.wcf.utils.Debug;

public class MainTabActivity extends AppCompatActivity {

    private static String TAG = MainTabActivity.class.getSimpleName();
    private static boolean DEBUG = Build.DEBUG;
    private static Context mContext;

    RelativeLayout relativeLeaderBoard;
    RelativeLayout relativeMyTeam;
    RelativeLayout relativeMyProfile;
    ImageView imageLeaderBoard;
    ImageView imageMyTeam;
    ImageView imageMyProfile;
    public Toolbar mToolbar;
    public TextView mTextBack;
    public ImageView mImageSettings;
    private Fragment mFragment = null;
    private FragmentManager mFragmentManager;
    private String mFragmentTitle = "";
    public TextView textLeaderBoard;
    public TextView textMyTeam;
    public TextView textMyProfile;
    public TextView textAppName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_maintab);
        setupView();
        mContext = this;
        mTextBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpBack();
            }
        });
        displayView(0);
    }

    View.OnClickListener onClickTabsListener =  new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.relativeLeaderBoard:
                    displayView(0);
                    break;
                case R.id.relativeMyTeam:
                    displayView(1);
                    break;
                case R.id.relativeMyProfile:
                    displayView(2);
                    break;
                case R.id.imageSettings:
                    displaySettingsFrgment();
                    break;
            }
        }
    };

    private void setupView() {
        relativeLeaderBoard = findViewById(R.id.relativeLeaderBoard);
        relativeMyTeam = findViewById(R.id.relativeMyTeam);
        relativeMyProfile = findViewById(R.id.relativeMyProfile);
        imageLeaderBoard = findViewById(R.id.imageLeaderBoard);
        imageMyTeam = findViewById(R.id.imageMyTeam);
        imageMyProfile = findViewById(R.id.imageMyProfile);
        mToolbar = findViewById(R.id.toolbar);
        mTextBack = findViewById(R.id.textBack);
        mImageSettings = findViewById(R.id.imageSettings);
        textLeaderBoard = findViewById(R.id.textLeaderBoard);
        textMyTeam = findViewById(R.id.textMyTeam);
        textMyProfile = findViewById(R.id.textMyProfile);
        textAppName = findViewById(R.id.textAppName);

        relativeLeaderBoard.setOnClickListener(onClickTabsListener);
        relativeMyTeam.setOnClickListener(onClickTabsListener);
        relativeMyProfile.setOnClickListener(onClickTabsListener);
        mImageSettings.setOnClickListener(onClickTabsListener);
    }

    private void displayView(int position) {

        switch (position) {
            case 0:
                mFragment = new ParentFragment();
                mFragmentTitle = AppUtil.LEADERBOARD_FRAGMENT;
                textLeaderBoard.setTextColor(getResources().getColor(R.color.color_green));
                textMyTeam.setTextColor(getResources().getColor(R.color.color_grey));
                textMyProfile.setTextColor(getResources().getColor(R.color.color_grey));
//                imageLeaderBoard.setBackgroundResource(R.drawable.hub_active_icon);
//                imageMyTeam.setBackgroundResource(R.drawable.news_icon);
//                imageMyProfile.setBackgroundResource(R.drawable.directory_icon);
                break;

            case 1:
                mFragment = new ParentFragment();
                mFragmentTitle = AppUtil.MYTEAM_FRAGMENT;
                textLeaderBoard.setTextColor(getResources().getColor(R.color.color_grey));
                textMyTeam.setTextColor(getResources().getColor(R.color.color_green));
                textMyProfile.setTextColor(getResources().getColor(R.color.color_grey));
//                imageLeaderBoard.setBackgroundResource(R.drawable.hub_icon);
//                imageMyTeam.setBackgroundResource(R.drawable.news_active_icon);
//                imageMyProfile.setBackgroundResource(R.drawable.directory_icon);
                break;

            case 2:
                mFragment = new ParentFragment();
                mFragmentTitle = AppUtil.MYPROFILE_FRAGMENT;
                textLeaderBoard.setTextColor(getResources().getColor(R.color.color_grey));
                textMyTeam.setTextColor(getResources().getColor(R.color.color_grey));
                textMyProfile.setTextColor(getResources().getColor(R.color.color_green));
//                imageLeaderBoard.setBackgroundResource(R.drawable.hub_icon);
//                imageMyTeam.setBackgroundResource(R.drawable.news_icon);
//                imageMyProfile.setBackgroundResource(R.drawable.directory_active);
                break;
        }

        if (mFragment != null) {
            Bundle bundle = new Bundle();
            bundle.putString("FragmentTitle", mFragmentTitle);
            mFragment.setArguments(bundle);
            mFragmentManager = getSupportFragmentManager();
            mFragmentManager.beginTransaction()
                    .replace(R.id.frame_container, mFragment, mFragmentTitle)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        handleBackbtn();
    }

    public void handleBackbtn() {
        try {
            Fragment myFragment = (Fragment) getSupportFragmentManager().findFragmentByTag(mFragmentTitle);
            if (myFragment != null && myFragment.isVisible()) {
                // add your code here
                //we have used is_survey_visible object inorder to cehck if my survey detail is visible or not
                if (myFragment.getChildFragmentManager().getBackStackEntryCount() == 1) {
                    showQuitDialog();
                } else {
                    myFragment.getChildFragmentManager().popBackStack();
                }
            } else {
                showQuitDialog();
            }

        } catch (Exception e) {
            Debug.error(DEBUG, "MainTabActivity", "Exception::" + e.getMessage());
        }
    }

    private void showQuitDialog() {
        final Dialog dialog = new Dialog(MainTabActivity.this, R.style.CustomTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // setting custom layout to dialog
        dialog.setContentView(R.layout.exit_dialog);

        RelativeLayout relativeYes = (RelativeLayout) dialog.findViewById(R.id.relativeYes);
        RelativeLayout relativeNo = (RelativeLayout) dialog.findViewById(R.id.relativeNo);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        // adding button click event
        relativeYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                finish();
            }
        });
        relativeNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }


    public static void finishMe() {
        if (mContext != null)
            ((Activity) mContext).finish();
    }

    public void displaySettingsFrgment() {
        Fragment myFragment = (Fragment) getSupportFragmentManager().findFragmentByTag(mFragmentTitle);
        if (myFragment != null) {
            Fragment moreFragment = new SettingsFragment();
            mFragmentManager = myFragment.getChildFragmentManager();
            mFragmentManager.beginTransaction()
                    .replace(R.id.frame_container_child, moreFragment, AppUtil.SETTINGS_FRAGMENT)
                    .setTransition(FragmentTransaction.TRANSIT_NONE)
                    .addToBackStack(mFragmentTitle)
                    .commit();
        }
    }

    public void popUpBack() {
        Fragment myFragment = (Fragment) getSupportFragmentManager().findFragmentByTag(mFragmentTitle);
        if (myFragment != null && myFragment.isVisible()) {
            if (myFragment.getChildFragmentManager().getBackStackEntryCount() != 1) {
                myFragment.getChildFragmentManager().popBackStack();
            }
        }
    }
}
