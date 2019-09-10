package com.android.wcf.fitbit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import com.fitbitsdk.authentication.*
import com.fitbitsdk.service.FitbitService
import com.fitbitsdk.service.models.ActivitySteps
import com.fitbitsdk.service.models.DailyActivitySummary
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit


class FitbitHelper {

    companion object {
        val TAG: String = "FitbitHelper"

        @JvmStatic
        fun generateAuthenticationConfiguration(context: Context, mainActivityClass: Class<out Activity>?): AuthenticationConfiguration {

            try {
                val applicationInfoMetaData = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
                val bundle = applicationInfoMetaData.metaData

                // Load clientId and redirectUrl from application manifest
                val clientId = bundle.getString("com.wcf.fitbit.CLIENT_ID")
                val clientSecret = bundle.getString("com.wcf.fitbit.CLIENT_SECRET")
                val redirectUrl = bundle.getString("com.wcf.fitbit.REDIRECT_URL")

                val clientCredentials = ClientCredentials(clientId, clientSecret, redirectUrl)

                val builder = AuthenticationConfigurationBuilder()
                        .setClientCredentials(clientCredentials)
                        .setTokenExpiresIn(TimeUnit.DAYS.toMillis(365))
                        .addRequiredScopes(Scope.activity)
                        .addOptionalScopes(Scope.settings, Scope.profile)
                        .setLogoutOnAuthFailure(false)

                mainActivityClass?.let {
                    builder.setBeforeLoginActivity(Intent(context, it))
                }
                return builder.build()

            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }

        const val FITBIT_SHARED_PREF_NAME = AuthenticationManager.FITBIT_SHARED_PREFERENCE_NAME
        const val FITBIT_DEVICE_SELECTED = "fitbit_device_selected"
        const val FITBIT_DEVICE_LOGGED_IN = "fitbit_device_logged_in"
        const val FITBIT_DEVICE_INFO = "fitbit_device_info"
        const val FITBIT_USER_DISPLAY_NAME = "fitbit_user_display_name"

        @JvmField
        var authenticationConfiguration: AuthenticationConfiguration? = null

        fun getSteps(context: Context, startDate: String, endDate: String, callback: FitbitStepsResponseCallback) {
            val sharedPreferences = context.getSharedPreferences("Fitbit", Context.MODE_PRIVATE)
            val fService = FitbitService(sharedPreferences, AuthenticationManager.getAuthenticationConfiguration().clientCredentials)
            fService.getActivityService().getActivityStepsForDateRange(startDate, endDate)
                    .enqueue(object : Callback<ActivitySteps> {
                        override fun onResponse(call: Call<ActivitySteps>, response: Response<ActivitySteps>) {
                            if (response.isSuccessful) {
                                Log.d(TAG, "fitbit steps success")
                                val stepsData: ActivitySteps = response.body() ?: ActivitySteps()
                                if (callback != null) {
                                    callback.onFitbitStepsRetrieved(stepsData)
                                }
                            } else {
                                Log.d(TAG, "fitbit steps error:" + response.code() + "- " + response.message())
                                if (response.code() == 401) {
                                    //TODO: ensure Interceptor is refreshing the expired token when errorcode is 401...
                                }
                                if (callback != null) {
                                    val error = Throwable(response.message())
                                    callback.onFitbitStepsError(error)
                                }
                            }
                        }

                        override fun onFailure(call: Call<ActivitySteps>, t: Throwable) {
                            Log.e(TAG, "fitbit steps failure: " + t.message)
                            if (callback != null) {
                                callback.onFitbitStepsError(t)
                            }
                        }
                    })
        }

        fun getActivities(context: Context, day: String, callback: FitbitActivityResponseCallback) {
            val sharedPreferences = context.getSharedPreferences("Fitbit", Context.MODE_PRIVATE)
            val fService = FitbitService(sharedPreferences, AuthenticationManager.getAuthenticationConfiguration().clientCredentials)
            fService.getActivityService().getDailyActivitySummary(day)
                    .enqueue(object : Callback<DailyActivitySummary> {
                        override fun onResponse(call: Call<DailyActivitySummary>, response: Response<DailyActivitySummary>) {
                            if (response.isSuccessful) {
                                Log.d(TAG, "fitbit activity success")
                                val activityData: DailyActivitySummary = response.body()
                                        ?: DailyActivitySummary()
                                if (callback != null) {
                                    callback.onFitbitActivityRetrieved(activityData)
                                }
                            } else {
                                Log.d(TAG, "fitbit activity error:\n" + response.errorBody())
                                if (callback != null) {
                                    val error = Throwable(response.errorBody().toString())
                                    callback.onFitbitActivityError(error)
                                }
                            }

                        }

                        override fun onFailure(call: Call<DailyActivitySummary>, t: Throwable) {
                            Log.d(TAG, "fitbit activity error:" + t.message)
                            if (callback != null) {
                                callback.onFitbitActivityError(t)
                            }
                        }
                    })
        }

    }

}