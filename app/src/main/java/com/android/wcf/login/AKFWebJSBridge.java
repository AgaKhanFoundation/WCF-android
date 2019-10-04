package com.android.wcf.login;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class AKFWebJSBridge {

    private static final String TAG = AKFWebJSBridge.class.getSimpleName();

    Context mContext;

    /**
     * Instantiate the interface and set the context
     */
   public AKFWebJSBridge(Context context) {
        mContext = context;
    }

    /**
     * Show a toast from the web page
     */
    @JavascriptInterface
    public void profileCreated() {
        String message = "Callback received, AKF Profile created";
        Log.d(TAG, message);
        // Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }
}
