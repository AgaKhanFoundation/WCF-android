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

public class HomePresenter implements HomeMvp.HomePresenter {
    private static final String TAG = HomePresenter.class.getSimpleName();

    private HomeMvp.HomeView homeView;
    private WCFClient wcfClient = WCFClient.getInstance();

    Participant mParticipant = null;
    Stats mParticipantStats = null;

    List<Team> mTeams = null;
    Team mTeam = null;
    Stats mTeamStats = null;

    public HomePresenter(HomeMvp.HomeView homeView) {
        this.homeView = homeView;

    }

    @Override
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
                        Log.d(TAG, "createTeam success");
                        getTeams();
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "createTeam() Error: " + error.getMessage());
                        homeView.showError(R.string.teams_manager_error, error.getMessage());
                    }
                });
    }

    @Override
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
                        Log.d(TAG, "getTeams success");
                        mTeams = teams;
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "getTeams() Error: " + error.getMessage());
                        homeView.showError(R.string.teams_manager_error, error.getMessage());
                    }
                });


    }

    @Override
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
                        Log.d(TAG, "getTeam success");
                        mTeam = team;
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "getTeam() Error: " + error.getMessage());
                        homeView.showError(R.string.teams_manager_error, error.getMessage());
                    }
                });

    }

    @Override
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
                        Log.d(TAG, "getTeamStats success");
                        mTeamStats = stats;
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "getTeamStats() Error: " + error.getMessage());
                        homeView.showError(R.string.teams_manager_error, error.getMessage());

                    }
                });
    }

    @Override
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
                        Log.d(TAG, "deleteTeam success=" + count);
                        getTeams();
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "deleteTeam(tId) Error: " + error.getMessage());
                        homeView.showError(R.string.teams_manager_error, error.getMessage());
                    }

                });
    }

    @Override
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
                        Log.d(TAG, "createParticipant success");
                        mParticipant = participant;
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "createParticipant() Error: " + error.getMessage());
                        homeView.showError(R.string.participants_manager_error, error.getMessage());

                    }
                });
    }

    @Override
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
                        Log.d(TAG, "getParticipant success");
                        mParticipant = participant;
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "getParticipant(fbid) Error: " + error.getMessage());
                        homeView.showError(R.string.participants_manager_error, error.getMessage());
                    }
                });
    }

    @Override
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
                        Log.d(TAG, "getParticipantStats success");
                        mParticipantStats = stats;
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "getParticipantStats Error: " + error.getMessage());
                        homeView.showError(R.string.participants_manager_error, error.getMessage());
                    }
                });
    }

    @Override
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
                        Log.d(TAG, "assignParticipantToTeam success: " + results.get(0));
                        getParticipant(fbid);
                        getTeam(teamId);
                    }


                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "assignParticipantToTeam(fbid, teamId) Error: " + error.getMessage());
                        homeView.showError(R.string.participants_manager_error, error.getMessage());
                    }
                });
    }

    @Override
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
                        Log.d(TAG, "deleteParticipant success=" + count);
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "deleteParticipant(pId) Error: " + error.getMessage());
                        homeView.showError(R.string.participants_manager_error, error.getMessage());
                    }

                });
    }

}
