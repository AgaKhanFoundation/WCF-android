package com.android.wcf.login;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class AKFWebJSBridge {


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
        Toast.makeText(mContext, "Profile created", Toast.LENGTH_SHORT).show();
    }
}
