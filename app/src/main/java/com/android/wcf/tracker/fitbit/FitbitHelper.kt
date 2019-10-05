package com.android.wcf.tracker.fitbit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import com.android.wcf.tracker.TrackerLoginStatusCallback
import com.android.wcf.tracker.TrackerStepsCallback
import com.android.wcf.tracker.TrackingHelper
import com.android.wcf.tracker.TrackingHelper.Companion.TRACKER_SHARED_PREF_NAME
import com.fitbitsdk.authentication.*
import com.fitbitsdk.service.FitbitService
import com.fitbitsdk.service.models.ActivitySteps
import com.fitbitsdk.service.models.UserProfile
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
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

        @JvmField
        var authenticationConfiguration: AuthenticationConfiguration? = null

        fun getSteps(context: Context, startDate: Date, endDate: Date, callback: TrackerStepsCallback?) {

            val sdf = SimpleDateFormat("yyyy-MM-dd")

            var startDateString =  sdf.format(startDate)

            var endDateString = sdf.format(endDate)

            val sharedPreferences = context.getSharedPreferences(TRACKER_SHARED_PREF_NAME, Context.MODE_PRIVATE)
            val fService = FitbitService(sharedPreferences, AuthenticationManager.getAuthenticationConfiguration().clientCredentials)
            fService.getActivityService().getActivityStepsForDateRange(startDateString, endDateString)
                    .enqueue(object : Callback<ActivitySteps> {
                        override fun onResponse(call: Call<ActivitySteps>, response: Response<ActivitySteps>) {
                            if (response.isSuccessful) {
                                Log.d(TAG, "fitbit steps success")
                                val stepsData: ActivitySteps = response.body() ?: ActivitySteps()
                                if (callback != null) {
                                    callback.onTrackerStepsRetrieved(stepsData)
                                }
                            } else {
                                Log.d(TAG, "fitbit steps error:" + response.code() + "- " + response.message())
                                if (response.code() == 401) {
                                    //TODO: ensure Interceptor is refreshing the expired token when errorcode is 401...
                                }
                                if (callback != null) {
                                    val error = Throwable(response.message())
                                    callback.onTrackerStepsError(error)
                                }
                            }
                        }

                        override fun onFailure(call: Call<ActivitySteps>, t: Throwable) {
                            Log.e(TAG, "fitbit steps failure: " + t.message)
                            if (callback != null) {
                                callback.onTrackerStepsError(t)
                            }
                        }
                    })
        }

        fun validateLogin(context:Context, callback: TrackerLoginStatusCallback?) {
            val sharedPreferences = context.getSharedPreferences(TrackingHelper.TRACKER_SHARED_PREF_NAME, Context.MODE_PRIVATE)

            val fService = FitbitService(sharedPreferences, AuthenticationManager.getAuthenticationConfiguration().clientCredentials)
            fService.getUserService().profile().enqueue(object : Callback<UserProfile> {
                override fun onResponse(call: Call<UserProfile>?, response: Response<UserProfile>?) {
                    Log.d(TAG, "Response: " + response?.body()?.user?.displayName)

                    response?.let {
                        if (it.body() == null || it.errorBody() != null) {
                            callback?.onTrackerLoginNotValid()
                        }
                        else {
                            callback?.onTrackerLoginValid(TrackingHelper.FITBIT_TRACKING_SOURCE_ID)
                        }
                    } ?: callback?.onTrackerLoginNotValid()
                }

                override fun onFailure(call: Call<UserProfile>?, t: Throwable?) {
                    Log.d(TAG, "Error: " + t?.message)
                    if (callback != null) {
                        callback?.onTrackerLoginNotValid()
                    }
                }
            })
        }

//        fun getActivities(context: Context, day: String, callback: FitbitActivityResponseCallback?) {
//            val trackersSharedPreferences = context.getSharedPreferences(TrackingHelper.TRACKER_SHARED_PREF_NAME, Context.MODE_PRIVATE)
//            val fService = FitbitService(trackersSharedPreferences, AuthenticationManager.getAuthenticationConfiguration().clientCredentials)
//            fService.getActivityService().getDailyActivitySummary(day)
//                    .enqueue(object : Callback<DailyActivitySummary> {
//                        override fun onResponse(call: Call<DailyActivitySummary>, response: Response<DailyActivitySummary>) {
//                            if (response.isSuccessful) {
//                                Log.d(TAG, "fitbit activity success")
//                                val activityData: DailyActivitySummary = response.body()
//                                        ?: DailyActivitySummary()
//                                if (callback != null) {
//                                    callback.onFitbitActivityRetrieved(activityData)
//                                }
//                            } else {
//                                Log.d(TAG, "fitbit activity error:\n" + response.errorBody())
//                                if (callback != null) {
//                                    val error = Throwable(response.errorBody().toString())
//                                    callback.onFitbitActivityError(error)
//                                }
//                            }
//
//                        }
//
//                        override fun onFailure(call: Call<DailyActivitySummary>, t: Throwable) {
//                            Log.d(TAG, "fitbit activity error:" + t.message)
//                            if (callback != null) {
//                                callback.onFitbitActivityError(t)
//                            }
//                        }
//                    })
//        }

    }

}