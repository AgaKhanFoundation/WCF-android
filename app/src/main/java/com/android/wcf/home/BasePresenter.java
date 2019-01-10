package com.android.wcf.home;

import android.util.Log;

import com.android.wcf.R;
import com.android.wcf.model.Participant;
import com.android.wcf.model.Stats;
import com.android.wcf.model.Team;
import com.android.wcf.network.WCFClient;

import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public abstract class BasePresenter {

    private static final String TAG = BasePresenter.class.getSimpleName();
    private WCFClient wcfClient = WCFClient.getInstance();

    /******* PARTICIPANT API   ******/

    public void createParticipant(String fbid) {
        wcfClient.createParticipant(fbid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Participant>() {
                    @Override
                    public void onSubscribe(Disposable d) {
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


    public void getParticipant(String fbid) {
        wcfClient.getParticipant(fbid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Participant>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

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
                .subscribe(new SingleObserver<Stats>() {
                    @Override
                    public void onSubscribe(Disposable d) {
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


    public void deleteParticipant(String fbid) {
        wcfClient.deleteParticipant(fbid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

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
    public void createTeam(String teamName) {
        wcfClient.createTeam(teamName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Team>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

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
                .subscribe(new SingleObserver<List<Team>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
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

    public void getTeamStats(int teamId) {
        wcfClient.getTeamStats(teamId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Stats>() {
                    @Override
                    public void onSubscribe(Disposable d) {
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
    public void assignParticipantToTeam(final String fbid, final int teamId) {
        wcfClient.updateParticipant(fbid, null, teamId, 0, 0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

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

}


