package com.android.wcf.network;

import android.util.ArrayMap;

import com.android.wcf.BuildConfig;
import com.android.wcf.helper.typeadapters.DateStringLongConverter;
import com.android.wcf.helper.typeadapters.DateStringTypeConverter;
import com.android.wcf.model.Commitment;
import com.android.wcf.model.Event;
import com.android.wcf.model.LeaderboardTeam;
import com.android.wcf.model.Milestone;
import com.android.wcf.model.Participant;
import com.android.wcf.model.Record;
import com.android.wcf.model.Source;
import com.android.wcf.model.Stats;
import com.android.wcf.model.Team;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

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

    private static Steps4ChangeEnv serverEnv = Steps4ChangeEnv.STAGE; //Ensure its PROD for a store build


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

    public Single<List<LeaderboardTeam>> getLeaderboard(int eventId) {
        return wcfApi.getLeaderboard(eventId);
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

    public Single<List<Milestone>> getJourneyMilestones(int eventId) {
        if (1 == 1) { //until the API is fixed, return a hard-coded resultset
            Gson gson = new Gson();
            List<Milestone> result = gson.fromJson(getMilestonesJson(),new TypeToken<List<Milestone>>(){}.getType());
            return Single.just(result);
        }
        return wcfApi.getJourneyMilestones();
    }

    private String getMilestonesJson(){
        return "[\n" +
                "  {\n" +
                "    \"sequence\": 0,\n" +
                "    \"distance\": 0,\n" +
                "    \"name\": \"Start\",\n" +
                "    \"flag_name\": \"\",\n" +
                "    \"journey_map\":\"http://wcf.x10host.com/challenge1_0.png\",\n" +
                "    \"description\":\"\",\n" +
                "    \"title\": \"\",\n" +
                "    \"subtitle\": \"\",\n" +
                "    \"media\": \"\",\n" +
                "    \"content\":\"\"\n" +
                "  },\n" +
                "    {\n" +
                "    \"sequence\": 1,\n" +
                "    \"distance\": 2580000,\n" +
                "    \"name\": \"Nairobi, Kenya\",\n" +
                "    \"flag_name\": \"Kenya\",\n" +
                "    \"journey_map\":\"http://wcf.x10host.com/challenge1_1.png\",\n" +
                "    \"description\":\"\",\n" +
                "    \"title\": \"Primary And Secondary: Stretching Their Wings\",\n" +
                "    \"subtitle\": \"Viola, a student\",\n" +
                "    \"media\": \"photo:http://wcf.x10host.com/1.Nairobi.png\",\n" +
                "    \"content\": \"Viola is a Grade 9 student at Joy Primary School in Mathare, a notoriously poor neighborhood in Nairobi, Kenya. There, few families have books. Even schools lack suitable reading material. Across Kenya, only 2 percent of public schools have libraries.\\r\\n \\r\\nBefore, Viola and her friends had very few chances to practice reading. The whole school had just a box of a few textbooks. When Viola first arrived several years ago, she was far behind her peers, with no way to catch up. \\u201CI was not good at reading,\\u201D she admits.\\r\\n \\r\\nThat changed when Joy School gained a library, thanks to a Kenyan nonprofit that raised funds for libraries with its Start-A-Library campaign, supported by the Aga Khan Foundation and the Yetu Initiative. The new library at Joy brought in 1,000 storybooks, and Viola started devouring them. \\r\\n\\r\\nRead Viola\\u2019s story here: \\r\\nhttps:\\/\\/www.akfusa.org\\/our-stories\\/primary-secondary-stretching-their-wings\\/.\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"sequence\": 2,\n" +
                "    \"distance\": 5120000,\n" +
                "    \"name\": \"Karachi, Pakistan\",\n" +
                "    \"flag_name\": \"Pakistan\",\n" +
                "    \"journey_map\":\"http://wcf.x10host.com/challenge1_2.png\",\n" +
                "    \"description\":\"\",\n" +
                "    \"title\": \"Reaching New Heights In National Healthcare: Zainab’s Story\",\n" +
                "    \"subtitle\": \"Dr. Zainab Samad, a doctor\",\n" +
                "    \"media\": \"photo:http://wcf.x10host.com/2.Karachi.png\",\n" +
                "    \"content\":\"Dr. Zainab Samad was only 13 years old when she knew she wanted to become a doctor. She was able to fulfill her dream by attending medical school at the Aga Khan University. Growing up, she didn\\u2019t see many female leaders in her field and despite her passion for medicine, the idea of playing a leading role at a university was a leap beyond her imagination. Nevertheless, after years of studying at AKU and abroad, Dr. Samad eventually returned to Karachi to lead the Medicine Department at AKU. She\\u2019s the youngest person ever to hold that position, and the first woman.\\r\\n\\r\\nZainab\\u2019s journey to new heights was long, but she hopes that it will inspire other women to follow in her footsteps. \\u201CAKU has had female chairs in other departments, including Obstetrics-Gynecology and Anesthesia,\\u201D she said. \\u201CI hope it means we\\u2019ll have more.\\u201D\\r\\n\\r\\nAga Khan University became the leading healthcare institution in Pakistan and influences healthcare practice and policy across the country. AKU Hospital will continue to develop skills of healthcare professionals like Zainab to deliver world-class care. \\r\\n\\r\\nRead Zainab\\u2019s story here: https:\\/\\/www.akfusa.org\\/our-stories\\/reaching-new-heights-in-national-healthcare\\/.\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"sequence\": 3,\n" +
                "    \"distance\": 6070000,\n" +
                "    \"name\": \"Badakhshan, Afghanistan\",\n" +
                "    \"flag_name\": \"Afghanistan\",\n" +
                "    \"journey_map\":\"http://wcf.x10host.com/challenge1_3.png\",\n" +
                "    \"description\":\"\",\n" +
                "    \"title\": \"Sara, a young girl who dreams of becoming a doctor\",\n" +
                "    \"subtitle\": \"Meet Sara\",\n" +
                "    \"media\": \"photo::http://wcf.x10host.com/3.Badakshan.png video:https://youtu.be/W__4h4p6Zp8\",\n" +
                "    \"content\":\"Sara is seven years old and lives in one of the world\\u2019s most remote areas\\u2014in northern Afghanistan. She dreams of becoming a doctor, but access to education isn\\u2019t the only thing that will get her there. Sarah needs books, a uniform, safe roads to get to school, qualified teachers, and electricity to study. \\r\\n\\r\\nSara is multifaceted. Her development should be, too. In Afghanistan, where many girls like Sara live, the Aga Khan Foundation applies an integrated development approach. \\r\\n\\r\\nLearn more about Sara and AKF\\u2019s approach here: https:\\/\\/www.akfusa.org\\/ourwork\\/meet-sara.\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"sequence\": 4,\n" +
                "    \"distance\": 7070000,\n" +
                "    \"name\": \"Bihar, India\",\n" +
                "    \"flag_name\": \"India\",\n" +
                "    \"journey_map\":\"http://wcf.x10host.com/challenge1_4.png\",\n" +
                "    \"description\":\"\",\n" +
                "    \"title\": \"Supporting the Empowerment of Adolescent Girls in India\",\n" +
                "    \"subtitle\": \"Jyothi, a participant of AKF’s girls empowerment program\",\n" +
                "    \"media\": \"photo:http://wcf.x10host.com/4.Bihar.png photo:http://wcf.x10host.com/3.Badakshan.png\",\n" +
                "    \"content\": \"As India\\u2019s population has become younger, the rapidly growing proportion of young girls in the country presents an opportunity for early investment in their learning and overall development. \\r\\n\\r\\nAKF works with out-of-school adolescent girls from poor and disadvantaged communities in rural India to prioritize their education, health, and wellbeing so that they can achieve economic and social empowerment. Key to AKF\\u2019s strategy is community engagement and building awareness among parents and community members about the importance of supporting girls to fulfil their potential. AKF\\u2019s approach to adolescent girls\\u2019 empowerment is three-pronged, comprising of (1) scholastic support, (2) vocational training, and (3) life skills education. The strategy supports out-of-school girls to build on their education, whether through functional literacy training, remedial support, or school enrollment assistance.\\r\\n\\r\\nLearn more about AKF\\u2019s work to support girls in India: https:\\/\\/www.akdn.org\\/sites\\/akdn\\/files\\/2018_09_-_akf_india_-_supporting_the_empowerment_of_adolescent_girls_in_india.pdf.\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"sequence\": 5,\n" +
                "    \"distance\": 8030000,\n" +
                "    \"name\": \"Chitral, Pakistan\",\n" +
                "    \"flag_name\": \"Pakistan\",\n" +
                "    \"journey_map\":\"http://wcf.x10host.com/challenge1_5.png\",\n" +
                "    \"description\":\"\",\n" +
                "    \"title\": \"Title: In Chitral, Pakistan, Educators Champion Change\",\n" +
                "    \"subtitle\": \"Shanila Parveen, a teacher\",\n" +
                "    \"media\": \"photo:http://wcf.x10host.com/5.Chirtal.png\",\n" +
                "    \"content\":\"In Garam Chasma (\\u201Chot springs\\u201D), Shanila Parveen is at the frontlines of the educational transformation sparked by the Aga Khan Foundation\\u2019s School Improvement Program (SIP). Every day, she walks three hours to school\\u2014and three hours home\\u2014to ensure that her students get a quality education. Through support and training from AKF, she helped start the early childhood development centre in her school and mobilized mothers to help her lead the classes. Shanila notes the recharged energy in her students, thanks to both her new tactics and the physical change in the school.\\r\\n\\r\\nIn 2016, the Aga Khan Foundation\\u2019s School Improvement Program (SIP) began in select schools in Pakistan\\u2019s mountainous northern region. It focuses on improving children\\u2019s learning outcomes by strengthening the capacity of all stakeholders to support them. It also taps into the core motivations of teachers to engage and empower them to be true champions of educational change. \\r\\n\\r\\nRead Shanila\\u2019s story here: https:\\/\\/www.akfusa.org\\/our-stories\\/in-chitral-pakistan-educators-champion-change\\/.\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"sequence\": 6,\n" +
                "    \"distance\": 8910000,\n" +
                "    \"name\": \"Jalal-abad, Kyrgyz Republic\",\n" +
                "    \"flag_name\": \"Kyrgistan\",\n" +
                "    \"journey_map\":\"http://wcf.x10host.com/challenge1_6.png\",\n" +
                "    \"description\":\"\",\n" +
                "    \"title\": \"In The Kyrgyz Republic, Going From Unemployment To Dream Job\",\n" +
                "    \"subtitle\": \"Baisal, a graphic designer\",\n" +
                "    \"media\": \"photo:http://wcf.x10host.com/6.Jalal-Abad.png\",\n" +
                "    \"content\":\"From a young age growing up in the Kyrgyz Republic, Baisal was drawn to the arts. Given that Jalal-Abad is a region dependent on agriculture, there were few opportunities for him to engage with his passion. He received his degree in programming, but after struggling with unemployment, Baisal found an alternative and decided to apply to an artistic apprenticeship program offered in Jalal-Abad through the USAID-funded Demilgeluu Jashtar (Youth Initiative). The program is managed primarily by the Aga Khan Foundation\\u2019s Mountain Societies Development Support Program.\\r\\n\\r\\nThe training program, he says, was \\u201Creally eye-opening for me, because I did not know that there was a field where I could find a good job that used my love of art as well as my technical programming skills.\\u201D Now, at 19, Baisal has a leg up thanks to the training and his ability to combine his skills with his passion. He is working as a graphic designer.\\r\\n\\r\\nThis program aims to help the Kyrgyz Republic\\u2019s workforce, particularly youth, fulfill their potential by equipping them with highly sought-after job skills. \\r\\n\\r\\nRead Baisal\\u2019s story here: https:\\/\\/www.akfusa.org\\/our-stories\\/kyrgyz-republic-unemployment-to-dream-job\\/.\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"sequence\": 7,\n" +
                "    \"distance\": 10200000,\n" +
                "    \"name\": \"Gilgit, Pakistan\",\n" +
                "    \"flag_name\": \"Pakistan\",\n" +
                "    \"journey_map\":\"http://wcf.x10host.com/challenge1_7.png\",\n" +
                "    \"description\":\"\",\n" +
                "    \"title\": \"Enterprise Development And Employable Skills\",\n" +
                "    \"subtitle\": \"Chand, a tailor\",\n" +
                "    \"media\": \"photo:http://wcf.x10host.com/7.Gilgit.png\",\n" +
                "    \"content\":\"Chand Begam attended an enterprise training workshop organized by AKF. As a result, she gained the skills and knowledge needed to open her own professional dressmaking shop, which has allowed her to increase her productivity and income.\\r\\n\\r\\nSince 2011, the Aga Khan Development Network, of which the Aga Khan Foundation is a part, has supported 63,000 young people like Chand in northern Pakistan to develop the skills and confidence they need to become more employable and self-enterprising.\\r\\n\\r\\nLearn more about the Network\\u2019s efforts in Pakistan: https:\\/\\/www.akdn.org\\/where-we-work\\/south-asia\\/pakistan.\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"sequence\": 8,\n" +
                "    \"distance\": 11000000,\n" +
                "    \"name\": \"Khorog, Tajikistan\",\n" +
                "    \"flag_name\": \"Tajikistan\",\n" +
                "    \"journey_map\":\"http://wcf.x10host.com/challenge1_8.png\",\n" +
                "    \"description\":\"\",\n" +
                "    \"title\": \"Financial Inclusion\",\n" +
                "    \"subtitle\": \"Rukhshod, leader of a national micro-lending organization\",\n" +
                "    \"media\": \"photo:http://wcf.x10host.com/8.Khorog.png\",\n" +
                "    \"content\":\"In a country where the majority of women head households, not financial entities, Rukhshod\\u2019s story underscores the importance of women\\u2019s inclusion in economic development activities.\\r\\n\\r\\nHer journey into this leadership position began when she became the manager of the women\\u2019s committee of a small village organization. Rukhshod\\u2019s natural ability to engage community members to ensure all voices were heard gained her early respect and recognition among her peers. In 2007, when a Social Union for the Development of Village Organizations (SUDVO) was formed in her area, she was selected to lead, and thrived. \\r\\n \\r\\nAs a result, in 2011, Rukhshod was appointed director of MLO \\u2018\\u2019Rushdi Pomir.\\u201D Thanks to ESCoMIAD, a joint project of AKF and USAID, Rukhshod received training and capacity development needed to be successful in her role. Trainings included topics like business planning; financial statement analysis; and cash management. Her background in economics paved the way for her quick absorption of the material.\\r\\n \\r\\nRead Rukhshod\\u2019s story here:  \\r\\nhttps:\\/\\/www.akfusa.org\\/our-stories\\/rukhshods-story\\/.\"\n" +
                "  }\n" +
                "]\n"
                ;
    }
}