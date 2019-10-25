package com.android.wcf.network;

import android.util.ArrayMap;

import com.android.wcf.BuildConfig;
import com.android.wcf.helper.typeadapters.DateStringLongConverter;
import com.android.wcf.helper.typeadapters.DateStringTypeConverter;
import com.android.wcf.model.Commitment;
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
import kotlin.Pair;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class WCFClient {

    private static final String TAG = WCFClient.class.getSimpleName();

    private static Steps4ChangeEnv serverEnv = Steps4ChangeEnv.PROD; //Ensure its PROD for a store build


    public static void switchServerForTestingTeam() {
        if (isProdBackend()) {
            serverEnv = Steps4ChangeEnv.STAGE;

        }
        else {
            serverEnv = Steps4ChangeEnv.PROD;
        }
        WCFAuth.clearTokenHeader();
        instance = null;
    }

    private static WCFClient instance;
    private WCFApiEndpoints wcfApi;

    public static WCFClient getInstance() {
        if (instance == null) {
            instance = new WCFClient();
        }
        return instance;
    }

    public static boolean isProdBackend() {
        return serverEnv == Steps4ChangeEnv.PROD;
    }

    private WCFClient() {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.BODY);

        Interceptor requestInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl url = original.url().newBuilder()
                        .build();

              Pair<String, String> authHeader = WCFAuth.basicHeader(serverEnv);
                Request request = original.newBuilder()
                        .url(url)
                        .header("Accept", "application/json")
                        .header( authHeader.getFirst(), authHeader.getSecond()) //TODO: activate after we have the password and updated in build script
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
                .baseUrl(serverEnv.getUrl())
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
        jsonParams.put(Team.TEAM_ATTRIBUTE_LEADER_ID, teamLeadParticipantId);
        jsonParams.put(Team.TEAM_ATTRIBUTE_VISIBILITY, teamVisibility);

        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                new JSONObject(jsonParams).toString());

        return wcfApi.createTeam(requestBody);
    }

    public Single<List<Integer>> updateTeamVisibility(int teamId, boolean teamVisibility) {

        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put(Team.TEAM_ATTRIBUTE_VISIBILITY, teamVisibility);

        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                new JSONObject(jsonParams).toString());

        return wcfApi.updateTeam(teamId, requestBody);
    }

    public Single<List<Team>> getTeamsList() {
        return wcfApi.getTeamsList();
    }

    public Single<Team> getTeam(int teamId) {
        return wcfApi.getTeam(teamId);
    }

    public Single<Stats> getTeamStats(int teamId) {
        return wcfApi.getTeamStats(teamId);
    }

    public Single<List<Integer>> deleteTeam(int teamId) {
        return wcfApi.deleteTeam(teamId);
    }

    public Single<Participant> createParticipant(String participantId) {
        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put(Participant.PARTICIPANT_ATTRIBUTE_FBID, participantId);

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
            jsonParams.put(Participant.PARTICIPANT_ATTRIBUTE_FBID, newParticipantId);
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

    public Single<List<Integer>> updateTeamName(int teamId, String teamName) {
        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put(Team.TEAM_ATTRIBUTE_NAME, teamName);

        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                new JSONObject(jsonParams).toString());

        return wcfApi.updateTeam(teamId, requestBody);
    }

    public Single<List<Integer>> updateParticipantTrackingSource(String participantId, Integer sourceId) {
        Map<String, Object> jsonParams = new ArrayMap<>();
        if (sourceId > 0) jsonParams.put(Participant.PARTICIPANT_ATTRIBUTE_SOURCE_ID, sourceId);

        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                (new JSONObject(jsonParams)).toString());

        return wcfApi.updateParticipant(participantId, requestBody);
    }

    public Single<List<Integer>> deleteParticipant(String participantId) {
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

    public Single<Commitment> createParticipantCommitment(String participantId, int eventId, int commitmentSteps) {
        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put(Commitment.COMMITMENT_ATTRIBUTE_FB_ID, participantId);
        jsonParams.put(Commitment.COMMITMENT_ATTRIBUTE_EVENT_ID, eventId);
        jsonParams.put(Commitment.COMMITMENT_ATTRIBUTE_COMMITMENT, commitmentSteps);

        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                new JSONObject(jsonParams).toString());

        return wcfApi.createParticipantCommitment(requestBody);
    }

    public Single<List<Integer>> updateParticipantCommitment(int commitmentId, String participantId, int eventId, int commitmentSteps) {
        Map<String, Object> jsonParams = new ArrayMap<>();

        jsonParams.put(Commitment.COMMITMENT_ATTRIBUTE_FB_ID, participantId);

        jsonParams.put(Commitment.COMMITMENT_ATTRIBUTE_EVENT_ID, eventId);

        jsonParams.put(Commitment.COMMITMENT_ATTRIBUTE_COMMITMENT, commitmentSteps);


        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                new JSONObject(jsonParams).toString());

        return wcfApi.updateParticipantCommitment(commitmentId, requestBody);
    }

    public Single<List<Commitment>> getParticipantCommitments(String fbId) {
        return wcfApi.getParticipantCommitment(fbId);
    }

    public Single<List<Commitment>> getEventCommitments(int eventId) {
        return wcfApi.getEventCommitments(eventId);
    }

    public Single<List<Integer>> updateParticipantProfileRegistered(String participantId) {
        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put(Participant.PARTICIPANT_ATTRIBUTE_REGISTERED, true);

        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                new JSONObject(jsonParams).toString());

        return wcfApi.updateParticipant(participantId, requestBody);


    }
}