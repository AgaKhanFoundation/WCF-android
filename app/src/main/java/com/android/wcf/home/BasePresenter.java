package com.android.wcf.home;

import android.util.Log;

import com.android.wcf.home.leaderboard.LeaderboardTeam;
import com.android.wcf.model.Constants;
import com.android.wcf.model.Event;
import com.android.wcf.model.Participant;
import com.android.wcf.model.Stats;
import com.android.wcf.model.Team;
import com.android.wcf.network.WCFClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


public abstract class BasePresenter {

    private static final String TAG = BasePresenter.class.getSimpleName();
    private WCFClient wcfClient = WCFClient.getInstance();

    /******* EVENT API ***********/

    public void getEventsList() {
        wcfClient.getEvents()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<List<Event>>() {
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
                .subscribe(new DisposableSingleObserver<Event>() {
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

    public void createParticipant(String fbid) {
        wcfClient.createParticipant(fbid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<Participant>() {
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

    public void getParticipant(String fbid) {
        wcfClient.getParticipant(fbid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<Participant>() {
                    @Override
                    public void onSuccess(Participant participant) {
                        onGetParticipantSuccess(participant);
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
        Log.e(TAG, "onGetParticipantError (fbid) : " + error.getMessage());
    }


    public void getParticipantStats(String fbid) {
        wcfClient.getParticipantStat(fbid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<Stats>() {
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


    public void deleteParticipant(String fbid) {
        wcfClient.deleteParticipant(fbid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<Integer>() {
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
    public void createTeam(String teamName) {
        wcfClient.createTeam(teamName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<Team>() {
                    @Override
                    public void onSuccess(Team team) {
                        onCreateTeamSuccess(team);
                    }

                    @Override
                    public void onError(Throwable error) {
                    }
                });
    }

    protected void onCreateTeamSuccess(Team team) {
        Log.d(TAG, "onCreateTeamSuccess");
    }

    protected void onCreateTeamError(Throwable error) {
        Log.e(TAG, "onCreateTeamError " + error.getMessage());

    }

    public void getTeams() {
        wcfClient.getTeams()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<List<Team>>() {
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
                .subscribe(new DisposableSingleObserver<Team>() {
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


    public void getLeaderboard() {
        wcfClient.getTeams()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<List<Team>>() {
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

        int participantsCount = 0, rank = 0, spotsAvailable = 0, stepsPledged = 0, stepsCompleted = 0, distancePledged = 0, distanceCompleted = 0;


        float amountPledged, amountAccrued;

        float avgPledgePer10kSteps;

        participantsCount = team.getParticipants().size();
        spotsAvailable = participantsCount < Constants.TEAM_MAX_SIZE ? Constants.TEAM_MAX_SIZE - participantsCount : 0;
        for (Participant participant : team.getParticipants()) {
            //TODO: compute appropriate steps to amount based on highest pledged support
        }
        stepsPledged = (int) (Math.random() * (100000 - 10000)) + 10000;
        stepsCompleted = (int) (stepsPledged * Math.random());
        distancePledged = Math.round(stepsPledged / Constants.STEPS_IN_A_MILE);
        distanceCompleted = Math.round(stepsCompleted / Constants.STEPS_IN_A_MILE);
        avgPledgePer10kSteps = ((int) (Math.random() * (1000 - 100)) + 100) / 100;
        amountPledged = (stepsPledged / Constants.STEPS_PER_UNIT_OF_DONATION) * avgPledgePer10kSteps;
        amountAccrued = (stepsCompleted / Constants.STEPS_PER_UNIT_OF_DONATION) * avgPledgePer10kSteps;

        rank = (int) (Math.random() * 15);

        LeaderboardTeam leaderboardTeam = new LeaderboardTeam(
                rank
                , team.getId()
                , team.getName()
                , team.getImage()
                , team.getLeaderName()
                , participantsCount
                , spotsAvailable
                , stepsPledged
                , stepsCompleted
                , distancePledged
                , distanceCompleted
                , amountPledged
                , amountAccrued
        );
        return leaderboardTeam;
    }

    public void getTeamStats(int teamId) {
        wcfClient.getTeamStats(teamId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<Stats>() {
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
                .subscribe(new DisposableSingleObserver<Integer>() {
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
    public void assignParticipantToTeam(final String fbid, final int teamId) {
            wcfClient.updateParticipant(fbid, null, teamId, 0, 0, 0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<List<Integer>>() {
                    @Override
                    public void onSuccess(List<Integer> results) {
                        onAssignParticipantToTeamSuccess(results, fbid, teamId);
                    }

                    @Override
                    public void onError(Throwable error) {
                        onAssignParticipantToTeamError(error, fbid, teamId);
                    }
                });
    }

    protected void onAssignParticipantToTeamSuccess(List<Integer> results, String fbid, final int teamId) {
        Log.d(TAG, "assignParticipantToTeam success: " + results.get(0));

    }

    protected void onAssignParticipantToTeamError(Throwable error, String fbid, final int teamId) {
        Log.e(TAG, "assignParticipantToTeam(fbid, teamId) Error: " + error.getMessage());
    }

    public void participantLeaveFromTeam(final String fbid) {
        wcfClient.updateParticipantLeaveTeam(fbid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<List<Integer>>() {
                    @Override
                    public void onSuccess(List<Integer> results) {
                        onParticipantLeaveFromTeamSuccess(results, fbid);
                    }

                    @Override
                    public void onError(Throwable error) {
                        onParticipantLeaveFromTeamError(error, fbid);
                    }
                });
    }

    protected void onParticipantLeaveFromTeamSuccess(List<Integer> results, String fbid) {
        Log.d(TAG, "onParticipantLeaveFromTeamSuccess success: " + results.get(0));
    }

    protected void onParticipantLeaveFromTeamError(Throwable error, String fbid) {
        Log.e(TAG, "onParticipantLeaveFromTeamError(fbid) Error: " + error.getMessage());
    }


    /***** PARTICPANT TO Event API ******/
    public void assignParticipantToEvent(final String fbid, final int eventId, final int causeId, final int localityId ) {
        wcfClient.updateParticipant(fbid, null, 0, 0, 0, eventId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<List<Integer>>() {
                    @Override
                    public void onSuccess(List<Integer> results) {
                        onAssignParticipantToEventSuccess(results, fbid, eventId, causeId, localityId);
                    }

                    @Override
                    public void onError(Throwable error) {
                        onAssignParticipantToEventError(error, fbid, eventId, causeId, localityId);
                    }
                });
    }

    protected void onAssignParticipantToEventSuccess(List<Integer> results, String fbid, final int eventId, final int causeId, final int localityId) {
        Log.d(TAG, "onAssignParticipantToEventSuccess success: " + results.get(0));

    }

    protected void onAssignParticipantToEventError(Throwable error, String fbid, final int eventId, final int causeId, final int localityId) {
        Log.e(TAG, "onAssignParticipantToEventError(fbid, eventId, causeId, localityId) Error: " + error.getMessage());
    }
}


