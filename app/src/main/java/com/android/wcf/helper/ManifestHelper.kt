package com.android.wcf.helper

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.android.wcf.application.WCFApplication
import com.android.wcf.network.Steps4ChangeEnv
import com.android.wcf.network.WCFClient

class ManifestHelper {

    companion object {
        private var prod_serverPassword: String? = null
        private var stage_serverPassword: String? = null
        private var dev_serverPassword: String? = null

        private var prod_wcbImageFolder: String? = null
        private var stage_wcbImageFolder: String? = null
        private var dev_wcbImageFolder: String? = null
        private var wcbImageServerUrl:String? = null
        private var wcbImageServerConnectionString:String? = null
        private var wcbImageServerContainer:String? = null

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

        fun getWcbImageServerConnectionString():String {
            wcbImageServerConnectionString?.let {
                return it
            }
            val accname = getManifestProp("wcb_image_server_accname")
            val acckey = getManifestProp("wcb_image_server_acckey" )
            wcbImageServerConnectionString= "DefaultEndpointsProtocol=https;AccountName=${accname};AccountKey=${acckey.reversed()}"

            wcbImageServerConnectionString?.let {
                return it
            }
            return ""
        }

        fun getWcbImageServerUrl(): String {
            wcbImageServerUrl?.let {
                return  it
            }
            wcbImageServerUrl = getManifestProp("wcb_image_server_url")
            wcbImageServerUrl?.let {
                return  it
            }
            return ""
        }

        fun getWcbImageServerContainer(): String {
            wcbImageServerContainer?.let {
                return it
            }
            wcbImageServerContainer = getManifestProp("wcb_image_server_container")
            wcbImageServerContainer?.let {
                return it
            }
            return ""
        }

        fun getWcbImageFolderUrl(): String {
            val serverUrl= getWcbImageServerUrl()
            val containerName = getWcbImageServerContainer()
            val folder = getWcbImageFolder()
            return serverUrl + containerName + (if (folder.length > 0)  "/" + folder + "/"  else "/")
        }

        fun getWcbImageFolder(): String {
            val env: Steps4ChangeEnv = WCFClient.getServerEnv()
            when (env) {
                Steps4ChangeEnv.DEV -> {
                    if (dev_wcbImageFolder == null) {
                        dev_wcbImageFolder = getManifestProp("dev_wcb_image_folder")
                    }
                    return dev_wcbImageFolder ?: ""
                }
                Steps4ChangeEnv.STAGE -> {
                    if (stage_wcbImageFolder == null) {
                        stage_wcbImageFolder = getManifestProp("stage_wcb_image_folder")
                    }
                    return stage_wcbImageFolder ?: ""
                }
                Steps4ChangeEnv.PROD -> {
                    if (prod_wcbImageFolder == null) {
                        prod_wcbImageFolder = getManifestProp("prod_wcb_image_folder")
                    }
                    return prod_wcbImageFolder ?: ""
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