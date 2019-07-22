package com.android.wcf.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.wcf.R;
import com.android.wcf.fitbit.FitbitHelper;
import com.android.wcf.googlefit.GoogleFitHelper;
import com.android.wcf.settings.FitnessTrackerConnectionMvp;
import com.fitbitsdk.authentication.AuthenticationConfiguration;
import com.fitbitsdk.authentication.AuthenticationHandler;
import com.fitbitsdk.authentication.AuthenticationManager;
import com.fitbitsdk.authentication.AuthenticationResult;
import com.fitbitsdk.authentication.LogoutTaskCompletionHandler;
import com.fitbitsdk.authentication.Scope;
import com.fitbitsdk.service.FitbitService;
import com.fitbitsdk.service.models.Device;
import com.fitbitsdk.service.models.User;
import com.fitbitsdk.service.models.UserProfile;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.fitness.ConfigClient;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class BaseActivity extends AppCompatActivity
        implements BaseMvp.BaseView, FitnessTrackerConnectionMvp.Host, AuthenticationHandler {

    protected SharedPreferences sharedPreferences = null;

    protected static final String TAG = BaseActivity.class.getSimpleName();

    private LogoutTaskCompletionHandler fitbitLogoutTaskCompletionHandler = new LogoutTaskCompletionHandler() {
        @Override
        public void logoutSuccess() {
            Log.i(TAG, "LogoutTaskCompletionHandler: logoutSuccess");
            onFitbitLogoutSuccess();
        }

        @Override
        public void logoutError(String message) {
            Log.i(TAG, "LogoutTaskCompletionHandler: logoutError");
            onFitbitLogoutError(message);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(FitbitHelper.FITBIT_SHARED_PREF_NAME, Context.MODE_PRIVATE);

        AuthenticationConfiguration fitbitAuthenticationConfiguration =
                FitbitHelper.generateAuthenticationConfiguration(this, null);
        AuthenticationManager.configure(this, fitbitAuthenticationConfiguration);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case AuthenticationManager.FITBIT_LOGIN_REQUEST_CODE:
                if (!AuthenticationManager.onActivityResult(requestCode, resultCode, data, this)) {
                    // Handle other activity results, if needed
                }
                break;

            case RequestCodes.GFIT_PERMISSIONS_REQUEST_CODE: {
                //TODO: make a GoogleFitAuthManager similar to Fitbit's
                onActivityResultForGoogleFit(requestCode, resultCode, data);

            }
            default:
                Log.e(TAG, "Unhandled Request Code " + requestCode);
        }
    }

    @Override
    public void setToolbarTitle(@NotNull String title) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(title);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    //TODO: implement the proper dialogFragment for showing error messages

    @Override
    public void showError(int messageId) {
        Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showError(Throwable error) {
        showMessage(error.getMessage());
    }

    @Override
    public void showError(String message) {
        showMessage(message);
    }

    @Override
    public void showError(String title, String message) {
        showMessage(message);
    }

    @Override
    public void showError(String title, int messageId) {
        Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showError(int titleId, String message) {
        showMessage(message);
    }

    @Override
    public void showError(int titleId, int messageId) {
        Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoadingDialogFragment() {

    }

    @Override
    public void hideLoadingDialogFragment() {

    }

    @Override
    public View showLoadingView() {
        return null;
    }

    @Override
    public void hideLoadingView() {

    }


    @Override
    public void closeKeyboard() {
        View view = view = findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getRootView().getWindowToken(), 0);
        }
    }

    /************* Fitbit related methods   ****************/

    @Override
    public void connectAppToFitbit() {
        Log.i(TAG, "connectAppToFitbit");
        AuthenticationManager.login(this);
    }

    @Override
    public void disconnectAppFromFitbit() {
        Log.i(TAG, "disconnectAppFromFitbit");
        AuthenticationManager.logout(this, fitbitLogoutTaskCompletionHandler);
    }

    @Override
    public void onAuthFinished(AuthenticationResult result) {
        Log.i(TAG, "onAuthFinished");
        if (result != null) {
            if (result.isSuccessful()) {
                onLoggedIn();
                sharedPreferences.edit().putBoolean(FitbitHelper.FITBIT_DEVICE_LOGGED_IN, true).commit();
            } else if (!result.isDismissed()) {
                displayAuthError(result);
                sharedPreferences.edit().putBoolean(FitbitHelper.FITBIT_DEVICE_LOGGED_IN, false).commit();
            }
        }
    }

    protected void onLoggedIn() {
        Log.i(TAG, "onLoggedIn");
        getUserProfile();
        getDeviceProfile();
    }

    private void getUserProfile() {
        Log.i(TAG, "getUserProfile");
        FitbitService fService = new FitbitService(sharedPreferences, AuthenticationManager.getAuthenticationConfiguration().getClientCredentials());
        fService.getUserService().profile().enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                String displayName = "";
                UserProfile userProfile = response.body();
                if (userProfile != null) {
                    User user = userProfile.getUser();
                    Log.i(TAG, "User Response: " +
                            user.getDisplayName() + " stride: " + user.getStrideLengthWalking() + " " + user.getDistanceUnit());

                    displayName = user.getDisplayName();
                }
                sharedPreferences.edit().putString(FitbitHelper.FITBIT_USER_DISPLAY_NAME, displayName).commit();
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
            }
        });
    }

    private void getDeviceProfile() {
        Log.i(TAG, "getDeviceProfile");
        final SharedPreferences sharedPreferences = getSharedPreferences(FitbitHelper.FITBIT_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        FitbitService fService = new FitbitService(sharedPreferences, AuthenticationManager.getAuthenticationConfiguration().getClientCredentials());
        fService.getDeviceService().devices().enqueue(new Callback<Device[]>() {
            @Override
            public void onResponse(Call<Device[]> call, Response<Device[]> response) {
                Device[] devices = response.body();

                StringBuilder stringBuilder = new StringBuilder();

                if (devices != null) {
                    for (Device device : devices) {

                        Log.i(TAG, "Device Response: " +
                                "Type: " + device.getType() + "\n" +
                                "version: " + device.getDeviceVersion() + "\n" +
                                "synched at: " + device.getLastSyncTime());

                        stringBuilder.append("<b>FITBIT ");
                        stringBuilder.append(device.getDeviceVersion().toUpperCase());
                        stringBuilder.append("&trade;</b><br>");

                        stringBuilder.append("<b>Last Sync: </b>");
                        stringBuilder.append(device.getLastSyncTime());
                        stringBuilder.append("<br><br>");
                    }

                    if (stringBuilder.length() > 0) {
                        stringBuilder.replace(stringBuilder.length() - 8, stringBuilder.length(), "");
                        Log.i(TAG, "Device info: " + stringBuilder);
                    }
                }
                sharedPreferences.edit().putString(FitbitHelper.FITBIT_DEVICE_INFO, stringBuilder.toString()).commit();
            }

            @Override
            public void onFailure(Call<Device[]> call, Throwable t) {
                Log.e(TAG, "Device api Error: " + t.getMessage());
            }
        });
    }

    private void displayAuthError(AuthenticationResult authenticationResult) {
        Log.i(TAG, "displayAuthError");
        String message = "";

        if (authenticationResult.getStatus() == AuthenticationResult.Status.dismissed) {
            return;
        } else if (authenticationResult.getStatus() == AuthenticationResult.Status.error) {
            if (authenticationResult.getErrorMessage().startsWith("net::ERR_INTERNET_DISCONNECTED")) {
                message = "Please check internet connection and try again";
            } else {
                message = authenticationResult.getErrorMessage();
            }
        } else if (authenticationResult.getStatus() == AuthenticationResult.Status.missing_required_scopes) {
            Set<Scope> missingScopes = authenticationResult.getMissingScopes();
            String missingScopesText = TextUtils.join(", ", missingScopes);
            message = missingScopesText + " " + getString(R.string.missing_scopes_error);
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.login_title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();
    }


    protected void onFitbitLogoutSuccess() {
        Log.i(TAG, "onFitbitLogoutSuccess");
        sharedPreferences.edit().putBoolean(FitbitHelper.FITBIT_DEVICE_LOGGED_IN, false).commit();
    }

    protected void onFitbitLogoutError(String message) {
        Log.i(TAG, "onFitbitLogoutError");
        sharedPreferences.edit().putBoolean(FitbitHelper.FITBIT_DEVICE_LOGGED_IN, false).commit();
    }

    /************* end of fitbit related methods ***************/

    /************* Google Fit related methods   ****************/
    @Override
    public void connectAppToGoogleFit() {
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .build();

        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this, // your activity
                    RequestCodes.GFIT_PERMISSIONS_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(this),
                    fitnessOptions);
        } else {
            onGoogleFitLoggedIn();
        }
    }

    protected void onActivityResultForGoogleFit(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResultForGoogleFit");
        if (resultCode == Activity.RESULT_OK) {
            sharedPreferences.edit().putBoolean(GoogleFitHelper.GOOGLE_FIT_APP_LOGGED_IN, true).commit();
            onGoogleFitLoggedIn();
        } else {
            sharedPreferences.edit().putBoolean(GoogleFitHelper.GOOGLE_FIT_APP_LOGGED_IN, false).commit();
        }
    }

    protected void onGoogleFitLoggedIn() {

    }


    public void disconnectAppFromGoogleFit() {
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);

        if (googleSignInAccount != null) {
            ConfigClient configClient = Fitness.getConfigClient(this, googleSignInAccount);
            if (configClient != null) {
                configClient.disableFit();
            }
        }
        sharedPreferences.edit().putBoolean(GoogleFitHelper.GOOGLE_FIT_APP_LOGGED_IN, false).commit();
        onGoogleFitLogoutSuccess();

//        FitnessOptions options =  FitnessOptions.builder()
//                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
//                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
//                .build();
//
//        GoogleSignInOptions signInOptions = null;
//
//       GoogleSignInClient client = GoogleSignIn.getClient(this, signInOptions);
//       client.revokeAccess();
//
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        if (account != null) {
//            Fitness.getConfigClient(this, account).disableFit();
//            sharedPreferences.edit().putBoolean(GoogleFitHelper.GOOGLE_FIT_APP_LOGGED_IN, false).commit();
//        }
//        onGoogleFitLogoutSuccess();
    }

    protected void onGoogleFitLogoutSuccess() {

    }

    protected void onGoogleFitLogoutError() {

    }

}
