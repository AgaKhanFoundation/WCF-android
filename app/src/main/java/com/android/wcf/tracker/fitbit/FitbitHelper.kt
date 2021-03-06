package com.android.wcf.tracker.fitbit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import com.android.wcf.tracker.TrackerLoginStatusCallback
import com.android.wcf.tracker.TrackerStepsCallback
import com.android.wcf.tracker.TrackingHelper
import com.fitbitsdk.authentication.*
import com.fitbitsdk.service.FitbitService
import com.fitbitsdk.service.models.ActivitySteps
import com.fitbitsdk.service.models.UserProfile
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.HttpURLConnection
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
                        .setTokenExpiresIn(TimeUnit.DAYS.toMillis(30))
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

        @JvmStatic
        fun getSteps(context: Context, startDate: Date, endDate: Date, callback: TrackerStepsCallback?) {

            val sdf = SimpleDateFormat("yyyy-MM-dd")

            var startDateString =  sdf.format(startDate)

            var endDateString = sdf.format(endDate)

            val sharedPreferences = TrackingHelper.getSharedPrefs()!!
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
                                    callback?.trackerNeedsReLogin(TrackingHelper.FITBIT_TRACKING_SOURCE_ID)
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

        @JvmStatic
        fun validateLogin(context:Context, callback: TrackerLoginStatusCallback?) {
            val sharedPreferences = TrackingHelper.getSharedPrefs()!!

            val fService = FitbitService(sharedPreferences, AuthenticationManager.getAuthenticationConfiguration().clientCredentials)
            fService.getUserService().profile().enqueue(object : Callback<UserProfile> {
                override fun onResponse(call: Call<UserProfile>?, response: Response<UserProfile>?) {
                    Log.d(TAG, "validateLogin Response: " + response?.body()?.user?.displayName ?: response?.errorBody().toString())

                    response?.let {
                        if (response.isSuccessful()) {
                            callback?.onTrackerLoginValid(TrackingHelper.FITBIT_TRACKING_SOURCE_ID)
                            return
                        }

                        if (it.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                            callback?.trackerNeedsReLogin(TrackingHelper.FITBIT_TRACKING_SOURCE_ID)
                            return
                        }

                        if (it.body() == null || it.errorBody() != null) {
                            val errorString:String? = it.errorBody()?.string()

                            errorString?.let {
                                Log.e(TAG, "validateLogin error: $errorString")
                            }
                            callback?.onTrackerLoginVerifyError()
                        }

                    } ?: callback?.onTrackerLoginVerifyError()
                }

                override fun onFailure(call: Call<UserProfile>?, t: Throwable?) {
                    Log.d(TAG, "validateLogin Error: " + t?.message)

                   //TODO: specfic auth failed check vs networtk error etc ?
                    callback?.onTrackerLoginVerifyError()

                }
            })
        }
    }

}