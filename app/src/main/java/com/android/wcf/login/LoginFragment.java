package com.android.wcf.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.wcf.R;
import com.android.wcf.base.BaseFragment;
import com.android.wcf.helper.SharedPreferencesUtil;
import com.android.wcf.onboard.OnboardActivity;
import com.android.wcf.web.WebViewActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.util.Arrays;

public class LoginFragment extends BaseFragment implements LoginMvp.LoginView {

    private static final String TAG = LoginFragment.class.getSimpleName();
    private static final String PUBLIC_PROFILE = "public_profile";
    private static final String EMAIL = "email";

    LoginMvp.Host host;
    LoginMvp.Presenter loginPesenter;
    private CallbackManager callbackManager;
    LoginButton loginButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_login, container, false);
        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView(view);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        loginPesenter = new LoginPresenter(this);
        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void setupView(View view) {
        loginButton = view.findViewById(R.id.login_button);
        loginButton.setPermissions(Arrays.asList(PUBLIC_PROFILE, EMAIL));

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("LoginPresenter", "onSuccess called");
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                String userName = "", userId = "", userEmail = "", userGender = "", userProfileUrl = "";
                                try {
                                    AccessToken token = AccessToken.getCurrentAccessToken();
                                    Log.d("access only Token is", String.valueOf(token.getToken()));

                                    userId = object.getString("id");
                                    userName = object.getString("name");
                                    userEmail = object.getString("email");
                                    userProfileUrl = response.getJSONObject().getJSONObject("picture").getJSONObject("data").getString("url");

                                    String savedParticipantId = SharedPreferencesUtil.getMyParticipantId();
                                    if (!userId.equals(savedParticipantId)){
                                        SharedPreferencesUtil.clearMyTeamId();
                                        setParticipantTeam(null);
                                    }

                                    SharedPreferencesUtil.saveMyAuthenticationMethodAsFacebook();
                                    SharedPreferencesUtil.saveMyFacebookId(userId);
                                    SharedPreferencesUtil.saveMyParticipantId(userId);
                                    SharedPreferencesUtil.saveUserLoggedIn(true);
                                    SharedPreferencesUtil.saveUserFullName(userName);
                                    SharedPreferencesUtil.saveUserEmail(userEmail);
                                    SharedPreferencesUtil.saveUserProfilePhotoUrl(userProfileUrl);
                                    joinFBGroup(userId);
                                    loginPesenter.onLoginSuccess();

                                } catch (Exception e) {
                                    Log.e(TAG, "Error processing Facebook Login response\n" + e);
                                    e.printStackTrace();
                                    loginPesenter.onLoginError(null);
                                }
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,birthday,cover,picture.type(large)");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel called");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "Facebook Login error: " + error.getMessage());
                loginPesenter.onLoginError(error.getMessage());
            }
        });
    }


    void joinFBGroup( String userId) {

        //THIS IS NO LONGER SUPPORTED BY FB

//        new GraphRequest(
//                AccessToken.getCurrentAccessToken(),
//                "/466564417434674/members/"+ userId,
//                null,
//                HttpMethod.POST,
//                new GraphRequest.Callback() {
//                    public void onCompleted(GraphResponse response) {
//                        if (response.getError() == null) {
//                            Log.d(TAG, "Facebook Group join response" + response.toString());
//                        } else {
//                            Log.e(TAG, "Facebook Group join error: " + response.getError().getException().getMessage());
//                        }
//                    }
//                }
//        ).executeAsync();

    }

    @Override
    public void showMessage(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    @Override
    public boolean isOnboardingComplete() {
        return !SharedPreferencesUtil.getShowOnboardingTutorial();
    }

    @Override
    public void showHomeActivity() {
        Intent intent = WebViewActivity.createIntent(getActivity());
        this.startActivity(intent);
    }

    @Override
    public void showOnboarding() {
        Intent intent = OnboardActivity.createIntent(getActivity());
        this.startActivity(intent);
    }

}

