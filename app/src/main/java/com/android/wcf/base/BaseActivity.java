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

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.wcf.R;
import com.android.wcf.application.DataHolder;
import com.android.wcf.tracker.TrackingHelper;
import com.android.wcf.tracker.fitbit.FitbitHelper;
import com.android.wcf.helper.SharedPreferencesUtil;
import com.android.wcf.model.Event;
import com.android.wcf.model.Participant;
import com.android.wcf.model.Team;
import com.android.wcf.settings.FitnessTrackerConnectionFragment;
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
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

abstract public class BaseActivity extends AppCompatActivity
        implements BaseMvp.BaseView, BaseMvp.Host, FitnessTrackerConnectionMvp.Host, AuthenticationHandler {

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

        sharedPreferences = getSharedPreferences(TrackingHelper.TRACKER_SHARED_PREF_NAME, Context.MODE_PRIVATE);

        AuthenticationConfiguration fitbitAuthenticationConfiguration =
                FitbitHelper.generateAuthenticationConfiguration(this, null);
        AuthenticationManager.configure(this, fitbitAuthenticationConfiguration);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final int unmaskedRequestCode = requestCode & 0x0000ffff;
        Log.i(TAG, "onActivityResult for " + unmaskedRequestCode);
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
    public void setToolbarTitle(@NotNull String title, boolean homeAffordance) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(title);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(homeAffordance);
    }

    @Override
    public void popBackStack(String tag) {
        FragmentManager fm = getSupportFragmentManager();
        // Pop off everything up to and including the current tab
        fm.popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void setEvent(Event event) {
        DataHolder.setEvent(event);
    }

    @Override
    public Event getEvent() {
        return DataHolder.getEvent();
    }

    @Override
    public void setTeamList(List<Team> teams) {
        DataHolder.setTeams(teams);
    }

    @Override
    public List<Team> getTeamList() {
        return DataHolder.getTeams();
    }

    @Override
    public void setParticipant(Participant participant) {
        DataHolder.setParticipant(participant);
        Event event = DataHolder.getEvent();
        if (event != null && participant.getCommitmentMiles() == 0) {
            participant.setCommitmentMiles(event.getDefaultParticipantCommitment());
            SharedPreferencesUtil.savetMyMilesCommitted(event.getDefaultParticipantCommitment());
        }
    }

    @Override
    public Participant getParticipant() {
        return DataHolder.getParticipant();
    }

    @Override
    public void setParticipantTeam(Team team) {
        DataHolder.setParticipantTeam(team);
    }

    @Override
    public Team getParticipantTeam() {
        return DataHolder.getParticipantTeam();
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

    @Override
    public void showDeviceConnection() {
        Fragment fragment = new FitnessTrackerConnectionFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
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
                onFitbitLoggedIn();
                sharedPreferences.edit().putBoolean(TrackingHelper.FITBIT_DEVICE_LOGGED_IN, true).commit();
            } else if (!result.isDismissed()) {
                displayAuthError(result);
                sharedPreferences.edit().putBoolean(TrackingHelper.FITBIT_DEVICE_LOGGED_IN, false).commit();
            }
        }
    }

    protected void onFitbitLoggedIn() {
        Log.i(TAG, "onFitbitLoggedIn");
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
                sharedPreferences.edit().putString(TrackingHelper.FITBIT_USER_DISPLAY_NAME, displayName).commit();
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
            }
        });
    }

    private void getDeviceProfile() {
        Log.i(TAG, "getDeviceProfile");
        final SharedPreferences sharedPreferences = getSharedPreferences(TrackingHelper.TRACKER_SHARED_PREF_NAME, Context.MODE_PRIVATE);
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
                sharedPreferences.edit().putString(TrackingHelper.FITBIT_DEVICE_INFO, stringBuilder.toString()).commit();
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
        sharedPreferences.edit().putBoolean(TrackingHelper.FITBIT_DEVICE_LOGGED_IN, false).commit();
    }

    protected void onFitbitLogoutError(String message) {
        Log.i(TAG, "onFitbitLogoutError");
        sharedPreferences.edit().putBoolean(TrackingHelper.FITBIT_DEVICE_LOGGED_IN, false).commit();
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
            sharedPreferences.edit().putBoolean(TrackingHelper.GOOGLE_FIT_APP_LOGGED_IN, true).commit();
            onGoogleFitLoggedIn();
        } else {
            sharedPreferences.edit().putBoolean(TrackingHelper.GOOGLE_FIT_APP_LOGGED_IN, false).commit();
        }
    }

    protected void onGoogleFitLoggedIn() {

        readStepsCountForWeek();
    }


    public void disconnectAppFromGoogleFit() {
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);

        if (googleSignInAccount != null) {
            ConfigClient configClient = Fitness.getConfigClient(this, googleSignInAccount);
            if (configClient != null) {
                configClient.disableFit();
            }
        }

        Fitness.getConfigClient(this, GoogleSignIn.getLastSignedInAccount(this)).disableFit();

        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .build();

        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder().addExtension(fitnessOptions).build();

        GoogleSignInClient client = GoogleSignIn.getClient(this, signInOptions);
        client.revokeAccess();

        sharedPreferences.edit().putBoolean(TrackingHelper.GOOGLE_FIT_APP_LOGGED_IN, false).commit();
        onGoogleFitLogoutSuccess();

    }

    protected void onGoogleFitLogoutSuccess() {

    }

    protected void onGoogleFitLogoutError() {

    }

    private void readStepsCountForWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        //TODO: try this, looks like it waits for results to come
//from: /Users/sultan/Documents/projects/samples/sample_android/sensors/Corey/app/src/main/java/at/shockbytes/corey/data/body/GoogleFitBodyRepository.kt
//        GoogleSignInOptionsExtension fitnessOptions =
//                FitnessOptions.builder()
//                        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
//                        .build();
//        GoogleSignInAccount gsa = GoogleSignIn.getAccountForExtension(this, fitnessOptions);
//
//        Date now = new Date();
//        Task<DataReadResponse> response = Fitness.getHistoryClient(this, gsa)
//                .readData(new DataReadRequest.Builder()
//                        .read(DataType.TYPE_STEP_COUNT_DELTA)
//                        .setTimeRange(now.getTime() - 7*24*60*60*1000, now.getTime(), TimeUnit.MILLISECONDS)
//                        .build());
//
//        DataReadResult readDataResult = Tasks.await(response);
//        DataSet dataSet = readDataResult.getDataSet(DataType.TYPE_STEP_COUNT_DELTA);


        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this)).readData(readRequest)
                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse dataReadResponse) {
                        List<Bucket> buckets = dataReadResponse.getBuckets();

                        int totalSteps = 0;

                        for (Bucket bucket : buckets) {

                            List<DataSet> dataSets = bucket.getDataSets();

                            for (DataSet dataSet : dataSets) {
//                                if (dataSet.getDataType() == DataType.TYPE_STEP_COUNT_DELTA) {


                                for (DataPoint dp : dataSet.getDataPoints()) {
                                    for (Field field : dp.getDataType().getFields()) {
                                        int steps = dp.getValue(field).asInt();
                                        totalSteps += steps;
                                    }
                                }
                            }
//                            }
                        }
                        Log.i(TAG, "total steps for this week: " + totalSteps);
                    }
                })
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        List<Bucket> buckets = ((DataReadResponse) task.getResult()).getBuckets();

                        int totalSteps = 0;

                        for (Bucket bucket : buckets) {

                            List<DataSet> dataSets = bucket.getDataSets();

                            for (DataSet dataSet : dataSets) {
//                                if (dataSet.getDataType() == DataType.TYPE_STEP_COUNT_DELTA) {


                                for (DataPoint dp : dataSet.getDataPoints()) {
                                    for (Field field : dp.getDataType().getFields()) {
                                        int steps = dp.getValue(field).asInt();
                                        totalSteps += steps;
                                    }
                                }
                            }
//                            }
                        }
                        Log.i(TAG, "total steps for this week: " + totalSteps);
                    }
                });




    }

}
