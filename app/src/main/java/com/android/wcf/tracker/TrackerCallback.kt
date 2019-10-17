package com.android.wcf.tracker

import com.fitbitsdk.service.models.ActivitySteps
import com.fitbitsdk.service.models.DailyActivitySummary


interface TrackerStepsCallback {
    fun onTrackerStepsRetrieved(data: ActivitySteps)

    fun onTrackerStepsError(t: Throwable)

    fun trackerNeedsReLogin(trackerSourceId: Int)
}

//interface FitbitActivityResponseCallback {
//    fun onFitbitActivityRetrieved(data: DailyActivitySummary)
//
//    fun onFitbitActivityError(t: Throwable)
//}

interface TrackerLoginStatusCallback {
    fun onTrackerLoginValid(trackerId:Int)

    fun onTrackerLoginVerifyError()

    fun trackerNeedsReLogin(trackerSourceId: Int)
}
