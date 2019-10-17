package com.android.wcf.network

import android.util.Base64
import com.android.wcf.helper.ManifestHelper

enum class Steps4ChangeEnv(val url:String) {
     DEV(WCFAuth.AKF_WCF_BACKEND_URL_DEV)
    ,STAGE(WCFAuth.AKF_WCF_BACKEND_URL_STAGING)
    ,PROD(WCFAuth.AKF_WCF_BACKEND_URL_PROD)
}

class WCFAuth {
    companion object {

        const val AKF_WCF_BACKEND_URL_PROD = "https://step4change.org"
        const val AKF_WCF_BACKEND_URL_DEV = "https://dev.step4change.org"
        const val AKF_WCF_BACKEND_URL_STAGING = "https://staging.step4change.org"

        private val AUTH_HEADER_KEY = "Authorization"

        @JvmStatic
        fun basicHeader(env: Steps4ChangeEnv): Pair<String, String> {
            var pwd =  ManifestHelper.getServerPassword(env)
            val basicToken = Base64.encode(":${pwd.reversed()}".toByteArray(), Base64.NO_WRAP)
            return AUTH_HEADER_KEY to "basic ${String(basicToken)}"
        }
    }
}
