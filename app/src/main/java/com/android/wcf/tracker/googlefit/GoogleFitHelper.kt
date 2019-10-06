package com.android.wcf.tracker.googlefit

import android.content.Context
import android.util.Log
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


class GoogleFitHelper {
    companion object {

        val TAG: String = "GoogleFitHelper"

        val googleFitFitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .build()

        val googleFitSignInOptions = GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .addExtension(googleFitFitnessOptions).build()

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
                                callback?.trackerNeedsRelogin(TrackingHelper.GOOGLE_FIT_TRACKING_SOURCE_ID)
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

                            callback?.onTrackerLoginValid(TrackingHelper.GOOGLE_FIT_TRACKING_SOURCE_ID)

                        }
                        .addOnFailureListener { exception ->
                            Log.e(TAG, "GoogleFit steps error:", exception);

                            Log.d(TAG, "Error: " + exception.message)
                            callback?.onTrackerLoginNotValid()

                        }
            }
        }
    }
}
//
//        fun getSteps2(context: Context, startDate: Date, endDate: Date, callback: TrackerStepsCallback?) {
//
//            GoogleSignIn.getLastSignedInAccount(context)?.let {googleAccount ->
//
//               val fitApiClient:GoogleApiClient = Fitness.getHistoryClient(context,googleAccount) as GoogleApiClient
//
//            return Observable.create(object : Observable.OnSubscribe<DataReadResult>() {
//                fun call(subscriber: Subscriber<in DataReadResult>) {
//                    val result = Fitness.HistoryApi.readData(fitApiClient, DataReadRequest.Builder()
//                            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
//                            .bucketByTime(1, TimeUnit.DAYS)
//                            .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
//                            .build()).await(30, TimeUnit.SECONDS)
//
//                    if (result.status.isSuccess) {
//                        subscriber.onNext(result)
//                        subscriber.onCompleted()
//                    } else {
//                        subscriber.onError(FitApiException(result.status))
//                    }
//                }
//            })

//+++++++++++

//                val response = Fitness.getHistoryClient(context, googleSignInAccount)
//                        .readData(readRequest)
//


//TODO: try this, looks like it waits for results to come
//from: /Users/sultan/Documents/projects/samples/sample_android/sensors/Corey/app/src/main/java/at/shockbytes/corey/data/body/GoogleFitBodyRepository.kt
//        GoogleSignInOptionsExtension fitnessOptions =
//                FitnessOptions.builder()
//                        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
//                        .build();
//        GoogleSignInAccount gsa = GoogleSignIn.getAccountForExtension(this, fitnessOptions);
//
//        Date now = new Date();
//        Task<DataReadResponse> response = Fitness.getHistoryClient(this, gsa)
//                .readData(new DataReadRequest.Builder()
//                        .read(DataType.TYPE_STEP_COUNT_DELTA)
//                        .setTimeRange(now.getTime() - 7*24*60*60*1000, now.getTime(), TimeUnit.MILLISECONDS)
//                        .build());
//
//        DataReadResult readDataResult = Tasks.await(response);
//        DataSet dataSet = readDataResult.getDataSet(DataType.TYPE_STEP_COUNT_DELTA);

//            val readRequest = DataReadRequest.Builder()
//                    .read(DataType.TYPE_STEP_COUNT_DELTA)
//                    .setTimeRange(startDate.time, endDate.time, TimeUnit.MILLISECONDS)
//                    .build()


//                    .addOnCompleteListener {
//                        override fun onComplete(task: Task<*>) {
//                            val buckets = (task.result as DataReadResponse).buckets
//
//                            var totalSteps = 0
//
//                            for (bucket in buckets) {
//
//                                val dataSets = bucket.dataSets
//
//                                for (dataSet in dataSets) {
//                                    //                                if (dataSet.getDataType() == DataType.TYPE_STEP_COUNT_DELTA) {
//
//
//                                    for (dp in dataSet.dataPoints) {
//                                        for (field in dp.dataType.fields) {
//                                            val steps = dp.getValue(field).asInt()
//                                            totalSteps += steps
//                                        }
//                                    }
//                                }
//                                //                            }
//                            }
//                            Log.i(TAG, "total steps for this week: $totalSteps")
//                        }
//                    }


