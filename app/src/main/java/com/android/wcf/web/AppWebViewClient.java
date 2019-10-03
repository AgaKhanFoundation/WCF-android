package com.android.wcf.web;

import android.annotation.TargetApi;
import android.os.Build;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.fitbitsdk.authentication.UrlChangeHandler;

public class AppWebViewClient extends WebViewClient {

    private UrlChangeHandler urlChangeHandler;

    public AppWebViewClient(UrlChangeHandler urlChangeHandler) {
        this.urlChangeHandler = urlChangeHandler;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return loadUrl(view, url);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return loadUrl(view, request.getUrl().toString());
    }

    private boolean loadUrl(WebView view, String url) {
        if (!urlChangeHandler.onUrlChanged(url)){
            view.loadUrl(url);
            return false;
        }
        return true;
    }


    @SuppressWarnings("deprecation")
    @Override
    public void onReceivedError(WebView view, final int errorCode,
                                final String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        urlChangeHandler.onLoadError(errorCode, description);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        urlChangeHandler.onLoadError(error.getErrorCode(), error.getDescription());
    }
}
