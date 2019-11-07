package com.android.wcf.tracker

import android.content.Context
import android.content.SharedPreferences
import com.android.wcf.application.WCFApplication
import com.android.wcf.model.Constants
import com.fitbitsdk.authentication.AuthenticationManager
import java.util.*

class TrackingHelper {

    companion object {
        const val TRACKER_SHARED_PREF_NAME = AuthenticationManager.FITBIT_SHARED_PREFERENCE_NAME

        const val TRACKER_DATA_LAST_SAVED_AT_TIME = "tracking_saved_at_time"
        const val TRACKER_DATA_LAST_SAVED_DATE = "tracking_last_saved_date"

        const val LAST_TRACKER_CHECK_TIME = "tracking_last_check_time"

        const val INVALID_TRACKING_SOURCE_ID = -1 //TODO should be lookup value from list of Sources API
        const val FITBIT_TRACKING_SOURCE_ID = 3 //TODO should be lookup value from list of Sources API
        const val GOOGLE_FIT_TRACKING_SOURCE_ID = 2 //TODO should be lookup value from list of Sources API

        const val FITBIT_DEVICE_SELECTED = "fitbit_device_selected"
        const val FITBIT_DEVICE_LOGGED_IN = "fitbit_device_logged_in"
        const val FITBIT_DEVICE_INFO = "fitbit_device_info"
        const val FITBIT_USER_DISPLAY_NAME = "fitbit_user_display_name"

        const val GOOGLE_FIT_APP_SELECTED = "googlefit_app_selected"
        const val GOOGLE_FIT_APP_LOGGED_IN = "googlefit_app_logged_in"
        const val GOOGLE_FIT_USER_DISPLAY_NAME = "googlefit_user_display_name"
        const val GOOGLE_FIT_USER_DISPLAY_EMAIL = "googlefit_user_display_email"

        const val TRACKER_CHECK_AFTER_MILLI = Constants.TRACKER_CHECK_DELTA_HOURS * 60 * 60 * 1000
        const val STEPS_DATA_SAVE_AFTER_MILLI = Constants.SAVE_STEPS_TO_SERVER_DELTA_MIN * 60 * 1000

        @JvmStatic
        fun isTimeToSave(): Boolean {
            val sharedPreferences = getSharedPrefs()
            sharedPreferences?.let {
                var lastSaveAtTime = it.getLong(TRACKER_DATA_LAST_SAVED_AT_TIME, 0)

                return Date().time - lastSaveAtTime > STEPS_DATA_SAVE_AFTER_MILLI
            }
            return true
        }

        @JvmStatic
        fun trackerDataSaved(lastSavedStepDate: String) {
            val sharedPreferences = getSharedPrefs()
            sharedPreferences?.let {
                it.edit().putLong(TRACKER_DATA_LAST_SAVED_AT_TIME, Date().time).commit()
                it.edit().putString(TRACKER_DATA_LAST_SAVED_DATE, lastSavedStepDate).commit()
            }
        }

        @JvmStatic
        fun lastTrackerDataSavedDate(): String {
            val sharedPreferences = getSharedPrefs()
            sharedPreferences?.let {
                var lastSaved: String? = it.getString(TRACKER_DATA_LAST_SAVED_DATE, "");
                lastSaved?.let {
                    return it
                }
            }
            return ""
        }

        @JvmStatic
        fun clearAll() {
            val sharedPreferences = getSharedPrefs()
            sharedPreferences?.let {
                it.edit().clear().commit()
            }
        }

        @JvmStatic
        fun clearTrackerSelection() {
            val sharedPreferences = getSharedPrefs()

            sharedPreferences?.let {

               it.edit().remove(FITBIT_DEVICE_SELECTED).commit()
               it.edit().remove(GOOGLE_FIT_APP_SELECTED).commit()
               it.edit().remove(LAST_TRACKER_CHECK_TIME).commit()
            }
        }

        @JvmStatic
        fun isTimeToValidateTrackerConnection(): Boolean {
            val sharedPreferences = getSharedPrefs()
            sharedPreferences?.let {
                var lastSaveAtTime = it.getLong(LAST_TRACKER_CHECK_TIME, 0)

                return Date().time - lastSaveAtTime > TRACKER_CHECK_AFTER_MILLI
            }
            return true
        }

        @JvmStatic
        fun trackerConnectionIsValid() {
            val sharedPreferences = getSharedPrefs()
            sharedPreferences?.let {
                it.edit().putLong(LAST_TRACKER_CHECK_TIME, Date().time).commit()
            }
        }

        @JvmStatic
        fun clearTrackerConnectionCheck() {
            val sharedPreferences = getSharedPrefs()
            sharedPreferences?.let {
                it.edit().remove(LAST_TRACKER_CHECK_TIME).commit()
            }
        }

        @JvmStatic
        fun googleFitLoggedIn(loggedIn: Boolean) {
            val sharedPreferences = getSharedPrefs()
            sharedPreferences?.let {
                it.edit().putBoolean(GOOGLE_FIT_APP_LOGGED_IN, loggedIn).commit()
            }
        }

        @JvmStatic
        fun fitbitLoggedIn(loggedIn: Boolean) {
            val sharedPreferences = getSharedPrefs()
            sharedPreferences?.let {
                it.edit().putBoolean(FITBIT_DEVICE_LOGGED_IN, loggedIn).commit()
            }
        }
        @JvmStatic
        fun getSelectedFitnessTracker(): Int {
            val sharedPreferences = getSharedPrefs()
            sharedPreferences?.let {

                val deviceLoggedIn = sharedPreferences.getBoolean(FITBIT_DEVICE_LOGGED_IN, false)
                val appLoggedIn = sharedPreferences.getBoolean(GOOGLE_FIT_APP_LOGGED_IN, false)

                if (deviceLoggedIn) {
                    return FITBIT_TRACKING_SOURCE_ID
                } else if (appLoggedIn) {
                    return GOOGLE_FIT_TRACKING_SOURCE_ID
                }
            }
            return INVALID_TRACKING_SOURCE_ID
        }

        fun getSharedPrefs(): SharedPreferences? {
            return WCFApplication.instance.getSharedPreferences(TRACKER_SHARED_PREF_NAME, Context.MODE_PRIVATE)
        }

    }
}