package com.android.wcf.facebook;

import android.os.Bundle;
import android.util.Log;

import com.android.wcf.model.Participant;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;

import org.json.JSONObject;

public  class FacebookHelper {
    private static final String TAG = FacebookHelper.class.getSimpleName();

    public static void getParticipantsInfoFromFacebook(final Participant participant,
                                                final FacebookHelper.OnFacebookProfileCallback onFacebookProfileCallback) {
        GraphRequest request = GraphRequest.newGraphPathRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + participant.getFbId() + "/",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        try {
                            JSONObject jsonObject = response.getJSONObject();
                            String userProfileUrl = jsonObject.getJSONObject("picture").getJSONObject("data").getString("url");
                            participant.setName(jsonObject.getString("name"));
                            participant.setParticipantProfile(userProfileUrl);

                        } catch (Exception e) {
                            Log.e(TAG, "Error processing Facebook Login response\n" + e);
                            e.printStackTrace();
                        }
                        if (onFacebookProfileCallback != null) {
                            onFacebookProfileCallback.onParticipantProfileRetrieved(participant);
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, name, email, picture");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public static void logout() {

        if (AccessToken.getCurrentAccessToken() == null) {
            return; // user already logged out
        }

        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                LoginManager.getInstance().logOut();

            }
        }).executeAsync();

    }


    public interface OnFacebookProfileCallback {
        void onParticipantProfileRetrieved(Participant participant);
    }

    public void initFacebookLogging() {
        FacebookSdk.setAutoInitEnabled(true);
        FacebookSdk.fullyInitialize();
    }
}
