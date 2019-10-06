package com.android.wcf.model

class Constants {
    companion object {

        const val SPLASH_TIMER = 3000
        const val MIN_TEAM_NAME_CHAR_LENGTH = 6

        const val AUTH_FACEBOOK:String = "Facebook"

        const val SORT_MODE_ASCENDING = 0
        const val SORT_MODE_DESCENDING = 1

        const val PARTICIPANT_COMMITMENT_STEPS_PER_DAY_DEFAULT = 10000

        const val PARTICIPANT_COMMITMENT_MILES_DEFAULT = 350
        const val STEPS_IN_A_MILE = 2000
        const val PARTICIPANT_COMMITMENT_STEPS_DEFAULT = PARTICIPANT_COMMITMENT_MILES_DEFAULT * STEPS_IN_A_MILE

        const val LOGIN_CHECK_DELTA_HOURS = 8 //check device and facebook login frequently for token expiry
        const val TRACKER_CHECK_DELTA_HOURS = 8 //check device and facebook login frequently for token expiry

        const val SAVE_STEPS_TO_SERVER_DELTA_MIN = 30 //optimize
    }
}

