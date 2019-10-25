package com.android.wcf.home;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.android.wcf.base.BaseMvp;
import com.android.wcf.model.Participant;


public interface HomeMvp {

    interface HomeView extends BaseMvp.BaseView {
        void showNoNetworkMessage();
        void noActiveEventFound();

        void showErrorAndCloseApp(@StringRes int messageRes);

        void onGetParticipant(Participant participant);

        void onGetParticipantNotFound();

        void onParticipantCreated(Participant participant);

        void onAssignedParticipantToEvent(String participantId, int eventId);

        void akfProfileCreationSkipped();

        void akfProfileRegistered();

        void showAKFProfileView();

        void akfProfileRegistrationError(Throwable error, String participantId);

        void participantNotRetrieved();

    }

    interface HomePresenter extends BaseMvp.Presenter {
        void getParticipant(String participantId);

        void participantLeaveFromTeam(String myParticpantId);

        void createParticipant(String participantId);

        void createParticipantCommitment(@NonNull String participantId, int eventId, int commitmentSteps);

        void updateParticipantCommitment(@NonNull int commitmentId, @NonNull String participantId, int eventId, int commitmentSteps);

        void confirmAKFProfile(boolean profileCreated);
    }

}
