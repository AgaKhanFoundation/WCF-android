package com.android.wcf.home;

import android.util.Log;

import com.android.wcf.R;
import com.android.wcf.model.Participant;
import com.android.wcf.model.Stats;
import com.android.wcf.model.Team;
import com.android.wcf.network.WCFClient;

import java.util.List;

import androidx.annotation.NonNull;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HomePresenter extends  BasePresenter implements HomeMvp.HomePresenter {
    private static final String TAG = HomePresenter.class.getSimpleName();

    private HomeMvp.HomeView homeView;

    Participant participant = null;
    Stats participantStats = null;

    public HomePresenter(HomeMvp.HomeView homeView) {
        this.homeView = homeView;

    }

    @Override
    public void getParticipant(@NonNull String fbId) {
        super.getParticipant(fbId);
    }

    @Override
    protected void onGetParticipantSuccess(Participant participant) {
        super.onGetParticipantSuccess(participant);
        this.participant = participant;
        homeView.onGetParticipant(participant);
    }

    @Override
    protected void onGetParticipantError(Throwable error) {
        super.onGetParticipantError(error);
        //TODO: Rx/Retrofit global error handling
//        if ("Not Found".equalsIgnoreCase(error.getMessage())){
//            homeView.onGetParticipantNotFound();
//        }
        homeView.onGetParticipantNotFound();

    }

    @Override
    public void createParticipant(String fbId) {
        super.createParticipant(fbId);
    }

    @Override
    protected void onCreateParticipantSuccess(Participant participant) {
        super.onCreateParticipantSuccess(participant);
        homeView.onParticipantCreated(participant);
    }

    @Override
    public void updateParticipantEvent(String fbId, int eventId) {
        super.assignParticipantToEvent(fbId, eventId, 0, 0);

    }
}
