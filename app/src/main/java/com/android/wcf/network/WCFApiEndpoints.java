package com.android.wcf.network;


import com.android.wcf.model.Event;
import com.android.wcf.model.Participant;
import com.android.wcf.model.Record;
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
    Single<Integer> deleteParticipant(@Path("fbid") String participantId);

    /*********** TEAM ***********/

    @POST("teams/")
    Single<Team> createTeam(@Body RequestBody params);

    @GET("teams/")
    Single<List<Team>> getTeams();

    @GET("teams/{id}")
    Single<Team> getTeam(@Path("id") int id);

    @GET("teams/{id}/stats")
    Single<Stats> getTeamStats(@Path("id") int id);

    @DELETE("teams/{id}")
    Single<Integer> deleteTeam(@Path("id") int id);

    /*********** SOURCES **********/
    @GET("sources/")
    Single<List<Source>> getSources();

    /********* RECORDS  ************/
    @POST("records/")
    Single<Record> recordSteps(@Body RequestBody params);

}
