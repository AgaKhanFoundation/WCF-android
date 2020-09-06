package com.android.wcf.login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.wcf.R;
import com.android.wcf.application.WCFApplication;
import com.android.wcf.base.BaseFragment;
import com.android.wcf.base.RequestCodes;
import com.android.wcf.helper.SharedPreferencesUtil;
import com.android.wcf.model.AuthSource;
import com.android.wcf.model.Constants;
import com.android.wcf.network.WCFClient;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;

import java.util.Arrays;
import java.util.Date;

public class LoginFragment extends BaseFragment implements LoginMvp.View {

    private static final String TAG = LoginFragment.class.getSimpleName();
    private static final String PUBLIC_PROFILE = "public_profile";

    LoginMvp.Host mLoginHost;
    LoginMvp.Presenter mLoginPesenter;
    private CallbackManager mFacebookCallbackManager;
    LoginButton mLoginButtonFacebook;
    SignInButton mLoginButtonGoogle;
    GoogleSignInClient mGoogleSignInClient;
    ImageView mAppleSignInButton;

    View mTermsTv;

    private FirebaseAuth mAuth;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LoginMvp.Host) {
            mLoginHost = (LoginMvp.Host) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement LoginMvp.Host");
        }
    }

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
        mLoginPesenter = new LoginPresenter(this);
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RequestCodes.LOGIN_REQUEST_GOOGLE_CODE) {
            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google signin failed", e);
                mLoginPesenter.onLoginError("Could not signin with Google:\n" + e);
            }
        } else if (FacebookSdk.isFacebookRequestCode(requestCode)){
            mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            onSigninComplete(user, AuthSource.Google);
                        } else {
                            Log.w(TAG, "Google signInWithCredential:failure", task.getException());
                            mLoginPesenter.onLoginError("Could not login with Google");
                        }
                    }
                });
    }


    @Override
    public void onStart() {
        super.onStart();
        mLoginHost.hideToolbar();

        checkPendingAuth();
    }

    @Override
    public void onStop() {
        super.onStop();
        mLoginPesenter.onStop();
    }

    private void setupView(View view) {

        mLoginButtonFacebook = view.findViewById(R.id.login_button_facebook);

        mTermsTv = view.findViewById(R.id.term_conditions);
        mTermsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginHost.showTermsAndConditions();
            }
        });
        TextView appVersionTv = view.findViewById(R.id.app_version);
        appVersionTv.setText(WCFApplication.instance.getAppVersion());

        appVersionTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchServerForTestingTeam();
            }
        });

        TextView hashKeyTV = view.findViewById(R.id.hash_key);
        hashKeyTV.setText(WCFApplication.instance.getHashKey());

        if (mLoginButtonFacebook != null) {
            mLoginButtonFacebook.setFragment(this);
            mLoginButtonFacebook.setPermissions(Arrays.asList(PUBLIC_PROFILE));

            mFacebookCallbackManager = CallbackManager.Factory.create();

            mLoginButtonFacebook.registerCallback(mFacebookCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.d("LoginPresenter", "onSuccess called");

                    handleFacebookAccessToken(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {
                    Log.d(TAG, "onCancel called");
                }

                @Override
                public void onError(FacebookException error) {
                    Log.e(TAG, "Facebook Login error: " + error.getMessage());
                    if (error.getMessage().contains("CONNECTION_FAILURE")) {
                        showNetworkErrorMessage(R.string.login_title);
                    } else {
                        mLoginPesenter.onLoginError(error.getMessage());
                    }
                }
            });
        }

        mLoginButtonGoogle = view.findViewById(R.id.login_button_google);
        if (mLoginButtonGoogle != null) {
            mLoginButtonGoogle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    googleSignIn();
                }
            });
        }

        mAppleSignInButton =  view.findViewById(R.id.login_button_apple);
        if (mAppleSignInButton != null) {
            mAppleSignInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    appleSignIn();
                }
            });
        }
    }

    private void checkPendingAuth() {

// Apple
//        Task<AuthResult> pending = mAuth.getPendingAuthResult();
//        if (pending != null) {
//            pending.addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//                @Override
//                public void onSuccess(AuthResult authResult) {
//                    Log.d(TAG, "Apple checkPending:onSuccess:" + authResult);
//                    FirebaseUser user = authResult.getUser();
//                    onSigninComplete(user, AuthSource.Apple);
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Log.w(TAG, "Apple checkPending:onFailure", e);
//                    mLoginPesenter.onLoginError("Could not login with Apple: " + e.getMessage());
//                }
//            });
//        } else {
//            Log.d(TAG, "pending: null");
//        }

// Google
//
//        OptionalPendingResult<GoogleSignInResult> optionalPendingResult = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
//        if (optionalPendingResult.isDone()) {
//            GoogleSignInResult googleSignInResult = optionalPendingResult.get();
//            handleSignInResult(googleSignInResult);
//        } else {
//            optionalPendingResult.setResultCallback(new ResultCallback<GoogleSignInResult>() {
//                @Override
//                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
//                    handleSignInResult(googleSignInResult);
//                }
//            });
//        }
//        auth.addAuthStateListener(authListener);

    }

    private void googleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RequestCodes.LOGIN_REQUEST_GOOGLE_CODE);
    }

    private void appleSignIn() {
        OAuthProvider appAuthProvider = OAuthProvider.newBuilder("apple.com", mAuth)
                .build();

        mAuth.startActivityForSignInWithProvider(getActivity(), appAuthProvider)
                .addOnSuccessListener(
                        new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                FirebaseUser user = authResult.getUser();
                                onSigninComplete(user, AuthSource.Apple);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Could not login with Apple :onFailure", e);
                                mLoginPesenter.onLoginError("Could not login with Apple: " + e.getMessage());
                            }
                        });


    }

    private void onSigninComplete(FirebaseUser user, AuthSource authSource) {
        if (user == null) {
            return;

        }
        String userName = "", userId = "", userProfileUrl = "", authSourceUserId;
        userId = user.getUid();
        userName = user.getDisplayName();
        userProfileUrl = user.getPhotoUrl().toString();
        authSourceUserId = user.getProviderId();

        Log.d(TAG, "onSigninComplete():"
                + " authSourceUserId=" + authSourceUserId
                + " userName=" + userName
                + " userId=" + userId
                + "\nuserProfileUrl=" + userProfileUrl);
        String savedParticipantId = SharedPreferencesUtil.getMyParticipantId();
        if (!userId.equals(savedParticipantId)) {
            SharedPreferencesUtil.clearMyStepsCommitted();
            SharedPreferencesUtil.clearMyTeamId();
            SharedPreferencesUtil.clearAkfProfileCreated();
            cacheParticipantTeam(null);
        }

        SharedPreferencesUtil.saveMyLoginId(userId, authSource, authSourceUserId);
        SharedPreferencesUtil.saveUserLoggedIn(true);
        SharedPreferencesUtil.saveUserFullName(userName);
        SharedPreferencesUtil.saveUserProfilePhotoUrl(userProfileUrl);
        joinFBGroup(userId);

        mLoginPesenter.onLoginSuccess();
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            onSigninComplete(user, AuthSource.Facebook);
                        } else {
                            Log.w(TAG, "Facebook signInWithCredential:failure", task.getException());
                            Toast.makeText(getActivity(), "Could not login with Facebook",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    void joinFBGroup(String userId) {
        //THIS IS NO LONGER SUPPORTED BY FB
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

    public void loginComplete() {
        LoginHelper.loginIsValid();
        mLoginHost.loginComplete();

    }

    int switchRequestClickCount = 0;
    long lastSwitchClickAt = new Date().getTime();

    private void switchServerForTestingTeam() {
        long now = new Date().getTime();
        Log.d(TAG, "Click count=" + switchRequestClickCount + " timeDelta=" + (now - lastSwitchClickAt));
        if (now - lastSwitchClickAt < 1000) { //upto .1 sec gap allowed between clicks
            switchRequestClickCount++;
            if (switchRequestClickCount >= 2) {
                switchRequestClickCount = 0;
                confirmServerSwitch();
            }
        } else {
            switchRequestClickCount = 0;
        }
        lastSwitchClickAt = now;
    }

    private void confirmServerSwitch() {

        final AlertDialog dialogBuilder = new AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.view_password_entry, null);

        final TextView connectionMessageTv = dialogView.findViewById(R.id.current_connection_message);
        if (WCFClient.isProdBackend()) {
            connectionMessageTv.setText(getString(R.string.connected_to_prod_server));
        } else {
            connectionMessageTv.setText(getString(R.string.connected_to_test_server));
        }

        final EditText editText = dialogView.findViewById(R.id.password);
        Button okBtn = dialogView.findViewById(R.id.ok);
        Button cancelBtn = dialogView.findViewById(R.id.cancel);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
                closeKeyboard();
            }
        });
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
                closeKeyboard();
                if (editText.getText().toString().equals("akftester1")) {
                    mLoginHost.switchServerForTestingTeam();
                } else {
                    Toast.makeText(getContext(), getString(R.string.invalid_password), Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();

    }

}

