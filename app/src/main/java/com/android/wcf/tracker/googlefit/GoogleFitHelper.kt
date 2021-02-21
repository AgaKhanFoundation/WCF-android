package com.android.wcf.tracker.googlefit

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.android.wcf.helper.DateTimeHelper
import com.android.wcf.tracker.TrackerLoginStatusCallback
import com.android.wcf.tracker.TrackerStepsCallback
import com.android.wcf.tracker.TrackingHelper
import com.fitbitsdk.service.models.ActivitySteps
import com.fitbitsdk.service.models.Steps
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

interface GoogleFitSubscriptionCallback {
    fun onSubscriptionSuccess()

    fun onSubscriptionError(exception: Exception?)
}


class GoogleFitHelper {

    companion object {

        val TAG: String = "GoogleFitHelper"

        val googleFitFitnessDataOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE, FitnessOptions.ACCESS_READ)
                .build()

        val googleFitSignInOptions = GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .addExtension(googleFitFitnessDataOptions).build()

        @JvmStatic
        fun getSteps(context: Context, startDate: Date, endDate: Date, callback: TrackerStepsCallback?) {

            val readRequest = DataReadRequest.Builder()
                    .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                    .bucketByTime(1, TimeUnit.DAYS)
                    .setTimeRange(startDate.time, endDate.time, TimeUnit.MILLISECONDS)
                    .build()

            val sdf = SimpleDateFormat("yyyy-MM-dd")

            GoogleSignIn.getLastSignedInAccount(context)?.let { googleSignInAccount ->

                Fitness.getHistoryClient(context, googleSignInAccount)
                        .readData(readRequest)
                        .addOnSuccessListener { dataReadResponse ->
                            val buckets = dataReadResponse.buckets
                            val activitySteps: ActivitySteps = ActivitySteps()

                            for (bucket in buckets) {
                                val stepData: Steps = Steps()
                                stepData.date = sdf.format(bucket.getStartTime(TimeUnit.MILLISECONDS))
                                val dataSets = bucket.dataSets
                                var totalSteps = 0L

                                for (dataSet in dataSets) {
                                    //   if (dataSet.getDataType() == DataType.TYPE_STEP_COUNT_DELTA) {
                                    for (dp in dataSet.dataPoints) {
                                        for (field in dp.dataType.fields) {
                                            val steps = dp.getValue(field).asInt()
                                            totalSteps += steps
                                        }
                                    }
                                }
                                stepData.value = totalSteps
                                activitySteps.steps.add(stepData)
                                Log.i(TAG, "total steps for ${stepData.date}: $totalSteps")
                            }

                            callback?.onTrackerStepsRetrieved(activitySteps)

                        }
                        .addOnFailureListener { exception ->

                            Log.e(TAG, "GoogleFit steps error:", exception);

                            val errorCode = 1 // TODO: replace 1 with errorcode from Exception or add appropriate error check when token expired situation
                            if (1 == 401) {
                                callback?.trackerNeedsReLogin(TrackingHelper.GOOGLE_FIT_TRACKING_SOURCE_ID)
                            }

                            val error = Throwable(exception.message)
                            callback?.onTrackerStepsError(error)
                        }
            }
        }

        fun validateLogin(context: Context, callback: TrackerLoginStatusCallback?) {
            var endDate: Date = Date()
            var startDate: Date = DateTimeHelper.yesterday()
            var endDate2 = DateTimeHelper.getDateForNow()

            val readRequest = DataReadRequest.Builder()
                    .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                    .bucketByTime(1, TimeUnit.DAYS)
                    .setTimeRange(startDate.time, endDate.time, TimeUnit.MILLISECONDS)
                    .build()


            GoogleSignIn.getLastSignedInAccount(context)?.let { googleSignInAccount ->

                Fitness.getHistoryClient(context, googleSignInAccount)
                        .readData(readRequest)
                        .addOnSuccessListener { dataReadResponse ->

                            TrackingHelper.trackerConnectionIsValid()
                            callback?.onTrackerLoginValid(TrackingHelper.GOOGLE_FIT_TRACKING_SOURCE_ID)

                        }
                        .addOnFailureListener { exception ->
                            Log.e(TAG, "GoogleFit steps error:", exception);

                            Log.d(TAG, "Error: " + exception.message)
                            callback?.onTrackerLoginVerifyError()

                        }
            }
        }

        /**
         * Gets a Google account for use in creating the Fitness client. This is achieved by either
         * using the last signed-in account, or if necessary, prompting the user to sign in.
         * `getAccountForExtension` is recommended over `getLastSignedInAccount` as the latter can
         * return `null` if there has been no sign in before.
         */
        fun getGoogleAccount(context: Context) = GoogleSignIn.getAccountForExtension(context, googleFitFitnessDataOptions)

        /** Records step data by requesting a subscription to background step data.  */
        fun subscribeToRecordSteps(context: Context, callback: GoogleFitSubscriptionCallback) {
            // To create a subscription, invoke the Recording API. As soon as the subscription is
            // active, fitness data will start recording.
            getGoogleAccount(context)?.let { googleSignInAccount ->
                Fitness.getRecordingClient(context, googleSignInAccount)
                        .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "Successfully subscribed!")
                                callback.onSubscriptionSuccess()
                            } else {
                                Log.w(TAG, "There was a problem subscribing.", task.exception)
                                callback.onSubscriptionError(task.exception)
                            }
                        }
            }
        }
    }

    private val runningQOrLater =
            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q

    private fun permissionApproved(context: Context): Boolean {
        val approved = if (runningQOrLater) {
            PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACTIVITY_RECOGNITION)
        } else {
            true
        }
        return approved
    }

}
