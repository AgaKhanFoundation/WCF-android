package com.android.wcf.helper

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.android.wcf.application.WCFApplication
import com.android.wcf.network.Steps4ChangeEnv

class ManifestHelper {

    companion object {
        private var prod_serverPassword: String? = null
        private var stage_serverPassword: String? = null
        private var dev_serverPassword: String? = null

        fun getServerPassword(env: Steps4ChangeEnv): String {
            when (env) {
                Steps4ChangeEnv.DEV -> {
                    if (dev_serverPassword == null) {
                        dev_serverPassword = getManifestProp("dev_wcb_server_password")
                    }
                    return dev_serverPassword ?: ""
                }
                Steps4ChangeEnv.STAGE -> {
                    if (stage_serverPassword == null) {
                        stage_serverPassword = getManifestProp("stage_wcb_server_password")
                    }
                    return stage_serverPassword ?: ""
                }
                Steps4ChangeEnv.PROD -> {
                    if (prod_serverPassword == null) {
                        prod_serverPassword = getManifestProp("prod_wcb_server_password")
                    }
                    return prod_serverPassword ?: ""
                }
                else ->
                    return ""
            }
        }

        fun getManifestProp(propName: String): String {
            var applicationInfoMetaData: ApplicationInfo?
            try {
                val context = WCFApplication.instance
                applicationInfoMetaData = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
                val bundle = applicationInfoMetaData!!.metaData
                val value = bundle.getString(propName)
                return value ?: ""

            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            return ""
        }
    }
}