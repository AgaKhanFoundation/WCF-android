package com.android.wcf.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Malik Khoja on 4/27/2017.
 */

public class AppUtil {

    public static final int PERMISSION_REQUEST_CODE = 1001;
    public static final String LEADERBOARD_FRAGMENT = "leaderBoardFragment";
    public static final String MYTEAM_FRAGMENT = "myTeamFragment";
    public static final String MYPROFILE_FRAGMENT = "myProfileFragment";
    public static final String SETTINGS_FRAGMENT = "settingsFragment";
    public static final String SPONSERERS_FRAGMENT = "sponserersFragment";

    public static void hideKeyboard(Context mContext, View mview) {
        InputMethodManager im = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(mview.getWindowToken(), 0);
    }
}
