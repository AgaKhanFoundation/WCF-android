package com.android.wcf.home;

import android.util.Log;

import com.android.wcf.application.DataHolder;
import com.android.wcf.base.BaseMvp;
import com.android.wcf.facebook.FacebookHelper;
import com.android.wcf.home.leaderboard.LeaderboardTeam;
import com.android.wcf.model.Commitment;
import com.android.wcf.model.Event;
import com.android.wcf.model.Milestone;
import com.android.wcf.model.Participant;
import com.android.wcf.model.Record;
import com.android.wcf.model.Source;
import com.android.wcf.model.Stats;
import com.android.wcf.model.Team;
import com.android.wcf.network.WCFClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    protected void getJourneyMilestones(int eventId) {
        wcfClient.getJourneyMilestones(eventId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Milestone>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onSuccess(List<Milestone> journeyMilestones) {
                        onGetJourneyMilestonesSuccess(journeyMilestones);
                    }

                    @Override
                    public void onError(Throwable error) {
                        onGetJourneyMilestoneError(error);
                    }
                });
    }

    protected void onGetJourneyMilestonesSuccess(List<Milestone> journeyMilestones) {
        Log.d(TAG, "onGetJourneyMilestonesSuccess");
    }

    protected void onGetJourneyMilestoneError(Throwable error) {
        Log.e(TAG, "onGetJourneyMilestoneError: " + error.getMessage());
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
                    public void onSuccess(final Participant participant) {
                        FacebookHelper.getParticipantsInfoFromFacebook(participant, new FacebookHelper.OnFacebookProfileCallback() {
                            @Override
                            public void onParticipantProfileRetrieved(Participant fbParticipant) {
                                if (participant.getFbId().equals(fbParticipant.getFbId())) {
                                    participant.setName(fbParticipant.getName());
                                    participant.setParticipantProfile(fbParticipant.getParticipantProfile());
                                }
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
        Log.d(TAG, "onGetParticipantStatsSuccess success: " + stats.toString());
    }

    protected void onGetParticipantStatsError(Throwable error) {
        Log.e(TAG, "onGetParticipantStatsError Error: " + error.getMessage());
    }


    public void deleteParticipant(String participantId) {
        wcfClient.deleteParticipant(participantId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onSuccess(List<Integer> result) {
                        int count = result != null ? result.get(0) : 0;
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
                        if (error.getMessage().startsWith("HTTP 409") || error.getMessage().startsWith("HTTP 400")) {
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
                .subscribe(new SingleObserver<List<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onSuccess(List<Integer> result) {
                        onTeamPublicVisibilityUpdateSuccess(result);
                    }

                    @Override
                    public void onError(Throwable e) {
                        onTeamPublicVisibilityUpdateError(e);
                    }
                });
    }

    protected void onTeamPublicVisibilityUpdateSuccess(List<Integer> result) {
        Log.d(TAG, "onTeamPublicVisibilityUpdateSuccess " + result);
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

    public void getTeamsList() {
        wcfClient.getTeamsList()
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

    public static int facebookRequestCount;
    public static Map<String, Participant> participantFacebookMap = new HashMap();

    public void getTeamParticipantsInfoFromFacebook(final Team team) {
        //https://graph.facebook.com/?ids=user1,user2,user3

        facebookRequestCount = team.getParticipants().size();
        Log.d(TAG, "getTeamParticipantsInfoFromFacebook: " + facebookRequestCount);
        List<String> fbIdList = new ArrayList<>();
        for (final Participant participant : team.getParticipants()) {
            String fbId = participant.getFbId();
            fbIdList.add(fbId);
            FacebookHelper.getParticipantsInfoFromFacebook(participant, new FacebookHelper.OnFacebookProfileCallback() {
                @Override
                public void onParticipantProfileRetrieved(Participant fbParticipant) {
                    Log.d(TAG, "onParticipantProfileRetrieved: " + facebookRequestCount + " name=" + fbParticipant.getName());
                    if (participant.getId() == fbParticipant.getId()) {
                        participant.setParticipantProfile(fbParticipant.getParticipantProfile());
                        participant.setName(fbParticipant.getName());
                        participantFacebookMap.put(participant.getFbId(), fbParticipant);
                    }
                    --facebookRequestCount;
                    if (facebookRequestCount == 0) {
                        Log.d(TAG, "onParticipantProfileRetrieved facebookRequestCount = 0 onGetTeamParticipantsInfoSuccess");
                        updateParticipantProfilesToTeam(team, participantFacebookMap);

                    }
                }
            });
        }
    }

    private void updateParticipantProfilesToTeam(Team team, Map<String, Participant> participantFacebookMap) {

//        for (Participant participant : DataHolder.getParticipantTeam().getParticipants()) {

        Participant cachedParticipant = DataHolder.getParticipant();

        for (Participant participant : team.getParticipants()) {
            Participant fbParticipant = participantFacebookMap.get(participant.getFbId());
            participant.setName(fbParticipant.getName());
            participant.setParticipantProfile(fbParticipant.getParticipantProfile());

            if (cachedParticipant != null && fbParticipant.getFbId().equals(cachedParticipant.getFbId())) {
                cachedParticipant.setName(fbParticipant.getName());
                cachedParticipant.setParticipantProfile(fbParticipant.getParticipantProfile());
            }
        }

        onGetTeamParticipantsInfoSuccess(team);

    }


    public static int participantsCount;
    public static Map<String, Commitment> participantCommitmentsMap = new HashMap();

    public void getTeamParticipantCommitments(final Team team, final int eventId) {

        participantsCount = team.getParticipants().size();
        participantCommitmentsMap.clear();
        Log.d(TAG, "getTeamParticipantCommitments: " + participantsCount);
        List<String> fbIdList = new ArrayList<>();
        for (Participant participant : team.getParticipants()) {
            String fbId = participant.getFbId();
            fbIdList.add(fbId);

            wcfClient.getParticipantCommitments(fbId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<List<Commitment>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            disposables.add(d);
                        }

                        @Override
                        public void onSuccess(List<Commitment> commitmentList) {
                            for (Commitment commitment : commitmentList) {
                                if (commitment.getEventId() == eventId) {
                                    Log.d(TAG, "getParticipantCommitments: " + participantsCount + " id=" + commitment.getParticipantId());
                                    participantCommitmentsMap.put(commitment.getParticipantId(), commitment);
                                    break;
                                }
                            }

                            participantsCount--;
                            if (participantsCount == 0) {
                                onGetTeamCommitmentSuccess(participantCommitmentsMap);
                            }
                        }

                        @Override
                        public void onError(Throwable error) {
                            Log.d(TAG, "getParticipantCommitments error: " + participantsCount + " error=" + error.getMessage());

                            participantsCount--;
                            if (participantsCount == 0) {
                                onGetTeamCommitmentSuccess(participantCommitmentsMap);
                            }
                        }
                    });
        }
    }

    protected void onGetTeamCommitmentSuccess(Map<String, Commitment> participantCommitmentsMap) {

    }


    protected void onGetTeamParticipantsInfoSuccess(Team team) {

    }

    protected void onGetTeamParticipantsInfoError(Throwable error) {
        Log.e(TAG, "onGetTeamParticipantsInfoError: " + error.getMessage());
    }

    public static int participantsProgressCount;
    public static Map<String, Stats> participantProgressMap = new HashMap();

    public void getTeamParticipantsChallengeProgress(final Team team, final int eventId) {

        participantsProgressCount = team.getParticipants().size();
        participantProgressMap.clear();
        Log.d(TAG, "getTeamParticipantsChallengeProgress: " + participantsProgressCount);
        List<String> fbIdList = new ArrayList<>();
        for (Participant participant : team.getParticipants()) {
            final String fbId = participant.getFbId();
            fbIdList.add(fbId);

            wcfClient.getParticipantStat(fbId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Stats>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            disposables.add(d);
                        }

                        @Override
                        public void onSuccess(Stats stats) {
                            Log.d(TAG, "getParticipantStat: " + participantsProgressCount + " " + stats.getDistance());
                            participantProgressMap.put(fbId, stats);

                            participantsProgressCount--;
                            if (participantsProgressCount == 0) {
                                onGetTeamChallengeProgressSuccess(participantProgressMap);
                            }
                        }

                        @Override
                        public void onError(Throwable error) {
                            Log.d(TAG, "getParticipantStat error: " + participantsProgressCount + " error=" + error.getMessage());

                            participantsProgressCount--;
                            if (participantsProgressCount == 0) {
                                onGetTeamChallengeProgressSuccess(participantProgressMap);
                            }
                        }
                    });
        }
    }

    protected void onGetTeamChallengeProgressSuccess(Map<String, Stats> teamChallengeProgress) {

    }

    public void getLeaderboard() {
        wcfClient.getTeamsList()
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
                        onGetLeaderboardLoadSuccess(leaderboard);
                    }

                    @Override
                    public void onError(Throwable error) {
                        onGetLeaderboardError(error);
                    }
                });
    }

    protected static int leaderboardTeamsCount;
    protected static Map<Integer, Stats> teamsStatsMap = new HashMap();

    protected void onGetLeaderboardLoadSuccess(final List<LeaderboardTeam> teams) {
        leaderboardTeamsCount = teams.size();
        teamsStatsMap.clear();
        Log.d(TAG, "onGetLeaderboardLoadSuccess: " + leaderboardTeamsCount);
        List<Integer> teamIdList = new ArrayList<>();
        for (LeaderboardTeam team: teams) {
            final int teamId = team.getId();
            teamIdList.add(teamId);

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
                            Log.d(TAG, "getTeamStat: " + leaderboardTeamsCount + " " + stats.getDistance());
                            teamsStatsMap.put(teamId, stats);

                            leaderboardTeamsCount--;
                            if (leaderboardTeamsCount == 0) {
                                onGetLeaderboardSuccess(teams, teamsStatsMap);
                            }
                        }

                        @Override
                        public void onError(Throwable error) {
                            Log.d(TAG, "getParticipantStat error: " + participantsProgressCount + " error=" + error.getMessage());

                            leaderboardTeamsCount--;
                            if (leaderboardTeamsCount == 0) {
                                onGetLeaderboardSuccess(teams, teamsStatsMap);
                            }
                        }
                    });
        }
    }

    protected void onGetLeaderboardSuccess(List<LeaderboardTeam> teams, Map<Integer, Stats> teamsStatsMap) {
        Log.d(TAG, "onGetLeaderboardSuccess");

        for (LeaderboardTeam team: teams) {
            Stats stats = teamsStatsMap.get(team.getId());
            if (stats != null) {
                team.setStepsCompleted(stats.getDistance());
            }
            else {
                team.setStepsCompleted(0);
            }
        }
        rankLeaderboard(teams);

        onGetLeaderboardSuccess(teams);
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
        Event event = DataHolder.getEvent();
        if (event == null) {
            return null;
        }

        int participantsCount = 0, rank = 0;

        int teamStepsCommitted = 0, teamStepsCompleted = 0;
        double teamAmountPledged = 0.0, teamAmountAccrued = 0.0;

//        for (Participant participant : team.getParticipants()) {
//            int participantStepsCommittedSteps = participant.getCommittedSteps();
//
//            double participantDistancePledged = (int) DistanceConverter.distance(participant.getCommittedSteps());
//            if (participantStepsCommittedSteps == 0) {
//                participantStepsCommittedSteps = event.getDefaultParticipantCommitment();
//            }
//            int participantCompletedSteps = participant.getCompletedSteps();
//            double participantCompletedDistance = (int) DistanceConverter.distance(participant.getCompletedSteps());
//
//            double participantAvgSupportPledgePerUnitDistance = 0.0;  //TODO get the pledge avg rate for this participant from AKF
//            teamAmountPledged += participantDistancePledged * participantAvgSupportPledgePerUnitDistance;
//            teamAmountAccrued += participantCompletedDistance * participantAvgSupportPledgePerUnitDistance;
//
//            teamStepsCommitted += participantStepsCommittedSteps;
//            teamStepsCompleted += participantCompletedSteps;
//
//        }

        // rank = (int) (Math.random() * 15);

        LeaderboardTeam leaderboardTeam = new LeaderboardTeam(
                rank
                , team.getId()
                , team.getName()
                , team.getImage()
                , team.getLeaderId()
                , team.getLeaderName()
                , participantsCount
                , teamStepsCommitted
                , teamStepsCompleted
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
                .subscribe(new SingleObserver<List<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onSuccess(List<Integer> count) {
                        onDeleteTeamSuccess(count);
                    }

                    @Override
                    public void onError(Throwable error) {
                        onDeleteTeamError(error);
                    }

                });
    }

    protected void onDeleteTeamSuccess(List<Integer> count) {
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

    public void updateTeamName(int teamId, final String teamName) {
        wcfClient.updateTeamName(teamId, teamName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onSuccess(List<Integer> results) {
                        onTeamNameUpdateSuccess(results, teamName);
                    }

                    @Override
                    public void onError(Throwable error) {
                        if (error.getMessage().startsWith("HTTP 409") || error.getMessage().startsWith("HTTP 400")) {
                            onTeamNameUpdateConstraintError(teamName);
                        } else {
                            onTeamNameUpdateError(error, teamName);
                        }
                    }
                });
    }

    protected void onTeamNameUpdateConstraintError(String teamName) {
        Log.e(TAG, "onTeamNameUpdateConstraintError: teamName=" +  teamName);
    }

    protected void onTeamNameUpdateSuccess(List<Integer> results, String teamName) {
        Log.d(TAG, "onTeamNameUpdateSuccess success: " + results.get(0));
    }

    protected void onTeamNameUpdateError(Throwable error, String teamName) {
        Log.e(TAG, "onTeamNameUpdateError(teamName) Error: " + error.getMessage());
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

    /***** PARTICIPANT COMMITMENTS API ******/

    public void createParticipantCommitment(final String participantId, final int eventId, final int commitmentSteps) {
        wcfClient.createParticipantCommitment(participantId, eventId, commitmentSteps)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Commitment>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onSuccess(Commitment commitment) {
                        onCreateParticipantCommitmentSuccess(participantId, commitment);
                    }

                    @Override
                    public void onError(Throwable error) {
                        onCreateParticipantCommitmentError(error, participantId, eventId, commitmentSteps);
                    }
                });
    }

    protected void onCreateParticipantCommitmentSuccess(String participantId, Commitment commitment) {
        Log.d(TAG, "onCreateParticipantCommitmentSuccess: participantId= " + participantId + " :" + commitment.toString());

    }

    protected void onCreateParticipantCommitmentError(Throwable error, String participantId, int eventId, int commitmentSteps) {
        //TODO force a refresh of Event.
        Log.e(TAG, "onCreateParticipantCommitmentError: " + error.getMessage() + "\n\tparticipantId= " + participantId +
                " eventId=" + eventId + " commitmentSteps=" + commitmentSteps);
    }

    public void updateParticipantCommitment(final int commitmentId, final String participantId, final int eventId, final int commitmentSteps) {
        wcfClient.updateParticipantCommitment(commitmentId, participantId, eventId, commitmentSteps)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onSuccess(List<Integer> results) {
                        onUpdateParticipantCommitmentSuccess(participantId, eventId, commitmentSteps);
                    }

                    @Override
                    public void onError(Throwable error) {
                        onUpdateParticipantCommitmentError(error, participantId, eventId, commitmentSteps);
                    }
                });
    }

    protected void onUpdateParticipantCommitmentSuccess(String participantId, int eventId, int commitmentSteps) {
        Log.d(TAG, "onUpdateParticipantCommitmentSuccess: participantId=" + participantId + " eventId=" + eventId + " commitmentSteps=" + commitmentSteps);
        DataHolder.updateParticipantCommitmentInCachedTeam( commitmentSteps);
        DataHolder.updateParticipantCommitment(commitmentSteps);
    }

    protected void onUpdateParticipantCommitmentError(Throwable error, String participantId, int eventId, int commitmentSteps) {
        Log.e(TAG, "onUpdateParticipantCommitmentError: " + error.getMessage() + "\n\tparticipantId=" + participantId + " eventId=" + eventId + " commitmentSteps=" + commitmentSteps);
    }

    /***** PARTICPANT TO Event API obsolete, we now use COMMITMENTS to join ******/
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


    public void updateParticipantProfileRegistered(final String participantId) {
        wcfClient.updateParticipantProfileRegistered( participantId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onSuccess(List<Integer> results) {
                        onUpdateParticipantProfileRegisteredSuccess(participantId);
                    }

                    @Override
                    public void onError(Throwable error) {
                        onUpdateParticipantProfileRegisteredError(error, participantId);
                    }
                });
    }

    protected void onUpdateParticipantProfileRegisteredSuccess(String participantId) {
        Log.d(TAG, "onUpdateParticipantProfileRegisteredSuccess: participantId=" + participantId );
    }

    protected void onUpdateParticipantProfileRegisteredError(Throwable error, String participantId) {
        Log.e(TAG, "onUpdateParticipantProfileRegisteredError: " + error.getMessage() + "\n\tparticipantId=" + participantId );
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
