package com.android.wcf.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.android.wcf.fragments.child.SettingsFragment;
import com.android.wcf.fragments.parent.ParentFragment;
import com.android.wcf.utils.AppUtil;
import com.android.wcf.utils.Build;
import com.android.wcf.utils.Debug;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Malik Khoja on 4/27/2017.
 */

public class MainTabActivity extends AppCompatActivity {

    private static String TAG = MainTabActivity.class.getSimpleName();
    private static boolean DEBUG = Build.DEBUG;
    @BindView(R.id.relativeLeaderBoard)
    RelativeLayout relativeLeaderBoard;
    @BindView(R.id.relativeMyTeam)
    RelativeLayout relativeMyTeam;
    @BindView(R.id.relativeMyProfile)
    RelativeLayout relativeMyProfile;
    @BindView(R.id.imageLeaderBoard)
    ImageView imageLeaderBoard;
    @BindView(R.id.imageMyTeam)
    ImageView imageMyTeam;
    @BindView(R.id.imageMyProfile)
    ImageView imageMyProfile;
    @BindView(R.id.toolbar)
    public Toolbar mToolbar;
    @BindView(R.id.textBack)
    public TextView mTextBack;
    @BindView(R.id.imageSettings)
    public ImageView mImageSettings;
    private Fragment mFragment = null;
    private FragmentManager mFragmentManager;
    private String mFragmentTitle = "";
    private static Context mContext;
    @BindView(R.id.textLeaderBoard)
    public TextView textLeaderBoard;
    @BindView(R.id.textMyTeam)
    public TextView textMyTeam;
    @BindView(R.id.textMyProfile)
    public TextView textMyProfile;
    @BindView(R.id.textAppName)
    public TextView textAppName;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_maintab);
        ButterKnife.bind(this);
        mContext = this;
        mTextBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpBack();
            }
        });
        displayView(0);
    }

    @Nullable
    @OnClick({R.id.relativeLeaderBoard, R.id.relativeMyTeam, R.id.relativeMyProfile, R.id.imageSettings})
    void onClickTabs(View v) {
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

    private void displayView(int position) {

        switch (position) {
            case 0:
                mFragment = new ParentFragment();
                mFragmentTitle = AppUtil.LEADERBOARD_FRAGMENT;
                textLeaderBoard.setTextColor(getResources().getColor(R.color.green));
                textMyTeam.setTextColor(getResources().getColor(R.color.grey));
                textMyProfile.setTextColor(getResources().getColor(R.color.grey));
//                imageLeaderBoard.setBackgroundResource(R.drawable.hub_active_icon);
//                imageMyTeam.setBackgroundResource(R.drawable.news_icon);
//                imageMyProfile.setBackgroundResource(R.drawable.directory_icon);
                break;

            case 1:
                mFragment = new ParentFragment();
                mFragmentTitle = AppUtil.MYTEAM_FRAGMENT;
                textLeaderBoard.setTextColor(getResources().getColor(R.color.grey));
                textMyTeam.setTextColor(getResources().getColor(R.color.green));
                textMyProfile.setTextColor(getResources().getColor(R.color.grey));
//                imageLeaderBoard.setBackgroundResource(R.drawable.hub_icon);
//                imageMyTeam.setBackgroundResource(R.drawable.news_active_icon);
//                imageMyProfile.setBackgroundResource(R.drawable.directory_icon);
                break;

            case 2:
                mFragment = new ParentFragment();
                mFragmentTitle = AppUtil.MYPROFILE_FRAGMENT;
                textLeaderBoard.setTextColor(getResources().getColor(R.color.grey));
                textMyTeam.setTextColor(getResources().getColor(R.color.grey));
                textMyProfile.setTextColor(getResources().getColor(R.color.green));
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
