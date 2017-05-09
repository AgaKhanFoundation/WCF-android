package com.android.wcf.permissions;
/**
 * Copyright © 2017 Aga Khan Foundation
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import com.android.wcf.utils.Build;
import com.android.wcf.utils.Debug;

import java.util.ArrayList;
import java.util.List;

public class ApplicationPermission {

    private static final String TAG = ApplicationPermission.class.getSimpleName();
    private final boolean DEBUG = Build.PERMISSION_DEBUG;
    private Context mContext;
    private List<String> mPermissionList;
    private List<String> mPermissionNeededList;
    private String[] permissions;

    public ApplicationPermission(Context context) {
        mContext = context;
    }

    public List<String> getPermissionList() {
        return mPermissionList;
    }

    public void setPermissionList(List<String> mPermissionList) {
        this.mPermissionList = mPermissionList;
    }

    public boolean checkAllPermission() {
        Debug.info(DEBUG, TAG, "checkAllPermission() :: ");
        boolean isAllPermissionGranted = true;
        mPermissionNeededList = new ArrayList<>();
        for (int i = 0; i < mPermissionList.size(); i++) {
            String permission = mPermissionList.get(i);
            int result = ContextCompat.checkSelfPermission(mContext, permission);
            Debug.info(DEBUG, TAG, "checkAllPermission() ::  permission == " + permission + " result == " + result);
            if (result != PackageManager.PERMISSION_GRANTED) {
                isAllPermissionGranted = false;
                mPermissionNeededList.add(permission);
            }

            Debug.info(DEBUG, TAG, "checkAllPermission() :: Permission Denied.");
        }
        return isAllPermissionGranted;
    }

    public String[] requestPermission(List<String> permissionList) {
        if (permissionList == null) {
            if (mPermissionNeededList != null && !mPermissionNeededList.isEmpty()) {
                permissions = mPermissionNeededList.toArray(new String[mPermissionNeededList.size()]);
                return permissions;
            }
        } else {
            if (permissionList != null && !permissionList.isEmpty()) {
                permissions = permissionList.toArray(new String[permissionList.size()]);
                return permissions;
            }
        }
        return null;
    }

    public List<String> getRequiredPermissionList(String permissions[], int[] grantResults) {
        ArrayList<String> reqPermissionList = new ArrayList<String>();
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                Debug.info(DEBUG, "Application Permission", permissions[i] + " Permission not granted.");
                reqPermissionList.add(permissions[i]);
            }
        }
        return reqPermissionList;
    }

    public List<String> checkNeverAskAgain(String permissions[], int[] grantResults) {
        ArrayList<String> reqPermissionList = new ArrayList<String>();
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                reqPermissionList.add(permissions[i]);
            }
        }
        return reqPermissionList;
    }

}
