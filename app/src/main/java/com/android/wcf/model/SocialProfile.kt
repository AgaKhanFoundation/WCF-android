package com.android.wcf.model

data class SocialProfile(
        val displayName: String,
        val photoURL: String)

interface SocialProfileCallback {
    fun onParticipantProfileRetrieved(socialProfile: SocialProfile)
    fun onParticipantProfileError(error: Throwable)
}