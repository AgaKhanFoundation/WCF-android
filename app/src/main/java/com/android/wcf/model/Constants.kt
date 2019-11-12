package com.android.wcf.model

class Constants {
    companion object {

        const val SPLASH_TIMER = 3000
        const val MIN_TEAM_NAME_CHAR_LENGTH = 6

        const val AUTH_FACEBOOK:String = "Facebook"

        const val SORT_MODE_ASCENDING = 0
        const val SORT_MODE_DESCENDING = 1

        const val LOGIN_CHECK_DELTA_HOURS = 8 //check device and facebook login frequently for token expiry
        const val TRACKER_CHECK_DELTA_HOURS = 4 //check frequently for token expiry. Fitbit access token expires in 8 hours

        const val SAVE_STEPS_TO_SERVER_DELTA_MIN = 30 //optimize

        @JvmStatic
        var featureFundraising = false //Hide fundraising until AKF backend integration is complete

        @JvmStatic
        var challengeStartSoonMessage = false

        @JvmStatic
        var bypassAKFProfile = false  //To  bypass AKF profile creation flow

        var BADGE_CALCULATION_DELTA_MIN = 60 //optimize badge calculations in a session

    }
}
