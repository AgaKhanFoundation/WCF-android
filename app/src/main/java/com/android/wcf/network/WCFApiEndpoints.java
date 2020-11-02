package com.android.wcf.network;


import com.android.wcf.model.Commitment;
import com.android.wcf.model.Event;
import com.android.wcf.model.LeaderboardTeam;
import com.android.wcf.model.Milestone;
import com.android.wcf.model.Notification;
import com.android.wcf.model.Participant;
import com.android.wcf.model.Record;
import com.android.wcf.model.SocialProfile;
import com.android.wcf.model.Source;
import com.android.wcf.model.Stats;
import com.android.wcf.model.Team;

import java.util.List;

import io.reactivex.Single;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface WCFApiEndpoints {

    /*********** EVENT ***********/
    @GET("events/{id}")
    Single<Event> getEvent(@Path("id") int eventId);

    @GET("events/")
    Single<List<Event>> getEvents();

    /*********** PARTICIPANT ***********/

    @POST("participants/")
    Single<Participant> createParticipant(@Body RequestBody params);

    @GET("participants/{fbid}")
    Single<Participant> getParticipant(@Path("fbid") String participantId);

    @PATCH("participants/{fbid}")
    Single<List<Integer>> updateParticipant(@Path("fbid") String participantId, @Body RequestBody params);

    @GET("participants/{fbid}/stats")
    Single<Stats> getParticipantStats(@Path("fbid") String participantId);

    @DELETE("participants/{fbid}")
    Single<List<Integer>> deleteParticipant(@Path("fbid") String participantId);

    @GET("participants/{fbid}/social")
    Single<SocialProfile> getParticipantSocialProfile(@Path("fbid") String participantId);

    /*********** PARTICIPANT COMMITMENTS ***********/

    @POST("commitments/")
    Single<Commitment> createParticipantCommitment(@Body RequestBody params);

    @PATCH("commitments/{id}")
    Single<List<Integer>> updateParticipantCommitment(@Path("id") int commitmentId, @Body RequestBody params);

    @GET("commitments/participant/{fbid}")
    Single<List<Commitment>> getParticipantCommitment(@Path("fbid") String fbId);

    @GET("commitments/event/{id}")
    Single<List<Commitment>> getEventCommitments(@Path("id") int eventId);

    /*********** TEAM ***********/

    @POST("teams/")
    Single<Team> createTeam(@Body RequestBody params);

    @GET("teams/")
    Single<List<Team>> getTeamsList();

    @GET("teams/{id}")
    Single<Team> getTeam(@Path("id") int id);

    @GET("teams/{id}/stats")
    Single<Stats> getTeamStats(@Path("id") int id);

    @DELETE("teams/{id}")
    Single<List<Integer>> deleteTeam(@Path("id") int id);

    @PATCH("teams/{id}")
    Single<List<Integer>> updateTeam(@Path("id") int teamId, @Body RequestBody params);

    @GET("teams/stats/{id}")
    Single<List<LeaderboardTeam>> getLeaderboard(@Path("id") int eventid);

    /*********** SOURCES **********/
    @GET("sources/")
    Single<List<Source>> getSources();

    /********* RECORDS  ************/
    @POST("records/")
    Single<Record> recordSteps(@Body RequestBody params);


    /********* JOURNEY MILESTONES  ************/
    @GET("achievement/")
    Single<List<Milestone>> getJourneyMilestones();


    /******* NOTIFICATIONS **********/
    @GET("notifications/participant/{fbid}/event/{eventId}")
    Single<List<Notification>> getParticipantNotifications(@Path("fbid") String fbId, @Path("eventId") int eventId );

    @PATCH("notifications/notification/{participantNotificationId}")
    Single<List<Integer>> updateParticipantNotification(@Path("participantNotificationId") int participantNotificationId, @Body RequestBody params);

}
