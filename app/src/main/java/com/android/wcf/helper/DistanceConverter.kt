package com.android.wcf.helper

import android.util.Log

enum class DistanceMetric(val steps: Int) {
    MILES(2000)
}

class DistanceConverter {

    companion object {
        private val TAG = DistanceConverter::class.java.simpleName

        var participantDistanceMetrics: DistanceMetric = DistanceMetric.MILES

        @JvmStatic
        fun setDefaultMetrics(metrics: DistanceMetric){
            participantDistanceMetrics = metrics;
        }

        @JvmStatic
        fun steps(distance: Int): Int {
            return steps(distance * 1.0, participantDistanceMetrics)
        }

        @JvmStatic
        fun distance(steps: Int): Long {
            return distance(steps, participantDistanceMetrics)
        }

        @JvmStatic
        fun steps(distance: Double, sourceMetrics: DistanceMetric): Int {
            when (sourceMetrics) {
                DistanceMetric.MILES ->
                    return (distance * DistanceMetric.MILES.steps).toInt()
                else -> {
                    Log.w(TAG, "Unsupported ${sourceMetrics.name}")
                    return 0
                }
            }
        }

        @JvmStatic
        fun steps(distance: Int, sourceMetrics: DistanceMetric): Int {
            return steps(distance * 1.0, sourceMetrics)
        }

        @JvmStatic
        fun distance(steps: Int, metrics: DistanceMetric): Long {
            when (metrics) {
                DistanceMetric.MILES ->
                    return Math.round(steps / (DistanceMetric.MILES.steps * 1.0))
                else -> {
                    Log.w(TAG, "Unsupported ${metrics.name}")
                    return 0L
                }
            }
        }
    }
}