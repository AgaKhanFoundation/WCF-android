package com.android.wcf.base;

import com.fitbitsdk.authentication.AuthenticationManager;

public class RequestCodes {

    // Fitbit requestCode here is just for tracking. AuthenticationManager uses' its variable for requests
    public static final int FITBIT_LOGIN_REQUEST_CODE = AuthenticationManager.FITBIT_LOGIN_REQUEST_CODE;

    public static final int GFIT_PERMISSIONS_REQUEST_CODE = 1001;

}
