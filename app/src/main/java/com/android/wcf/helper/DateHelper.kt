package com.android.wcf.helper

import java.util.*

object DateHelper {

    fun getUtcOffetForlocal():Int {
        return TimeZone.getDefault().getOffset(System.currentTimeMillis())
    }

}