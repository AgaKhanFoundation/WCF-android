package com.android.wcf.login

import android.content.Context
import android.content.SharedPreferences
import com.android.wcf.application.WCFApplication
import com.android.wcf.helper.SharedPreferencesUtil.PREF_TYPE_NAME_APP
import com.android.wcf.model.Constants.Companion.LOGIN_CHECK_DELTA_HOURS
import java.util.*

class LoginHelper {
    companion object {
       const val LAST_LOGIN_CHECK_TIME = "last_login_check_time"
       const val LOGIN_CHECK_AFTER_MILLI = LOGIN_CHECK_DELTA_HOURS * 60 * 60 * 1000

        fun isTimeToValidateLogin(): Boolean {
            val deviceSharedPreferences = getSharedPrefs()
            deviceSharedPreferences.let {
                var lastSaveAtTime = deviceSharedPreferences.getLong(LAST_LOGIN_CHECK_TIME, 0)

                return Date().time - lastSaveAtTime > LOGIN_CHECK_AFTER_MILLI
            }
        }


        fun loginIsValid() {
            val deviceSharedPreferences = getSharedPrefs()
            deviceSharedPreferences.let {

                deviceSharedPreferences.let {
                    deviceSharedPreferences.edit().putLong(LAST_LOGIN_CHECK_TIME, Date().time).commit()

                }
            }
        }

        fun clearLoginCheck() {
            val deviceSharedPreferences = getSharedPrefs()
            deviceSharedPreferences.let {

                deviceSharedPreferences.let {
                    deviceSharedPreferences.edit().remove(LAST_LOGIN_CHECK_TIME).commit()
                }
            }
        }

        fun getSharedPrefs(): SharedPreferences {
            return WCFApplication.instance.getSharedPreferences(PREF_TYPE_NAME_APP, Context.MODE_PRIVATE)
        }

    }
}