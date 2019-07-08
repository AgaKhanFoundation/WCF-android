package com.fitbitsdk.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.fitbitsdk.service.models.auth.OAuthAccessToken

class AccessTokenReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

    }

    private fun saveAccessToken(accessToken: OAuthAccessToken){

    }
}