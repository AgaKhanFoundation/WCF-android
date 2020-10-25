package com.android.wcf.login

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.android.wcf.R
import com.android.wcf.base.BaseFragment
import com.android.wcf.helper.SharedPreferencesUtil
import com.android.wcf.web.AppWebViewClient
import com.fitbitsdk.authentication.UrlChangeHandler
import java.io.IOException

class AKFParticipantProfileFragment : BaseFragment(), AKFParticipantProfileMvp.View, UrlChangeHandler {

    var host: AKFParticipantProfileMvp.Host? = null
    var mWebView: WebView? = null
    lateinit var presenter: AKFParticipantProfileMvp.Presenter
    var fbid:String? = null

    companion object {
        val TAG = AKFParticipantProfileFragment::class.java.simpleName
        val AKF_PROFILE_URL = "https://www.akfusa.org/steps4impact/"
        val AKF_PROFILE_THANKYOU_URL = "https://www.akfusa.org/thank-you-steps4impact/"
    }

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
        presenter = AKFParticipantProfilePresenter(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_web_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        host?.setToolbarTitle(getString(R.string.akf_profileview_title), true)
        host?.showToolbar()
        var url = AKF_PROFILE_URL
        var fbid = SharedPreferencesUtil.getMyParticipantId();

        if (fbid == null || fbid.isEmpty()) {
            AlertDialog.Builder(context)
                    .setTitle(R.string.akf_profileview_title)
                    .setMessage(R.string.login_id_not_found)
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener { dialog, which -> relogin() })
                    .create()
                    .show()
        }

        this.fbid = fbid
        url += "?fbid=" + fbid

        mWebView?.loadUrl(url)
    }

    fun closeView() {
        host?.akfProfileCreationSkipped()
    }

    fun relogin() {
        host?.restartApp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var handled = super.onOptionsItemSelected(item)
        if (!handled) {
            when (item.itemId) {
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

    fun setupView() {
        mWebView = view?.findViewById(R.id.webView)
        val webSettings = mWebView?.getSettings()
        webSettings?.setJavaScriptEnabled(true)

        var webViewClient: WebViewClient = AppWebViewClient(this)

        mWebView?.setWebViewClient(webViewClient)

        mWebView?.addJavascriptInterface(AKFWebJSBridge(context), "androidAppJSReceiver")

    }

    override fun onUrlChanged(newUrl: String?): Boolean {
        Log.d(TAG, "onUrlChanged : $newUrl")
        newUrl?.let {
            val responseAsExpected: Boolean = it.startsWith(AKF_PROFILE_THANKYOU_URL, true);
            if (responseAsExpected) {
                Toast.makeText(context, "AKF Profile created", Toast.LENGTH_LONG).show()
                SharedPreferencesUtil.saveAkfProfileCreated(true);
                presenter.updateParticipantProfileRegistered(fbid!!)
            }
        }
        return true
    }

    override fun onLoadError(errorCode: Int, description: CharSequence?) {
        Toast.makeText(context, "Error loading AKF Profile creation page: $description", Toast.LENGTH_LONG).show()
    }

    override fun akfProfileRegistered() {
        host?.akfProfileCreationComplete()
    }

    override fun akfProfileRegistrationError(error: Throwable, participantId: String) {
        if (error is IOException) {
            showNetworkErrorMessage(R.string.data_error)
        }

        host?.akfProfileCreationComplete()

    }
}
