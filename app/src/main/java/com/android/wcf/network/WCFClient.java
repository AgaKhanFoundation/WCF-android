package com.android.wcf.network;

import android.util.ArrayMap;
import android.util.Log;

import com.android.wcf.BuildConfig;
import com.android.wcf.helper.typeadapters.DateStringLongConverter;
import com.android.wcf.helper.typeadapters.DateStringTypeConverter;
import com.android.wcf.model.Event;
import com.android.wcf.model.Participant;
import com.android.wcf.model.Record;
import com.android.wcf.model.Source;
import com.android.wcf.model.Stats;
import com.android.wcf.model.Team;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class WCFClient {

    //    private final static String AKF_WCF_BACKEND_URL = "http://40.121.10.181:80/";
    private final static String AKF_WCF_BACKEND_URL = "https://sultan.step4change.org/";


    private static WCFClient instance;
    private WCFApiEndpoints wcfApi;

    public static WCFClient getInstance() {
        if (instance == null) {
            instance = new WCFClient();
        }
        return instance;
    }

    private WCFClient() {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.BODY);

        Interceptor requestInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl url = original.url().newBuilder()
//                        .addQueryParameter("api_key", API_KEY)
                        .build();

                // Customize the request
                Request request = original.newBuilder()
                        .url(url)
                        .header("Accept", "application/json")
                        .method(original.method(), original.body())
                        .build();

                Response response = chain.proceed(request);

                // Customize or return the response
                return response;
            }
        };

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.addInterceptor(requestInterceptor);
        httpClientBuilder.addInterceptor(loggingInterceptor);
        httpClientBuilder.readTimeout(60, TimeUnit.SECONDS);
        httpClientBuilder.connectTimeout(60, TimeUnit.SECONDS);
        final OkHttpClient httpClient = httpClientBuilder.build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateStringTypeConverter())
                .registerTypeAdapter(Long.class, new DateStringLongConverter())
                .create();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AKF_WCF_BACKEND_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        wcfApi = retrofit.create(WCFApiEndpoints.class);
    }

    public Single<Event> getEvent(int eventId) {
        return wcfApi.getEvent(eventId);
    }

    public Single<List<Event>> getEvents() {
        return wcfApi.getEvents();
    }

    public Single<Team> createTeam(String teamName, String teamLeadParticipantId, boolean teamVisibility) {
        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put(Team.TEAM_ATTRIBUTE_NAME, teamName);
        jsonParams.put(Team.TEAM_LEADER_ID_ATTRIBUTE_NAME, teamLeadParticipantId);
        jsonParams.put(Team.TEAM_VISIBILITY_ATTRIBUTE_NAME, teamVisibility);

        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                new JSONObject(jsonParams).toString());

        return wcfApi.createTeam(requestBody);
    }

    public Single<List<Team>> getTeams() {
        return wcfApi.getTeams();
    }

    public Single<Team> getTeam(int teamId) {
        return wcfApi.getTeam(teamId);
    }

    public Single<Stats> getTeamStats(int teamId) {
        return wcfApi.getTeamStats(teamId);
    }

    public Single<Integer> deleteTeam(int teamId) {
        return wcfApi.deleteTeam(teamId);
    }

    public Single<Participant> createParticipant(String participantId) {
        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put(Participant.PARTICIPANT_ATTRIBUTE_ID, participantId);

        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                new JSONObject(jsonParams).toString());

        return wcfApi.createParticipant(requestBody);
    }

    public Single<Participant> getParticipant(String participantId) {
        return wcfApi.getParticipant(participantId);
    }

    public Single<Stats> getParticipantStat(String participantId) {
        return wcfApi.getParticipantStats(participantId);
    }

    public Single<List<Integer>> updateParticipant(String currentParticipantId, String newParticipantId, Integer teamId, Integer causeId, Integer localityId, Integer eventId) {
        Map<String, Object> jsonParams = new ArrayMap<>();
        if (newParticipantId != null && !newParticipantId.isEmpty())
            jsonParams.put(Participant.PARTICIPANT_ATTRIBUTE_ID, newParticipantId);
        if (teamId > 0) jsonParams.put(Participant.PARTICIPANT_ATTRIBUTE_TEAM_ID, teamId);
        if (causeId > 0) jsonParams.put(Participant.PARTICIPANT_ATTRIBUTE_CAUSE_ID, causeId);
        if (localityId > 0) jsonParams.put(Participant.PARTICIPANT_ATTRIBUTE_LOCALITY_ID, localityId);
        if (eventId > 0) jsonParams.put(Participant.PARTICIPANT_ATTRIBUTE_EVENT_ID, eventId);

        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                new JSONObject(jsonParams).toString());

        return wcfApi.updateParticipant(currentParticipantId, requestBody);
    }

    public Single<List<Integer>> updateParticipantLeaveTeam(String participantId) {
        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put(Participant.PARTICIPANT_ATTRIBUTE_TEAM_ID, null);

        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                new JSONObject(jsonParams).toString());

        return wcfApi.updateParticipant(participantId, requestBody);
    }

    public Single<List<Integer>> updateParticipantTrackingSource(String participantId, Integer sourceId) {
        Map<String, Object> jsonParams = new ArrayMap<>();
        if (sourceId > 0) jsonParams.put(Participant.PARTICIPANT_ATTRIBUTE_SOURCE_ID, sourceId);

        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                (new JSONObject(jsonParams)).toString());

        return wcfApi.updateParticipant(participantId, requestBody);
    }

    public Single<Integer> deleteParticipant(String participantId) {
        return wcfApi.deleteParticipant(participantId);
    }

    public Single<List<Source>> getTrackingSources() {
        return wcfApi.getSources();
    }



    public Single<Record> recordSteps(int participantId, int trackerSourceId, String trackedDate, long stepsCount) {
        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put(Record.RECORD_ATTRIBUTE_PARTICIPANT_ID, participantId);
        jsonParams.put(Record.RECORD_ATTRIBUTE_SOURCE_ID, trackerSourceId);
        jsonParams.put(Record.RECORD_ATTRIBUTE_DATE_ID, trackedDate);
        jsonParams.put(Record.RECORD_ATTRIBUTE_DISTANCE_ID, stepsCount);

        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                new JSONObject(jsonParams).toString());

        return wcfApi.recordSteps(requestBody);
    }
}