package com.android.wcf.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.wcf.application.WCFApplication;
import com.android.wcf.model.Constants;

import static com.android.wcf.model.Constants.AUTH_FACEBOOK;

public class SharedPreferencesUtil {

    private static final String TAG = SharedPreferencesUtil.class.getSimpleName();
    /* group different preferences under types */
    private static String PREF_TYPE_NAME_APP = "WCF_APP";

    /* property names for individual preferences */
    private static String PREF_NAME_MY_FACEBOOK_ID = "MY_FB_ID";

    private static String PREF_NAME_MY_AUTH_METHOD_ID = "MY_AUTH_METHOD_NAME";
    private static String PREF_NAME_MY_LOGIN_ID = "MY_LOGIN_ID";
    private static String PREF_NAME_MY_TEAM_ID = "MY_TEAM_ID";
    private static String PREF_NAME_MY_ACTIVE_EVENT_ID = "MY_ACTIVE_EVENT_ID";
    private static String PREF_NAME_SHOW_ONBOARD_TUTORIAL = "SHOW_ONBOARD_TUTORIAL";

    private static String PREF_NAME_AKF_PROFILE_CREATED = "akf_profile_created";

    private static String PREF_NAME_USER_LOGGED_IN = "isUserLoggedIn";
    private static String PREF_NAME_USER_FULL_NAME = "userFullName";
    private static String PREF_NAME_USER_EMAIL = "userEmail";
    private static String PREF_NAME_USER_PROFILE_PHOTO_URL = "userProfilePhotoUrl";
    private static String PREF_NAME_USER_STEPS_COMMITTED = "userStepsCommitted";

    public static final String DEFAULT_FB_ID = null;   //TODO: this will be null for not logged-in person
    public static final int DEFAULT_TEAM_ID = -1;            //TODO: this will be -1 for represent unassigned teamId
    public static final int DEFAULT_ACTIVE_EVENT_ID = -1;     //TODO: this will be -1 for participants who have not selected event when list is API driven

    public static final int UNKNOWN_ACTIVE_EVENT_ID = -1;
    public static final int UNKNOWN_TEAM_ID = -1;
    public static final int UNKNOWN_PARTICIPANT_ID = -1;

    public static SharedPreferences getSharedPrefs(String preferanceTypeName) {
        return WCFApplication.instance.getSharedPreferences(preferanceTypeName, Context.MODE_PRIVATE);
    }

    public static void clearAll() {
        SharedPreferences.Editor editor = getSharedPrefs(PREF_TYPE_NAME_APP).edit();
        editor.clear();
        editor.commit();
    }

    public static void saveMyLoginId(String loginId, String authSource) {
        if (AUTH_FACEBOOK.equals(authSource)) {
            saveMyFacebookId(loginId);
            saveMyAuthSource(AUTH_FACEBOOK);
        } else {
            Log.w(TAG, "Unsupported authentication source " + authSource);
        }
    }

    public static void saveMyAuthSource(String authSource) {
        SharedPreferences.Editor editor = getSharedPrefs(PREF_TYPE_NAME_APP).edit();
        editor.putString(PREF_NAME_MY_AUTH_METHOD_ID, authSource);
        editor.commit();
    }

    public static String getMyAuthSource() {
        SharedPreferences preferences = getSharedPrefs(PREF_TYPE_NAME_APP);
        return preferences.getString(PREF_NAME_MY_AUTH_METHOD_ID, AUTH_FACEBOOK);
    }

    public static void saveMyFacebookId(String fbId) {
        SharedPreferences.Editor editor = getSharedPrefs(PREF_TYPE_NAME_APP).edit();
        editor.putString(PREF_NAME_MY_FACEBOOK_ID, fbId);
        editor.commit();
    }

    public static String getMyFacebookId() {
        SharedPreferences preferences = getSharedPrefs(PREF_TYPE_NAME_APP);
        return preferences.getString(PREF_NAME_MY_FACEBOOK_ID, DEFAULT_FB_ID);
    }

    public static String getMyParticipantId() {
        String authSource = getMyAuthSource();
        if (AUTH_FACEBOOK.equals(authSource)) {
            return getMyFacebookId();
        }
        Log.w(TAG, "Unsupported authentication source " + authSource);
        return "";
    }

    public static int getMyTeamId() {
        SharedPreferences preferences = getSharedPrefs(PREF_TYPE_NAME_APP);
        return preferences.getInt(PREF_NAME_MY_TEAM_ID, DEFAULT_TEAM_ID);
    }

    public static void saveMyActiveEvent(int eventId) {
        SharedPreferences.Editor editor = getSharedPrefs(PREF_TYPE_NAME_APP).edit();
        editor.putInt(PREF_NAME_MY_ACTIVE_EVENT_ID, eventId);
        editor.commit();
    }

    public static void saveMyTeamId(int teamId) {
        SharedPreferences.Editor editor = getSharedPrefs(PREF_TYPE_NAME_APP).edit();
        editor.putInt(PREF_NAME_MY_TEAM_ID, teamId);
        editor.commit();
    }

    public static void clearMyLogin() {
        SharedPreferences preferences = getSharedPrefs(PREF_TYPE_NAME_APP);
        preferences.edit()
                .remove(PREF_NAME_USER_LOGGED_IN)
                .commit();
    }

    public static void clearMyTeamId() {
        SharedPreferences preferences = getSharedPrefs(PREF_TYPE_NAME_APP);
        preferences.edit().remove(PREF_NAME_MY_TEAM_ID).commit();
    }

    public static void clearMyFacebookId() {
        SharedPreferences preferences = getSharedPrefs(PREF_TYPE_NAME_APP);
        preferences.edit().remove(PREF_NAME_MY_FACEBOOK_ID).commit();
    }

    public static int getMyActiveEventId() {
        SharedPreferences preferences = getSharedPrefs(PREF_TYPE_NAME_APP);
        return preferences.getInt(PREF_NAME_MY_ACTIVE_EVENT_ID, DEFAULT_ACTIVE_EVENT_ID);
    }

    public static void cleartMyActiveEventId() {
        SharedPreferences preferences = getSharedPrefs(PREF_TYPE_NAME_APP);
        preferences.edit().remove(PREF_NAME_MY_ACTIVE_EVENT_ID).commit();
    }

    public static void saveShowOnboardingTutorial(boolean show) {
        SharedPreferences.Editor editor = getSharedPrefs(PREF_TYPE_NAME_APP).edit();
        editor.putBoolean(PREF_NAME_SHOW_ONBOARD_TUTORIAL, show);
        editor.commit();
    }

    public static boolean getShowOnboardingTutorial() {
        SharedPreferences preferences = getSharedPrefs(PREF_TYPE_NAME_APP);
        return preferences.getBoolean(PREF_NAME_SHOW_ONBOARD_TUTORIAL, true);
    }

    public static void saveUserLoggedIn(boolean loggedIn) {
        SharedPreferences.Editor editor = getSharedPrefs(PREF_TYPE_NAME_APP).edit();
        editor.putBoolean(PREF_NAME_USER_LOGGED_IN, loggedIn);
        editor.commit();
    }

    public static boolean isUserLoggedIn() {
        SharedPreferences preferences = getSharedPrefs(PREF_TYPE_NAME_APP);
        return preferences.getBoolean(PREF_NAME_USER_LOGGED_IN, false);
    }


    public static void saveUserFullName(String userFullname) {
        SharedPreferences.Editor editor = getSharedPrefs(PREF_TYPE_NAME_APP).edit();
        editor.putString(PREF_NAME_USER_FULL_NAME, userFullname);
        editor.commit();
    }

    public static String getUserFullName() {
        SharedPreferences preferences = getSharedPrefs(PREF_TYPE_NAME_APP);
        return preferences.getString(PREF_NAME_USER_FULL_NAME, null);
    }

    public static void saveUserEmail(String userEmail) {
        SharedPreferences.Editor editor = getSharedPrefs(PREF_TYPE_NAME_APP).edit();
        editor.putString(PREF_NAME_USER_EMAIL, userEmail);
        editor.commit();
    }

    public static String getPrefNameUserEmail() {
        SharedPreferences preferences = getSharedPrefs(PREF_TYPE_NAME_APP);
        return preferences.getString(PREF_NAME_USER_EMAIL, null);
    }

    public static void saveUserProfilePhotoUrl(String profileUrl) {
        SharedPreferences.Editor editor = getSharedPrefs(PREF_TYPE_NAME_APP).edit();
        editor.putString(PREF_NAME_USER_PROFILE_PHOTO_URL, profileUrl);
        editor.commit();
    }

    public static String getUserProfilePhotoUrl() {
        SharedPreferences preferences = getSharedPrefs(PREF_TYPE_NAME_APP);
        return preferences.getString(PREF_NAME_USER_PROFILE_PHOTO_URL, null);
    }

    public static int getMyStepsCommitted() {
        SharedPreferences preferences = getSharedPrefs(PREF_TYPE_NAME_APP);
        return preferences.getInt(PREF_NAME_USER_STEPS_COMMITTED, Constants.PARTICIPANT_COMMITMENT_STEPS_DEFAULT);
    }

    public static void savetMyStepsCommitted(int steps) {
        SharedPreferences.Editor editor = getSharedPrefs(PREF_TYPE_NAME_APP).edit();
        editor.putInt(PREF_NAME_USER_STEPS_COMMITTED, steps);
        editor.commit();
    }

    public static void clearMyStepsCommitted() {
        SharedPreferences preferences = getSharedPrefs(PREF_TYPE_NAME_APP);
        preferences.edit().remove(PREF_NAME_USER_STEPS_COMMITTED).commit();
    }

    public static boolean getAkfProfileCreated() {
        SharedPreferences preferences = getSharedPrefs(PREF_TYPE_NAME_APP);
        return preferences.getBoolean(PREF_NAME_AKF_PROFILE_CREATED, false);
    }

    public static void saveAkfProfileCreated(boolean createdFlag) {
        SharedPreferences.Editor editor = getSharedPrefs(PREF_TYPE_NAME_APP).edit();
        editor.putBoolean(PREF_NAME_AKF_PROFILE_CREATED, createdFlag);
        editor.commit();
    }


    public static void clearAkfProfileCreated() {
        SharedPreferences preferences = getSharedPrefs(PREF_TYPE_NAME_APP);
        preferences.edit()
                .remove(PREF_NAME_AKF_PROFILE_CREATED)
                .commit();
    }
}
