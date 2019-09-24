package com.android.wcf.home;

import androidx.annotation.NonNull;

import com.android.wcf.model.Participant;
import com.android.wcf.model.Stats;

public class HomePresenter extends  BasePresenter implements HomeMvp.HomePresenter {
    private static final String TAG = HomePresenter.class.getSimpleName();

    private HomeMvp.HomeView homeView;

    Participant participant = null;
    Stats participantStats = null;

    public HomePresenter(HomeMvp.HomeView homeView) {
        this.homeView = homeView;

    }

    @Override
    public void getParticipant(@NonNull String participantId) {
        super.getParticipant(participantId);
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
    public void createParticipant(String participantId) {
        super.createParticipant(participantId);
    }

    @Override
    protected void onCreateParticipantSuccess(Participant participant) {
        super.onCreateParticipantSuccess(participant);
        homeView.onParticipantCreated(participant);
    }

    @Override
    public void updateParticipantEvent(String participantId, int eventId) {
        super.assignParticipantToEvent(participantId, eventId, 0, 0);
        homeView.onAssignedParticipantToEvent(participantId, eventId);

    }
}
