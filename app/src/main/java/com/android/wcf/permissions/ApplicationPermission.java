package com.android.wcf.permissions;

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
