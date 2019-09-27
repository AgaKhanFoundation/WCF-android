package com.android.wcf.home;

import android.util.Log;

import com.android.wcf.base.BaseMvp;
import com.android.wcf.facebook.FacebookHelper;
import com.android.wcf.home.leaderboard.LeaderboardTeam;
import com.android.wcf.model.Constants;
import com.android.wcf.model.Event;
import com.android.wcf.model.Participant;
import com.android.wcf.model.Record;
import com.android.wcf.model.Source;
import com.android.wcf.model.Stats;
import com.android.wcf.model.Team;
import com.android.wcf.network.WCFClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public abstract class BasePresenter implements BaseMvp.Presenter {

    private static final String TAG = BasePresenter.class.getSimpleName();
    private WCFClient wcfClient = WCFClient.getInstance();

    CompositeDisposable disposables = new CompositeDisposable();

    @Override
    public String getTag() {
        return BasePresenter.class.getSimpleName();
    }

    @Override
    public void onStop() {
        disposables.clear();
    }

    /******* EVENT API ***********/

    public void getEventsList() {
        wcfClient.getEvents()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Event>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onSuccess(List<Event> events) {
                        onGetEventsListSuccess(events);
                    }

                    @Override
                    public void onError(Throwable error) {
                        onGetEventsListError(error);
                    }
                });
    }

    protected void onGetEventsListSuccess(List<Event> events) {
        Log.d(TAG, "onGetEventsListSuccess");
    }

    protected void onGetEventsListError(Throwable error) {
        Log.e(TAG, "onGetEventsListError: " + error.getMessage());
    }

    public void getEvent(int eventId) {
        wcfClient.getEvent(eventId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Event>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onSuccess(Event event) {
                        onGetEventSuccess(event);
                    }

                    @Override
                    public void onError(Throwable error) {
                        onGetEventError(error);
                    }
                });
    }

    protected void onGetEventSuccess(Event event) {
        Log.d(TAG, "onGetEventSuccess");
    }

    protected void onGetEventError(Throwable error) {
        Log.e(TAG, "onGetEventError: " + error.getMessage());
    }

    /******* PARTICIPANT API   ******/

    public void createParticipant(String participantId) {
        wcfClient.createParticipant(participantId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Participant>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onSuccess(Participant participant) {
                        onCreateParticipantSuccess(participant);
                    }

                    @Override
                    public void onError(Throwable error) {
                        onCreateParticipantError(error);
                    }
                });
    }

    protected void onCreateParticipantSuccess(Participant participant) {
        Log.d(TAG, "onCreateParticipantSuccess");

    }

    protected void onCreateParticipantError(Throwable error) {
        Log.e(TAG, "onCreateParticipantError: " + error.getMessage());
    }

    public void getParticipant(String participantId) {
        wcfClient.getParticipant(participantId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Participant>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onSuccess(Participant participant) {
                        FacebookHelper.getParticipantsInfoFromFacebook(participant, new FacebookHelper.OnFacebookProfileCallback() {
                            @Override
                            public void onParticipantProfileRetrieved(Participant participant) {
                                onGetParticipantSuccess(participant);
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable error) {
                        onGetParticipantError(error);
                    }
                });
    }

    protected void onGetParticipantSuccess(Participant participant) {
        Log.d(TAG, "onGetParticipantSuccess");

    }

    protected void onGetParticipantError(Throwable error) {
        Log.e(TAG, "onGetParticipantError (participantId) : " + error.getMessage());
    }


    public void getParticipantStats(String participantId) {
        wcfClient.getParticipantStat(participantId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Stats>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }
                    @Override
                    public void onSuccess(Stats stats) {
                        onGetParticipantStatsSuccess(stats);
                    }

                    @Override
                    public void onError(Throwable error) {
                        onGetParticipantStatsError(error);
                    }
                });
    }

    protected void onGetParticipantStatsSuccess(Stats stats) {
        Log.d(TAG, "onGetParticipantStatsSuccess success");
    }

    protected void onGetParticipantStatsError(Throwable error) {
        Log.e(TAG, "onGetParticipantStatsError Error: " + error.getMessage());
    }


    public void deleteParticipant(String participantId) {
        wcfClient.deleteParticipant(participantId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onSuccess(Integer count) {
                        onDeleteParticipantSuccess(count);
                    }

                    @Override
                    public void onError(Throwable error) {
                        onDeleteParticipantError(error);
                    }

                });
    }

    protected void onDeleteParticipantSuccess(Integer count) {
        Log.d(TAG, "onDeleteParticipantSuccess=" + count);

    }

    protected void onDeleteParticipantError(Throwable error) {
        Log.e(TAG, "onDeleteParticipantError Error: " + error.getMessage());

    }

    /******* TEAM API   ******/
    public void createTeam(String teamName, String teamLeadParticipantId, boolean teamVisibility) {
        wcfClient.createTeam(teamName, teamLeadParticipantId, teamVisibility)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Team>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onSuccess(Team team) {
                        onCreateTeamSuccess(team);
                    }

                    @Override
                    public void onError(Throwable error) {
                        if (error.getMessage().startsWith("HTTP 409")) {
                            onCreateTeamConstraintError();
                        } else {
                            onCreateTeamError(error);
                        }
                    }
                });
    }

    public void updateTeamPublicVisibility(int teamId, boolean isVisible) {
        wcfClient.updateTeamVisibility(teamId, isVisible)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onSuccess(Integer integer) {
                        onTeamPublicVisibilityUpdateSuccess();
                    }

                    @Override
                    public void onError(Throwable e) {
                        onTeamPublicVisibilityUpdateError(e);
                    }
                });
    }

    protected void onTeamPublicVisibilityUpdateSuccess() {
        Log.d(TAG, "onTeamPublicVisibilityUpdateSuccess");
    }

    protected void onTeamPublicVisibilityUpdateError(Throwable error) {
        Log.e(TAG, "onTeamPublicVisibilityUpdateError " + error.getMessage());
    }

    protected void onCreateTeamSuccess(Team team) {
        Log.d(TAG, "onCreateTeamSuccess");
    }

    protected void onCreateTeamError(Throwable error) {
        Log.e(TAG, "onCreateTeamError " + error.getMessage());
    }

    protected void onCreateTeamConstraintError() {
        Log.e(TAG, "onCreateTeamConstraintError");
    }

    public void getTeams() {
        wcfClient.getTeams()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Team>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }
                    @Override
                    public void onSuccess(List<Team> teams) {
                        onGetTeamListSuccess(teams);
                    }

                    @Override
                    public void onError(Throwable error) {
                        onGetTeamListError(error);
                    }
                });
    }

    protected void onGetTeamListSuccess(List<Team> teams) {
        Log.d(TAG, "onGetTeamListSuccess");
    }

    protected void onGetTeamListError(Throwable error) {
        Log.e(TAG, "onGetTeamListError: " + error.getMessage());
    }

    public void getTeam(int id) {
        wcfClient.getTeam(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Team>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onSuccess(Team team) {
                        onGetTeamSuccess(team);
                    }

                    @Override
                    public void onError(Throwable error) {
                        onGetTeamError(error);
                    }
                });

    }

    protected void onGetTeamSuccess(Team team) {
        Log.d(TAG, "onGetTeamSuccess");
    }

    protected void onGetTeamError(Throwable error) {
        Log.e(TAG, "onGetTeamError: " + error.getMessage());
    }
    /*
    public void getTeamParticipantsInfoFromFacebook(String myFbId, final Team team) {
        //https://graph.facebook.com/?ids=user1,user2,user3
         List<String> fbIdList = new ArrayList<>();
         for (Participant participant : team.getParticipants()) {
             fbIdList.add(participant.getFbId());
         }
        if (fbIdList.size() == 0) {
            onGetTeamParticipantsInfoError(new Error("No participants"));
        }
        else {

            GraphRequest request = GraphRequest.newGraphPathRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/" + myFbId + "/",
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {
                            // Insert your code here
                        }
                    });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id, name, email, picture");
            request.setParameters(parameters);
            request.executeAsync();

            GraphRequestBatch batch = new GraphRequestBatch(
                    GraphRequest.newMeRequest(
                            AccessToken.getCurrentAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(
                                        JSONObject jsonObject,
                                        GraphResponse response) {
                                    // Application code for user
                                }
                            }),
                    GraphRequest.newMyFriendsRequest(
                            AccessToken.getCurrentAccessToken(),
                            new GraphRequest.GraphJSONArrayCallback() {
                                @Override
                                public void onCompleted(
                                        JSONArray jsonArray,
                                        GraphResponse response) {
                                    // Application code for users friends
                                }
                            })
            );
            batch.addCallback(new GraphRequestBatch.Callback() {
                @Override
                public void onBatchCompleted(GraphRequestBatch graphRequests) {
                    // Application code for when the batch finishes
                }
            });
            batch.executeAsync();
        }

        onGetTeamParticipantsInfoSuccess(team);
        https://graph.facebook.com/?ids=user1,user2,user3
    }
    */


    public static int facebookRequestCount;

    public void getTeamParticipantsInfoFromFacebook(final Team team) {
        //https://graph.facebook.com/?ids=user1,user2,user3

        facebookRequestCount = team.getParticipants().size();
        Log.d(TAG, "getTeamParticipantsInfoFromFacebook: " + facebookRequestCount);
        List<String> fbIdList = new ArrayList<>();
        for (Participant participant : team.getParticipants()) {
            String fbId = participant.getFbId();
            fbIdList.add(fbId);
            FacebookHelper.getParticipantsInfoFromFacebook(participant, new FacebookHelper.OnFacebookProfileCallback() {
                @Override
                public void onParticipantProfileRetrieved(Participant participant) {
                    Log.d(TAG, "onParticipantProfileRetrieved: " + facebookRequestCount + " name=" + participant.getName());
                    --facebookRequestCount;
                    if (facebookRequestCount == 0) {
                        Log.d(TAG, "onParticipantProfileRetrieved facebookRequestCount = 0 onGetTeamParticipantsInfoSuccess");
                        onGetTeamParticipantsInfoSuccess(team);
                    }
                }
            });
        }
    }

    protected void onGetTeamParticipantsInfoSuccess(Team team) {

    }

    protected void onGetTeamParticipantsInfoError(Throwable error) {
        Log.e(TAG, "onGetTeamParticipantsInfoError: " + error.getMessage());
    }

    public void getLeaderboard() {
        wcfClient.getTeams()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Team>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onSuccess(List<Team> teams) {
                        List<LeaderboardTeam> leaderboard = extractTeamStats(teams);
                        onGetLeaderboardSuccess(leaderboard);
                    }

                    @Override
                    public void onError(Throwable error) {
                        onGetLeaderboardError(error);
                    }
                });
    }

    protected void onGetLeaderboardSuccess(List<LeaderboardTeam> teamsStats) {
        Log.d(TAG, "onGetLeaderboardSuccess");
    }

    protected void onGetLeaderboardError(Throwable error) {
        Log.e(TAG, "onGetLeaderboardError: " + error.getMessage());
    }

    protected List<LeaderboardTeam> extractTeamStats(List<Team> teams) {
        List<LeaderboardTeam> leaderboardTeamList = new ArrayList<>();
        for (Team team : teams) {
            LeaderboardTeam leaderboardTeam = extractTeamStats(team);
            if (leaderboardTeam != null) {
                leaderboardTeamList.add(leaderboardTeam);
            }
        }
        rankLeaderboard(leaderboardTeamList);

        return leaderboardTeamList;
    }

    public void rankLeaderboard(List<LeaderboardTeam> leaderboard) {
        Collections.sort(leaderboard, LeaderboardTeam.SORT_BY_STEPS_COMPLETED);
        for (int index = 0; index < leaderboard.size(); index++) {
            leaderboard.get(index).setRank(index + 1);
        }
    }

    protected LeaderboardTeam extractTeamStats(Team team) {
        if (team == null || team.getParticipants() == null) {
            return null;
        }

        int participantsCount = 0, rank = 0;

        int teamStepsPledged = 0, teamStepsCompleted = 0;
        double teamDistancePledged = 0.0, teamdistanceCompleted = 0.0;
        double teamAmountPledged = 0.0, teamAmountAccrued = 0.0;

        for (Participant participant : team.getParticipants()) {
            double participantDistancePledged = participant.getCommitmentDistance();
            if (participantDistancePledged == 0) {
                participantDistancePledged = Constants.PARTICIPANT_COMMITMENT_STEPS_DEFAULT;
            }
            int participantCompletedSteps = participant.getCompletedSteps();
            double participantCompletedDistance = participantCompletedSteps / Constants.STEPS_IN_A_MILE;

            double participantAvgSupportPledgePerUnitDistance = 0.0;  //TODO get the pledge avg rate for this participant from AKF
            teamAmountPledged += participantDistancePledged * participantAvgSupportPledgePerUnitDistance;
            teamAmountAccrued += participantCompletedDistance * participantAvgSupportPledgePerUnitDistance;

            teamStepsPledged += participantDistancePledged * Constants.STEPS_IN_A_MILE;
            teamStepsCompleted += participantCompletedSteps;

        }
        teamDistancePledged = Math.round(teamStepsPledged / Constants.STEPS_IN_A_MILE);
        teamdistanceCompleted = Math.round(teamStepsCompleted / Constants.STEPS_IN_A_MILE);

        // rank = (int) (Math.random() * 15);

        LeaderboardTeam leaderboardTeam = new LeaderboardTeam(
                rank
                , team.getId()
                , team.getName()
                , team.getImage()
                , team.getLeaderId()
                , team.getLeaderName()
                , participantsCount
                , teamStepsPledged
                , teamStepsCompleted
                , teamDistancePledged
                , teamdistanceCompleted
                , teamAmountPledged
                , teamAmountAccrued
        );
        return leaderboardTeam;
    }

    public void getTeamStats(int teamId) {
        wcfClient.getTeamStats(teamId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Stats>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onSuccess(Stats stats) {
                        onGetTeamStatsSuccess(stats);
                    }

                    @Override
                    public void onError(Throwable error) {
                        onGetTeamStatsError(error);
                    }
                });
    }

    protected void onGetTeamStatsSuccess(Stats stats) {
        Log.d(TAG, "onGetTeamStatsSuccess");
    }

    protected void onGetTeamStatsError(Throwable error) {
        Log.e(TAG, "onGetTeamStatsError: " + error.getMessage());

    }

    public void deleteTeam(int teamId) {
        wcfClient.deleteTeam(teamId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onSuccess(Integer count) {
                        onDeleteTeamSuccess(count);
                    }

                    @Override
                    public void onError(Throwable error) {
                        onDeleteTeamError(error);
                    }

                });
    }

    protected void onDeleteTeamSuccess(Integer count) {
        Log.d(TAG, "onDeleteTeamSuccess=" + count);
    }

    protected void onDeleteTeamError(Throwable error) {
        Log.e(TAG, "onDeleteTeamError: " + error.getMessage());
    }

    /***** PARTICPANT TO TEAM API ******/
    public void assignParticipantToTeam(final String participantId, final int teamId) {
        wcfClient.updateParticipant(participantId, null, teamId, 0, 0, 0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onSuccess(List<Integer> results) {
                        onAssignParticipantToTeamSuccess(results, participantId, teamId);
                    }

                    @Override
                    public void onError(Throwable error) {
                        onAssignParticipantToTeamError(error, participantId, teamId);
                    }
                });
    }

    protected void onAssignParticipantToTeamSuccess(List<Integer> results, String participantId, final int teamId) {
        Log.d(TAG, "assignParticipantToTeam success: " + results.get(0));

    }

    protected void onAssignParticipantToTeamError(Throwable error, String participantId, final int teamId) {
        Log.e(TAG, "assignParticipantToTeam(participantId, teamId) Error: " + error.getMessage());
    }

    public void participantLeaveFromTeam(final String participantId) {
        wcfClient.updateParticipantLeaveTeam(participantId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onSuccess(List<Integer> results) {
                        onParticipantLeaveFromTeamSuccess(results, participantId);
                    }

                    @Override
                    public void onError(Throwable error) {
                        onParticipantLeaveFromTeamError(error, participantId);
                    }
                });
    }

    protected void onParticipantLeaveFromTeamSuccess(List<Integer> results, String participantId) {
        Log.d(TAG, "onParticipantLeaveFromTeamSuccess success: " + results.get(0));
    }

    protected void onParticipantLeaveFromTeamError(Throwable error, String participantId) {
        Log.e(TAG, "onParticipantLeaveFromTeamError(participantId) Error: " + error.getMessage());
    }


    /***** PARTICPANT TO Event API ******/
    public void assignParticipantToEvent(final String participantId, final int eventId, final int causeId, final int localityId) {
        wcfClient.updateParticipant(participantId, null, 0, 0, 0, eventId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onSuccess(List<Integer> results) {
                        onAssignParticipantToEventSuccess(results, participantId, eventId, causeId, localityId);
                    }

                    @Override
                    public void onError(Throwable error) {
                        onAssignParticipantToEventError(error, participantId, eventId, causeId, localityId);
                    }
                });
    }

    protected void onAssignParticipantToEventSuccess(List<Integer> results, String participantId, final int eventId, final int causeId, final int localityId) {
        Log.d(TAG, "onAssignParticipantToEventSuccess success: " + results.get(0));

    }

    protected void onAssignParticipantToEventError(Throwable error, String participantId, final int eventId, final int causeId, final int localityId) {
        Log.e(TAG, "onAssignParticipantToEventError(participantId, eventId, causeId, localityId) Error: " + error.getMessage());
    }

    /***** tracking sources *****/


    public void getTrackingSources() {
        wcfClient.getTrackingSources()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Source>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onSuccess(List<Source> sources) {
                        onGetTrackingSourcesListSuccess(sources);
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "getTrackingSources: " + error.getMessage());

                        onGetTrackingSourcesListError(error);

                    }
                });
    }

    protected void onGetTrackingSourcesListSuccess(List<Source> sources) {
        Log.d(TAG, "onGetTrackingSourcesListSuccess success: ");

    }

    protected void onGetTrackingSourcesListError(Throwable error) {
        Log.e(TAG, "onGetTrackingSourcesListError(Error: " + error.getMessage());
    }

    /***** tracked steps *******/


    public void saveTrackedSteps(final int participantId, final int trackerSourceId, final String stepsDate, final long stepsCount) {
        wcfClient.recordSteps(participantId, trackerSourceId, stepsDate, stepsCount)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Record>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onSuccess(Record stepsRecord) {
                        onStepsRecordSuccess(stepsRecord, stepsDate);
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "saveTrackedSteps: " + error.getMessage());
                        onStepsRecordError(error, participantId, trackerSourceId, stepsDate, stepsCount);
                    }
                });
    }

    protected void onStepsRecordSuccess(Record stepsRecord, String stepDate) {
        Log.d(TAG, "onStepsRecordSuccess success: " + stepsRecord);
    }

    protected void onStepsRecordError(Throwable error, final int participantId, final int trackerSourceId, final String stepsDate, final long stepsCount) {
        Log.e(TAG, "onStepsRecordError(Error: " + error.getMessage()
                + " participantId=" + participantId
                + " sourceId=" + trackerSourceId
                + " stepsDate=" + stepsDate
                + " stepsCount=" + stepsCount
        );
    }
}
