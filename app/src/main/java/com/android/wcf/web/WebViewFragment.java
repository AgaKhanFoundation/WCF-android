package com.android.wcf.web;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.HttpAuthHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.wcf.R;
import com.android.wcf.base.BaseFragment;

public class WebViewFragment extends BaseFragment implements WebViewMvp.View {
    public static final String WEB_URL_KEY = "WebViewActivity.webUrl";
    public static final String TITLE_OVERRIDE_KEY = "WebViewActivity.titleOverride";
    private static final String DEFAULT_URL = null;

    public static final String KEY_URL_USER_NAME = "key_user_name";
    public static final String KEY_URL_PWD = "key_password";

    WebViewMvp.Host host;
    protected WebView mWebView;

    public static WebViewFragment getInstance(String url, String title) {
        WebViewFragment fragment = new WebViewFragment();
        if (url != null) {
            Bundle args = new Bundle();
            args.putString(WEB_URL_KEY, url);
            args.putString(TITLE_OVERRIDE_KEY, title);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof WebViewMvp.Host) {
            host = (WebViewMvp.Host) context;
        }
        else {
            throw new RuntimeException(context.toString()
                    + " must implement WebViewMvp.Host");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_web_view, container, false);
        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mWebView = view.findViewById(R.id.webView);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient());

        String url = setupWebView();

        mWebView.loadUrl(url);
    }

    protected String setupWebView() {
        final Bundle extras = getArguments();

        String title = null;
        String url = DEFAULT_URL;
        if (extras != null) {
            url = extras.getString(WEB_URL_KEY, DEFAULT_URL);
            title = extras.getString(TITLE_OVERRIDE_KEY, null);
        }

        if (title != null) {
           host.setToolbarTitle(title, false);
            mWebView.setWebChromeClient(new WebChromeClient());
        } else {
            mWebView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onReceivedTitle(WebView view, String title) {
                    super.onReceivedTitle(view, title);
                    if (title != null && title.length() > 0) {
                        host.setToolbarTitle(title, false);

                        host.setToolbarTitle(title, false);
                    } else {
                        host.setToolbarTitle(null, false);
                    }
                }
            });
        }

        if (extras != null && extras.containsKey(KEY_URL_USER_NAME) && extras.containsKey(KEY_URL_PWD)) {
            mWebView.setWebViewClient(new WebViewClient() {
                public void onReceivedHttpAuthRequest(WebView view,
                                                      HttpAuthHandler handler,
                                                      String host, String realm) {
                    String userName = extras.getString(KEY_URL_USER_NAME, "");
                    String pwd = extras.getString(KEY_URL_PWD, "");
                    handler.proceed(userName, pwd);
                }
            });
        }
        return url;
    }

    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mWebView.onResume();
    }

}
