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
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.wcf.R;
import com.android.wcf.application.DataHolder;
import com.android.wcf.facebook.FacebookHelper;
import com.android.wcf.helper.SharedPreferencesUtil;
import com.android.wcf.model.AuthSource;
import com.android.wcf.model.Event;
import com.android.wcf.model.Milestone;
import com.android.wcf.model.Participant;
import com.android.wcf.model.SocialProfile;
import com.android.wcf.model.Team;
import com.android.wcf.network.NetworkUtils;
import com.android.wcf.settings.FitnessTrackerConnectionFragment;
import com.android.wcf.settings.FitnessTrackerConnectionMvp;
import com.android.wcf.tracker.TrackingHelper;
import com.android.wcf.tracker.fitbit.FitbitHelper;
import com.android.wcf.tracker.googlefit.GoogleFitHelper;
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
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseActivity extends AppCompatActivity
        implements BaseMvp.BaseView, BaseMvp.Host, FitnessTrackerConnectionMvp.Host, AuthenticationHandler {

    private int progressViewCounter = 0;

    protected SharedPreferences trackersSharedPreferences = null;

    protected static String TAG = BaseActivity.class.getSimpleName();

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
        TAG = getClass().getSimpleName();
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        trackersSharedPreferences = getSharedPreferences(TrackingHelper.TRACKER_SHARED_PREF_NAME, Context.MODE_PRIVATE);

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

            case RequestCodes.LOGIN_REQUEST_GOOGLE_CODE:
                onActivityResultForGoogleLogin(requestCode, resultCode, data);
                break;

            default:
                Log.i(TAG, "Unhandled Request Code " + requestCode);

        }
    }
    @Override

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
    public boolean isNetworkConnected() {
        return NetworkUtils.isNetworkConnected(this);
    }


    @Override
    public void showNetworkErrorMessage(@StringRes int error_title_res_id) {
        showError(getString(error_title_res_id), getString(R.string.no_network_message), null);
    }

    @Override
    public void popBackStack(String tag) {
        FragmentManager fm = getSupportFragmentManager();
        // Pop off everything up to and including the current tab
        fm.popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void signout(boolean complete) {
        AuthSource authSource = AuthSource.valueOf(SharedPreferencesUtil.getMyAuthSource());
        if (authSource == AuthSource.Facebook){
            FacebookHelper.logout();
        }
        DataHolder.clearCache();
        if (complete) {
            SharedPreferencesUtil.clearAll();
        } else {
            SharedPreferencesUtil.clearMyLogin();
        }
        FirebaseAuth.getInstance().signOut();
    }

    @Override
    public void cacheEvent(Event event) {
        DataHolder.setEvent(event);
    }

    @Override
    public Event getEvent() {
        return DataHolder.getEvent();
    }

    @Override
    public void cacheTeamList(List<Team> teams) {
        DataHolder.setTeamsList(teams);
    }

    @Override
    public List<Team> getTeamList() {
        return DataHolder.getTeamsList();
    }

    @Override
    public void cacheParticipant(Participant participant) {
        DataHolder.setParticipant(participant);
        String savedParticipantId = SharedPreferencesUtil.getMyParticipantId();
        if (savedParticipantId.equals(participant.getFbId())){
            SharedPreferencesUtil.saveUserFullName(participant.getName());
            SharedPreferencesUtil.saveUserProfilePhotoUrl(participant.getParticipantProfile());
        }
    }

    @Override
    public Participant getParticipant() {
        return DataHolder.getParticipant();
    }

    @Override
    public void cacheParticipantTeam(Team team) {
        DataHolder.setParticipantTeam(team);
    }

    @Override
    public Team getParticipantTeam() {
        return DataHolder.getParticipantTeam();
    }

    @Override
    public void cacheMilestones(List<Milestone> journeyMilestones) {
        DataHolder.setMilestones(journeyMilestones);
    }

    @Override
    public List<Milestone> getMilestones() {
        return DataHolder.getMilestones();
    }

    @Override
    public void clearMilestones() {
        DataHolder.setMilestones(null);

    }

    @Override
    public void clearCachedEvent() {
        DataHolder.setEvent(null);
    }

    @Override
    public void clearCachedParticipantTeam() {
        DataHolder.setParticipantTeam(null);
    }

    @Override
    public void clearCachedParticipant() {
        DataHolder.setParticipant(null);
    }

    @Override
    public void clearCacheTeamList() {
        DataHolder.setTeamsList(null);
    }

    @Override
    public void updateTeamVisibilityInCache(boolean hidden) {
        DataHolder.getParticipantTeam().setHidden(hidden);
    }

    @Override
    public void updateSocialProfileInCachedParticipant(String participantId, SocialProfile socialProfile) {
        Participant participant = DataHolder.getParticipant();
        if (participant != null && participant.getFbId().equals(participantId)) {
            participant.setParticipantProfile(socialProfile.getPhotoURL());
            participant.setName(socialProfile.getDisplayName());
        }
    }

    @Override
    public boolean isAttached() {
        return !isDestroyed() && !isFinishing() ;
    }

    @Override
    public void initializeLoadingProgressView(String logMarker) {
        Log.d(TAG, logMarker + " initializeLoadingProgressView before progressViewCounter=" + progressViewCounter);

        View rootView = findViewById(android.R.id.content);
        if (rootView != null) {
            View progressView = rootView.findViewById(R.id.view_progress);
            if (progressView != null && progressView.getVisibility() != View.GONE) {
                progressView.setVisibility(View.GONE);
            }
        }
        progressViewCounter = 0;
    }

    @Override
    public void showLoadingProgressView(String logMarker) {
        Log.d(TAG, logMarker + " showLoadingProgressView before progressViewCounter=" + progressViewCounter);
        View  rootView = findViewById(android.R.id.content);
        if (rootView != null) {
            View progressView = rootView.findViewById(R.id.view_progress);
            if (progressView != null && progressView.getVisibility() != View.VISIBLE) {
                progressView.setVisibility(View.VISIBLE);
            }
        }
        ++progressViewCounter;

    }

    @Override
    public void hideLoadingProgressView(String logMarker) {
        Log.d(TAG, logMarker + " hideLoadingProgressView before progressViewCounter=" + progressViewCounter);

        if (progressViewCounter > 0){
            --progressViewCounter;
        }
        if (progressViewCounter <= 0) {
            View  rootView = findViewById(android.R.id.content);
            if (rootView != null) {
                View progressView = rootView.findViewById(R.id.view_progress);
                if (progressView != null && progressView.getVisibility() != View.GONE) {
                    progressView.setVisibility(View.GONE);
                }
            }
            progressViewCounter = 0;
        }

    }

    @Override
    public boolean hideLoadingProgressViewUnStack(String logMarker) {
        Log.d(TAG, logMarker + " hideLoadingProgressViewUnStack before progressViewCounter=" + progressViewCounter);
        if(progressViewCounter > 0) {
            progressViewCounter = 0;
            hideLoadingProgressView(logMarker);
            return true;
        }
        return false;
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
        hideLoadingProgressViewUnStack("showError()" + " error=" + error);
        showMessage(error.getMessage());
    }

    @Override
    public void showError(String message) {
        hideLoadingProgressViewUnStack("showError()" +  " message=" + message);
        showMessage(message);
    }

    @Override
    public void showError(String title, String message, final ErrorDialogCallback errorDialogCallback) {

        hideLoadingProgressViewUnStack("showError()" + " title=" + title + " message=" + message);

        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.view_error_dialog, null);
        ((TextView) dialogView.findViewById(R.id.error_title)).setText(title);
        ((TextView) dialogView.findViewById(R.id.error_message)).setText(message);

        Button okBtn = dialogView.findViewById(R.id.ok_button);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
                if (errorDialogCallback != null) {
                    errorDialogCallback.onOk();
                }
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    @Override
    public void showError(String title, int messageId, final ErrorDialogCallback errorDialogCallback) {
        hideLoadingProgressViewUnStack("showError()" + " title=" + title + " messageId=" + messageId);

        Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showError(int titleId, String message, final ErrorDialogCallback errorDialogCallback) {
        hideLoadingProgressViewUnStack("showError()" + " titleId=" + titleId + " message=" + message);
        showMessage(message);
    }

    @Override
    public void showError(int titleId, int messageId, final ErrorDialogCallback errorDialogCallback) {
        hideLoadingProgressViewUnStack("showError()" + " titleId=" + titleId + " messageId=" + messageId);
        Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onAuthFinished(AuthenticationResult result) {
        Log.i(TAG, "onAuthFinished");
        if (result != null) {
            if (result.isSuccessful()) {
                onFitbitLoggedIn();
                TrackingHelper.trackerConnectionIsValid();
                TrackingHelper.fitbitLoggedIn(true);
            } else if (!result.isDismissed()) {
                displayAuthError(result);
                TrackingHelper.fitbitLoggedIn(false);
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
        FitbitService fService = new FitbitService(trackersSharedPreferences, AuthenticationManager.getAuthenticationConfiguration().getClientCredentials());
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
                trackersSharedPreferences.edit().putString(TrackingHelper.FITBIT_USER_DISPLAY_NAME, displayName).commit();
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
            }
        });
    }

    private void getDeviceProfile() {
        Log.i(TAG, "getDeviceProfile");
        final SharedPreferences trackersSharedPreferences = getSharedPreferences(TrackingHelper.TRACKER_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        FitbitService fService = new FitbitService(trackersSharedPreferences, AuthenticationManager.getAuthenticationConfiguration().getClientCredentials());
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
                trackersSharedPreferences.edit().putString(TrackingHelper.FITBIT_DEVICE_INFO, stringBuilder.toString()).commit();
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
        TrackingHelper.fitbitLoggedIn(false);
    }

    protected void onFitbitLogoutError(String message) {
        Log.i(TAG, "onFitbitLogoutError");
        TrackingHelper.fitbitLoggedIn(false);
    }

    /************* end of fitbit related methods ***************/

    /************* Google Fit related methods   ****************/

    protected void onActivityResultForGoogleFit(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResultForGoogleFit");
        if (resultCode == Activity.RESULT_OK) {
            TrackingHelper.trackerConnectionIsValid();
            TrackingHelper.googleFitLoggedIn(true);
            onGoogleFitLoggedIn();
        } else {
            TrackingHelper.googleFitLoggedIn(false);
        }
    }


    protected void onActivityResultForGoogleLogin(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResultForGoogleLogin");
        if (resultCode == Activity.RESULT_OK) {
        } else {
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

        TrackingHelper.googleFitLoggedIn(false);

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
