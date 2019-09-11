package com.android.wcf.login

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.webkit.HttpAuthHandler
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.android.wcf.R
import com.android.wcf.web.WebViewFragment

class AKFParticipantProfileFragment : WebViewFragment(), AKFParticipantProfileMvp.View {
    val AKF_PROFILE_URL = "https://www.akfusa.org/steps4impact/"

    var host: AKFParticipantProfileMvp.Host? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AKFParticipantProfileMvp.Host) {
            host = context
        } else {
            throw RuntimeException("$context must implement AKFParticipantProfileMvp.Host")
        }
    }

    override fun onDetach() {
        super.onDetach()
        host = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        host?.setToolbarTitle(null, true)
        host?.showToolbar()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var handled = super.onOptionsItemSelected(item)
        if (!handled) {
            when (item!!.itemId) {
                android.R.id.home -> {
                    closeView()
                    handled = true
                }
                else -> {
                }
            }
        }
        return handled
    }

     fun closeView() {
       host?.akfProfileCreationComplete()
    }

    override fun setupWebView(): String? {
        val extras = arguments

        var title: String? = null
        var url = AKF_PROFILE_URL
        if (extras != null) {
            url = extras.getString(WEB_URL_KEY, AKF_PROFILE_URL)
            title = extras.getString(TITLE_OVERRIDE_KEY, null)
        }

        if (title != null) {
            host?.setToolbarTitle(title, true)
            mWebView?.setWebChromeClient(WebChromeClient())
        } else {
            mWebView?.setWebChromeClient(object : WebChromeClient() {
                override fun onReceivedTitle(view: WebView, title: String?) {
                    super.onReceivedTitle(view, title)
                    if (title != null && title.length > 0) {
                        host?.setToolbarTitle(null, true)

                    } else {
                        host?.setToolbarTitle(null, true)
                    }
                }
            })
        }

        if (extras != null && extras.containsKey(KEY_URL_USER_NAME) && extras.containsKey(KEY_URL_PWD)) {
            mWebView?.setWebViewClient(object : WebViewClient() {
                override fun onReceivedHttpAuthRequest(view: WebView,
                                                       handler: HttpAuthHandler,
                                                       host: String, realm: String) {
                    val userName = extras.getString(KEY_URL_USER_NAME, "")
                    val pwd = extras.getString(KEY_URL_PWD, "")
                    handler.proceed(userName, pwd)
                }
            })
        }
        return url
    }
}