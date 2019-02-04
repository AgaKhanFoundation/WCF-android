package com.android.wcf.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.wcf.application.WCFApplication;

public class SharedPreferencesUtil {

    /* group different preferences under types */
    private static String PREF_TYPE_NAME_APP = "WCF_APP";

    /* property names for individual preferences */
    private static String PREF_NAME_MY_FACEBOOK_ID = "MY_FBID";
    private static String PREF_NAME_MY_PARTICIPANT_ID = "MY_PARTICIPANT_ID";
    private static String PREF_NAME_MY_TEAM_ID = "MY_TEAM_ID";
    private static String PREF_NAME_MY_ACTIVE_EVENT_ID = "MY_ACTIVE_EVENT_ID";

    public static final String DEFAULT_FB_ID = "skfbid1";   //TODO: this will be null for not logged-in person
    public static final int DEFAULT_TEAM_ID = 1;            //TODO: this will be -1 for represent unassigned teamId
    public static final int DEFAULT_PARTICIPANT_ID = 1;     //TODO: this will be -1 for not logged-in person
    public static final int DEFAULT_ACTIVE_EVENT_ID = 1;     //TODO: this will be -1 for participants who have not selected event when list is API driven

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


    public static void saveMyActiveEvent(int eventId){
        SharedPreferences.Editor editor = getSharedPrefs(PREF_TYPE_NAME_APP).edit();
        editor.putInt(PREF_NAME_MY_ACTIVE_EVENT_ID, eventId);
        editor.commit();

    }

    public static void saveMyFacebookId(String fbid) {
        SharedPreferences.Editor editor = getSharedPrefs(PREF_TYPE_NAME_APP).edit();
        editor.putString(PREF_NAME_MY_FACEBOOK_ID, fbid);
        editor.commit();
    }

    public static void saveMyTeamId(int teamId) {
        SharedPreferences.Editor editor = getSharedPrefs(PREF_TYPE_NAME_APP).edit();
        editor.putInt(PREF_NAME_MY_TEAM_ID, teamId);
        editor.commit();
    }

    public static void saveMyParticipantId(int participantId) {
        SharedPreferences.Editor editor = getSharedPrefs(PREF_TYPE_NAME_APP).edit();
        editor.putInt(PREF_NAME_MY_PARTICIPANT_ID, participantId);
        editor.commit();
    }

    public static String getMyFacebookId() {
        SharedPreferences preferences = getSharedPrefs(PREF_TYPE_NAME_APP);
        return preferences.getString(PREF_NAME_MY_FACEBOOK_ID, DEFAULT_FB_ID);
    }

    public static int getMyParticipantId() {
        SharedPreferences preferences = getSharedPrefs(PREF_TYPE_NAME_APP);
        return preferences.getInt(PREF_NAME_MY_PARTICIPANT_ID, DEFAULT_PARTICIPANT_ID);
    }

    public static int getMyTeamId() {
        SharedPreferences preferences = getSharedPrefs(PREF_TYPE_NAME_APP);
        return preferences.getInt(PREF_NAME_MY_TEAM_ID, DEFAULT_TEAM_ID);
    }

    public static int getMyActiveEventId() {
        SharedPreferences preferences = getSharedPrefs(PREF_TYPE_NAME_APP);
        return preferences.getInt(PREF_NAME_MY_ACTIVE_EVENT_ID, DEFAULT_ACTIVE_EVENT_ID);
    }

}
