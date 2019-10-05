package com.android.wcf.tracker

import android.content.Context
import com.fitbitsdk.authentication.AuthenticationManager
import java.text.SimpleDateFormat
import java.util.*

class TrackingHelper {

    companion object {
        public const val TRACKER_DATA_LAST_SAVED_AT_TIME = "tracking_saved_at_time"
        public const val TRACKER_DATA_LAST_SAVED_DATE = "tracking_last_saved_date"
        const val SAVE_AFTER_MINUTES = 30

        const val SELECTED_TRACKING_SOURCE_ID = "tracking_source_id"

        const val INVALID_TRACKING_SOURCE_ID = -1 //TODO should be lookup value from list of Sources API
        const val FITBIT_TRACKING_SOURCE_ID = 1 //TODO should be lookup value from list of Sources API
        const val GOOGLE_FIT_TRACKING_SOURCE_ID = 2 //TODO should be lookup value from list of Sources API

        const val TRACKER_SHARED_PREF_NAME = AuthenticationManager.FITBIT_SHARED_PREFERENCE_NAME

        const val FITBIT_DEVICE_SELECTED = "fitbit_device_selected"
        const val FITBIT_DEVICE_LOGGED_IN = "fitbit_device_logged_in"
        const val FITBIT_DEVICE_INFO = "fitbit_device_info"
        const val FITBIT_USER_DISPLAY_NAME = "fitbit_user_display_name"

        const val GOOGLE_FIT_APP_SELECTED = "googlefit_app_selected"
        const val GOOGLE_FIT_APP_LOGGED_IN = "googlefit_app_logged_in"
        const val GOOGLE_FIT_USER_DISPLAY_NAME = "googlefit_user_display_name"
        const val GOOGLE_FIT_USER_DISPLAY_EMAIL = "googlefit_user_display_email"


        fun isTimeToSave(context: Context): Boolean {
            val deviceSharedPreferences = context.applicationContext.getSharedPreferences(TRACKER_SHARED_PREF_NAME, Context.MODE_PRIVATE)
            deviceSharedPreferences.let {
                var lastSaveAtTime = deviceSharedPreferences.getLong(TRACKER_DATA_LAST_SAVED_AT_TIME, 0)

                return Date().time - lastSaveAtTime > SAVE_AFTER_MINUTES * 60 * 1000
            }
        }

        fun trackerDataSaved(context: Context,  lastSavedStepDate:String) {
            val deviceSharedPreferences = context.applicationContext.getSharedPreferences(TRACKER_SHARED_PREF_NAME, Context.MODE_PRIVATE)
            deviceSharedPreferences.let {
                deviceSharedPreferences.edit().putLong(TRACKER_DATA_LAST_SAVED_AT_TIME, Date().time).commit()
                deviceSharedPreferences.edit().putString(TRACKER_DATA_LAST_SAVED_DATE,lastSavedStepDate).commit()

            }
        }

        fun lastTrackerDataSavedDate(context: Context):String {
            val deviceSharedPreferences = context.getSharedPreferences(TRACKER_SHARED_PREF_NAME, Context.MODE_PRIVATE)
            deviceSharedPreferences.let {
               var lastSaved:String? = it.getString(TRACKER_DATA_LAST_SAVED_DATE, "");
                lastSaved?.let {
                    return it
                }
            }
            return ""
        }

        fun saveTrackerSelection(context:Context, deviceSelection:Boolean, appSelection: Boolean, trackerSourceId:Int) {
            val sharedPreferences = context.applicationContext.getSharedPreferences(TRACKER_SHARED_PREF_NAME, Context.MODE_PRIVATE)

            sharedPreferences.edit().putBoolean(FITBIT_DEVICE_SELECTED, deviceSelection).commit()
            sharedPreferences.edit().putBoolean(GOOGLE_FIT_APP_SELECTED, appSelection).commit()
            sharedPreferences.edit().putInt(SELECTED_TRACKING_SOURCE_ID, trackerSourceId).commit()
        }

        fun clearTrackerSelection(context:Context) {
            val sharedPreferences = context.applicationContext.getSharedPreferences(TRACKER_SHARED_PREF_NAME, Context.MODE_PRIVATE)

            sharedPreferences.edit().remove(FITBIT_DEVICE_SELECTED).commit()
            sharedPreferences.edit().remove(GOOGLE_FIT_APP_SELECTED).commit()
            sharedPreferences.edit().remove(SELECTED_TRACKING_SOURCE_ID).commit()
        }
    }
}