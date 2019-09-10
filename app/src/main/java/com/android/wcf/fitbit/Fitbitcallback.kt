package com.android.wcf.fitbit

import com.fitbitsdk.service.models.ActivitySteps
import com.fitbitsdk.service.models.DailyActivitySummary


interface FitbitStepsResponseCallback {
    fun onFitbitStepsRetrieved(data: ActivitySteps)

    fun onFitbitStepsError(t: Throwable)
}

interface FitbitActivityResponseCallback {
    fun onFitbitActivityRetrieved(data: DailyActivitySummary)

    fun onFitbitActivityError(t: Throwable)
}