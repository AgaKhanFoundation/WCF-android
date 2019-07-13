package com.android.wcf.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.android.wcf.R;
import com.android.wcf.fitbit.FitbitHelper;
import com.android.wcf.fitbit.WCFFitbitLoginActivity;
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

import org.jetbrains.annotations.NotNull;

import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity implements SettingsMvp.Host, DeviceConnectionMvp.Host, AuthenticationHandler {

    private Toolbar toolbar;
    SharedPreferences sharedPreferences = null;

    private static final String TAG = SettingsActivity.class.getSimpleName();

    private LogoutTaskCompletionHandler logoutTaskCompletionHandler = new LogoutTaskCompletionHandler() {
        @Override
        public void logoutSuccess() {
            sharedPreferences.edit().putBoolean(FitbitHelper.FITBIT_DEVICE_LOGGED_IN, false).commit();
        }

        @Override
        public void logoutError(String message) {
            sharedPreferences.edit().putBoolean(FitbitHelper.FITBIT_DEVICE_LOGGED_IN, false).commit();
        }
    };

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sharedPreferences = getSharedPreferences(FitbitHelper.FITBIT_SHARED_PREF_NAME, Context.MODE_PRIVATE);

        AuthenticationConfiguration authenticationConfiguration =
                FitbitHelper.generateAuthenticationConfiguration(this, WCFFitbitLoginActivity.class);
        AuthenticationManager.configure(this, authenticationConfiguration);

       // FitbitHelper.authenticationConfiguration = authenticationConfiguration;

        setupView();

        showSettingsConfiguration();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (!AuthenticationManager.onActivityResult(requestCode, resultCode, data, this)) {
            // Handle other activity results, if needed

/*
                if(result == AuthenticationResult.Status.dismissed) {

                    displayAuthError(result);
                }
                if (requestCode == 1) {
                    if (resultCode == Activity.RESULT_OK) {

                    }
                }

 */
        }
    }

    private void showSettingsConfiguration() {
        Fragment fragment = new SettingsFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public void showDeviceConnection() {
        Fragment fragment = new DeviceConnectionFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void setupView() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void setToolbarTitle(@NotNull String title) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(title);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void connectAppToFitbit() {
        AuthenticationManager.login(SettingsActivity.this);
    }

    @Override
    public void disconnectAppFromFitbit() {
        AuthenticationManager.logout(SettingsActivity.this, logoutTaskCompletionHandler);
    }

    @Override
    public void onAuthFinished(AuthenticationResult result) {
        if (result != null) {
            if (result.isSuccessful()) {
                onLoggedIn();
                sharedPreferences.edit().putBoolean(FitbitHelper.FITBIT_DEVICE_LOGGED_IN, true).commit();
            } else {
                displayAuthError(result);
                sharedPreferences.edit().putBoolean(FitbitHelper.FITBIT_DEVICE_LOGGED_IN, false).commit();
            }
        }
    }

    protected void onLoggedIn() {
        getUserProfile();
        getDeviceProfile();
    }


    private void getUserProfile() {
        FitbitService fService = new FitbitService(sharedPreferences, AuthenticationManager.getAuthenticationConfiguration().getClientCredentials());
        fService.getUserService().profile().enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                UserProfile userProfile = response.body();
                User user = userProfile.getUser();
                Log.d(TAG, "User Response: " +
                        user.getDisplayName() + " stride: " + user.getStrideLengthWalking() + " " + user.getDistanceUnit());

                sharedPreferences.edit().putString(FitbitHelper.FITBIT_USER_DISPLAY_NAME, user.getDisplayName()).commit();
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
            }
        });
    }

    private void getDeviceProfile() {
        final SharedPreferences sharedPreferences = getSharedPreferences(FitbitHelper.FITBIT_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        FitbitService fService = new FitbitService(sharedPreferences, AuthenticationManager.getAuthenticationConfiguration().getClientCredentials());
        fService.getDeviceService().devices().enqueue(new Callback<Device[]>() {
            @Override
            public void onResponse(Call<Device[]> call, Response<Device[]> response) {
                Device[] devices = response.body();

                StringBuilder stringBuilder = new StringBuilder();

                for (Device device : devices) {

                    Log.d(TAG, "Device Response: " +
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
                    Log.d(TAG, "Device info: " + stringBuilder);
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
        String message = "";

        if (authenticationResult.getStatus() == AuthenticationResult.Status.dismissed)
            message = getString(R.string.login_dismissed);

        else if (authenticationResult.getStatus() == AuthenticationResult.Status.error)
            message = authenticationResult.getErrorMessage();

        else if (authenticationResult.getStatus() == AuthenticationResult.Status.missing_required_scopes) {
            Set<Scope> missingScopes = authenticationResult.getMissingScopes();
            String missingScopesText = TextUtils.join(", ", missingScopes);
            message = getString(R.string.missing_scopes_error) + missingScopesText;
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
}
