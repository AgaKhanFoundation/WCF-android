package com.android.wcf.fitbit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.fitbitsdk.authentication.AuthenticationConfiguration
import com.fitbitsdk.authentication.AuthenticationConfigurationBuilder
import com.fitbitsdk.authentication.ClientCredentials
import com.fitbitsdk.authentication.Scope
import java.util.concurrent.TimeUnit


class FitbitHelper {

    companion object {
        @JvmStatic fun generateAuthenticationConfiguration(context: Context, mainActivityClass: Class<out Activity>): AuthenticationConfiguration {

            try {
                val ai = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
                val bundle = ai.metaData

                // Load clientId and redirectUrl from application manifest
                val clientId = bundle.getString("com.wcf.fitbit.CLIENT_ID")
                val clientSecret = bundle.getString("com.wcf.fitbit.CLIENT_SECRET")
                val redirectUrl = bundle.getString("com.wcf.fitbit.REDIRECT_URL")

                val clientCredentials = ClientCredentials(clientId, clientSecret, redirectUrl)

                return AuthenticationConfigurationBuilder()
                        .setClientCredentials(clientCredentials)
                        .setBeforeLoginActivity(Intent(context, mainActivityClass))
                        .setTokenExpiresIn(TimeUnit.DAYS.toMillis(1))
                        .addRequiredScopes(Scope.activity)
                        .addOptionalScopes(Scope.settings, Scope.profile)
                        .setLogoutOnAuthFailure(true)
                        .build()

            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
        const val FITBIT_SHARED_PREF_NAME = "Fitbit"
        const val FITBIT_DEVICE_SELECTED = "fitbit_device_selected"
        const val FITBIT_DEVICE_LOGGED_IN = "fitbit_device_logged_in"
        const val FITBIT_DEVICE_INFO =  "fitbit_device_info"
        const val FITBIT_USER_DISPLAY_NAME = "fitbit_user_display_name"

       @JvmField var authenticationConfiguration:AuthenticationConfiguration? = null
    }
}