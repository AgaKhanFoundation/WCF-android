package com.android.wcf.web;


public interface UrlChangeHandler {
    boolean onUrlChanged(String newUrl);
    void onLoadError(int errorCode, CharSequence description);
}
