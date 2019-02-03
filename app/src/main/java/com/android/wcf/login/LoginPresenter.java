package com.android.wcf.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.wcf.activity.MainTabActivity;
import com.android.wcf.home.HomeMvp;
import com.android.wcf.utils.Preferences;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.Arrays;

public class LoginPresenter extends WCFActivityPresenter<LoginMvp.LoginView> {
  private CallbackManager callbackManager;
  private LoginMvp.LoginView view;
  private AccessToken accessToken;
  private Boolean isLoggedIn;

  public LoginPresenter(LoginMvp.LoginView LoginView) {
    this.view = LoginView;
    callbackManager = CallbackManager.Factory.create();
    accessToken = AccessToken.getCurrentAccessToken();
    isLoggedIn = accessToken != null && !accessToken.isExpired();
    //setupApi();
  }

//  @Override
//  public void onCreate(@NotNull LoginView view) {
//    //TODO : follow project structure
//    super.onCreate(view);
//    callbackManager = CallbackManager.Factory.create();
//    accessToken = AccessToken.getCurrentAccessToken();
//    isLoggedIn = accessToken != null && !accessToken.isExpired();
//  }

  public void onFbLoginPressed() {
    LoginManager.getInstance().logInWithReadPermissions((Activity) view, Arrays.asList("public_profile", "email"));
    if (!isLoggedIn) {
      Log.d("LoginPresenter", "onFbLoginPressed: user not logged in");
      LoginManager.getInstance().registerCallback(callbackManager, mCallBack);
    } else
      LoginManager.getInstance().logOut();
  }

  private FacebookCallback<LoginResult> mCallBack = new FacebookCallback<LoginResult>() {
    @Override
    public void onSuccess(LoginResult loginResult) {

      GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
          new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
              String userName = "", userId = "", userEmail = "", userGender = "", userProfileUrl = "";
              Log.e("response: ", response + "");
              try {
                AccessToken token = AccessToken.getCurrentAccessToken();
                Log.d("access only Token is", String.valueOf(token.getToken()));
                String facebook_id_token = String.valueOf(token.getToken());
                userName = object.getString("name").toString();
                userId = object.getString("id").toString();
                userEmail = object.getString("email").toString();
                userGender = object.getString("gender").toString();
                userProfileUrl = response.getJSONObject().getJSONObject("picture").getJSONObject("data").getString("url");
                view.showMainTabActivity(Arrays.asList(userName, userId, userEmail, userGender, userProfileUrl));

              } catch (Exception e) {
                e.printStackTrace();
              }
            }
          });

      Bundle parameters = new Bundle();
      parameters.putString("fields", "id,name,email,gender, birthday,cover,picture.type(large)");
      request.setParameters(parameters);
      request.executeAsync();
    }

    @Override
    public void onCancel() {
    }

    @Override
    public void onError(FacebookException e) {
    }
  };

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    callbackManager.onActivityResult(requestCode, resultCode, data);
  }
}
