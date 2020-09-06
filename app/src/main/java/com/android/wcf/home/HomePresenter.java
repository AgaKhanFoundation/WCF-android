package com.android.wcf.home;

import androidx.annotation.NonNull;

import com.android.wcf.application.DataHolder;
import com.android.wcf.model.Commitment;
import com.android.wcf.model.Constants;
import com.android.wcf.model.Participant;
import com.android.wcf.model.Stats;

import java.io.IOException;

public class HomePresenter extends  BasePresenter implements HomeMvp.HomePresenter {
    private static final String TAG = HomePresenter.class.getSimpleName();

    private HomeMvp.HomeView homeView;

    Participant participant = null;
    Stats participantStats = null;

    public HomePresenter(HomeMvp.HomeView homeView) {
        this.homeView = homeView;

    }

    @Override
    public void confirmAKFProfile( boolean profileCreated) {

        if (Constants.getBypassAKFProfile()) {
            homeView.akfProfileCreationSkipped();
            return;
        }

        boolean profileRegistered = false;
        if (participant == null) {
            homeView.participantNotRetrieved();
            return;
        }

        profileRegistered = participant.getRegistered();

        if(profileRegistered) {
            homeView.akfProfileRegistered();
            return;
        }

        if (profileCreated) {
            super.updateParticipantProfileRegistered(participant.getParticipantId());
        }
        else {
            if (DataHolder.checkShowAkfProfile()) {
                homeView.hideLoadingProgressViewUnStack("confirmAKFProfile");
                homeView.showAKFProfileView();
            }
        }
    }

    protected void onUpdateParticipantProfileRegisteredSuccess(String participantId) {
        super.onUpdateParticipantProfileRegisteredSuccess(participantId);
        homeView.akfProfileRegistered();
    }

    protected void onUpdateParticipantProfileRegisteredError(Throwable error, String participantId) {
        super.onUpdateParticipantProfileRegisteredError(error, participantId);
        homeView.akfProfileRegistrationError(error, participantId);
    }

    @Override
    public void getParticipant(@NonNull String participantId) {
        homeView.showLoadingProgressView("getParticipant");
        super.getParticipant(participantId);
    }

    @Override
    protected void onGetParticipantSuccess(Participant participant) {
        super.onGetParticipantSuccess(participant);
        this.participant = participant;
        homeView.hideLoadingProgressView("onGetParticipantSuccess");
        homeView.onGetParticipant(participant);
    }

    @Override
    protected void onGetParticipantError(Throwable error) {
        super.onGetParticipantError(error);

        if (error instanceof IOException) {
            homeView.showNoNetworkMessage();
        }
        else {
            homeView.hideLoadingProgressView("onGetParticipantError onGetParticipantNotFound");
            homeView.onGetParticipantNotFound();
        }
    }

    @Override
    public void createParticipant(String participantId) {
        homeView.showLoadingProgressView("createParticipant id=" + participantId);
        super.createParticipant(participantId);
    }

    @Override
    protected void onCreateParticipantSuccess(Participant participant) {
        super.onCreateParticipantSuccess(participant);
        homeView.onParticipantCreated(participant);
        homeView.hideLoadingProgressView("onCreateParticipantSuccess");
    }

    @Override
    public void createParticipantCommitment(String participantId, int eventId, int commitmentSteps) {
        homeView.showLoadingProgressView("createParticipantCommitment");
        super.createParticipantCommitment(participantId, eventId, commitmentSteps);
         homeView.onAssignedParticipantToEvent(participantId, eventId);
    }

    @Override
    protected void onCreateParticipantCommitmentSuccess(String participantId, Commitment commitment) {
        super.onCreateParticipantCommitmentSuccess(participantId, commitment);
        homeView.onAssignedParticipantToEvent(participantId, commitment.getEventId());
        homeView.hideLoadingProgressView("onCreateParticipantCommitmentSuccess");
    }

    @Override
    public void updateParticipantCommitment(int commitmentId, String participantId, int eventId, int commitmentSteps) {
        homeView.showLoadingProgressView("updateParticipantCommitment");
        super.updateParticipantCommitment(commitmentId, participantId, eventId, commitmentSteps);
    }

    @Override
    protected void onUpdateParticipantCommitmentSuccess(String participantId, int eventId, int commitmentSteps) {
        super.onUpdateParticipantCommitmentSuccess(participantId, eventId, commitmentSteps);
        homeView.onAssignedParticipantToEvent(participantId, eventId);
        homeView.hideLoadingProgressView("onUpdateParticipantCommitmentSuccess");
    }
}
