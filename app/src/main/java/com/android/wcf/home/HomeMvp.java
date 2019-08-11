package com.android.wcf.home;

import androidx.annotation.StringRes;

import com.android.wcf.base.BaseMvp;
import com.android.wcf.model.Participant;

public interface HomeMvp {

    interface HomeView extends BaseMvp.BaseView {
        void showErrorAndCloseApp(@StringRes int messageRes);

        void onGetParticipant(Participant participant);

        void onGetParticipantNotFound();

        void onParticipantCreated(Participant participant);

        void onAssignedParticipantToEvent(String fbId, int eventId);

        void showCreateTeam();
    }

    interface HomePresenter {
        void getParticipant(String fbId);

        void createParticipant(String fbId);

        void updateParticipantEvent(String fbId, int eventId);
    }

}
