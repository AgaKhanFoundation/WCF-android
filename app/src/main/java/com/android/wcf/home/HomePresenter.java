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

public class HomePresenter  implements HomeMvp.HomePresenter {
    private static final String TAG = HomePresenter.class.getSimpleName();

    private HomeMvp.HomeView homeView;

    Participant mParticipant = null;
    Stats mParticipantStats = null;

    List<Team> mTeams = null;
    Team mTeam = null;
    Stats mTeamStats = null;

    public HomePresenter(HomeMvp.HomeView homeView) {
        this.homeView = homeView;

    }




}
