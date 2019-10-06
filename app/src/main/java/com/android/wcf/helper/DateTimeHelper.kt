package com.android.wcf.helper


import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue


/**
 * Created by jwcona0510 on 6/16/17.
 */

const val ISO_1860_W_TIME_ZONE = "yyyy-MM-dd'T'HH:mm:ssZZZ"
const val MM_DD_HH_MM_SS_MMM = "MM-dd HH:mm:ss.SSS"


const val MILLIS_IN_HOUR: Int = 1000 * 60 * 60
const val MILLIS_IN_MINUTE: Int = 1000 * 60
const val MILLIS_IN_SECOND: Int = 1000

const val HOUR_INDX = 0
const val MIN_INDX = 1
const val SEC_INDX = 2
const val MILLI_INDX = 3

class DateTimeHelper {

    companion object {
        val DAY_IN_MS: Long = 1000 * 60 * 60 * 24

        @JvmStatic
        fun dateWeekAgo(): Date {
            return Date(Date().getTime() - (7 * DAY_IN_MS))
        }

        @JvmStatic
        fun getNowLong(): Long {
            return System.currentTimeMillis()
        }

        @JvmStatic
        fun intToUserReadableTime(i: Int): String {
            val times = generateTimeUnitsArray(i.toLong())
            return generateUiTimeString(times)
        }

        @JvmStatic
        fun intToReadableTime(i: Int): String {
            val times = generateTimeUnitsArray(i.toLong())
            return generateTimeString(times)
        }

        @JvmStatic
        fun longToReadableTime(l: Long): String {
            val times = generateTimeUnitsArray(l)
            return generateTimeString(times)
        }

        @JvmStatic
        fun generateTimeUnitsArray(l: Long): LongArray {
            val timeUnits = LongArray(4)
            timeUnits[HOUR_INDX] = l / MILLIS_IN_HOUR
            timeUnits[MIN_INDX] = (l % MILLIS_IN_HOUR) / MILLIS_IN_MINUTE
            timeUnits[SEC_INDX] = (l % MILLIS_IN_MINUTE) / MILLIS_IN_SECOND
            timeUnits[MILLI_INDX] = l % MILLIS_IN_SECOND

            return timeUnits
        }

        @JvmStatic
        fun generateTimeString(timeUnits: LongArray): String {
            val hours = timeUnits[HOUR_INDX].absoluteValue
            val minutes = timeUnits[MIN_INDX].absoluteValue
            val seconds = timeUnits[SEC_INDX].absoluteValue
            var millis = timeUnits[MILLI_INDX]

            val sb = StringBuilder(if (millis < 0L) {
                millis = millis.absoluteValue; "-"
            } else "")
            sb.append(if (hours < 10) "0" + hours.toString() else hours.toString())
                    .append(":")
                    .append(if (minutes < 10) "0" + minutes.toString() else minutes.toString())
                    .append(":")
                    .append(if (seconds < 10) "0" + seconds.toString() else seconds.toString())
                    .append(".")
                    .append(when {
                        (millis < 10) -> "00" + millis.toString()
                        (millis < 100) -> "0" + millis.toString()
                        else -> millis.toString()
                    })

            return sb.toString()
        }

        @JvmStatic
        fun generateUiTimeString(timeUnits: LongArray): String {
            val hours = timeUnits[HOUR_INDX]
            val minutes = timeUnits[MIN_INDX]
            val seconds = timeUnits[SEC_INDX]

            val sb = StringBuilder("")
            val hasHours = hours > 0
            val hasMinutes = minutes > 0
            val hasSeconds = seconds >= 0
            if (hasHours) {
                sb.append(hours.toString())
                        .append(":")
            }
            if (hasMinutes) {
                sb.append(if (minutes in 1..9) "0" + minutes.toString() else minutes.toString())
                        .append(":")
            } else {
                sb.append("00").append(":")
            }

            sb.append(if (seconds < 10) "0" + seconds.toString() else seconds.toString())

            return sb.toString()
        }

        @JvmStatic
        fun obtainDateForNow(): String = longToReadableDate(System.currentTimeMillis())

        @JvmStatic
        fun getDateForNow(): Date {
            val cal: Calendar = Calendar.getInstance()
            cal.timeInMillis = System.currentTimeMillis()
            return cal.time
        }

        @JvmStatic
        fun yesterday(): Date {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DATE, -1)
            return cal.time
        }

        @JvmStatic
        fun longToReadableDate(l: Long): String {
            val cal: Calendar = Calendar.getInstance()
            cal.timeInMillis = l
            val date: Date = cal.time
            val dateForm: DateFormat = SimpleDateFormat.getDateTimeInstance()
            return dateForm.format(date)
        }

        @JvmStatic
        fun obtainISO8601DateForNow(): String = longToISO8601(System.currentTimeMillis())

        @JvmStatic
        fun obtainobtainISO8601Date(l: Long) = longToISO8601(l)

        @JvmStatic
        fun longToISO8601(l: Long): String {
            val cal = Calendar.getInstance()
            cal.timeInMillis = l
            val date: Date = cal.time
            val dateFormat = SimpleDateFormat(ISO_1860_W_TIME_ZONE)
            return dateFormat.format(date)
        }

        @JvmStatic
        fun obtainDebuggingDate(l: Long): String {
            val cal = Calendar.getInstance()
            cal.timeInMillis = l
            val date: Date = cal.time
            val dateFormat = SimpleDateFormat(MM_DD_HH_MM_SS_MMM)
            return dateFormat.format(date)
        }

        @JvmStatic
        fun obtainDebuggingDateForNow() = obtainDebuggingDate(System.currentTimeMillis())


        @JvmStatic
        fun calcTimeDiffFromNow(eventTime: Long): Long {
            return System.currentTimeMillis() - eventTime
        }

        @JvmStatic
        fun getUtcOffetForlocal(): Int {
            return TimeZone.getDefault().getOffset(System.currentTimeMillis())
        }

    }
}