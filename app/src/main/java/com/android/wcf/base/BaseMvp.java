package com.android.wcf.base;

import android.view.View;

import com.android.wcf.model.Participant;
import com.android.wcf.model.Team;

public interface BaseMvp {
    interface BaseView {

        void showMessage(String message);

        void showError(Throwable error);

        void showError(int messageId);

        void showError(String message);

        void showError(String title, String message);

        void showError(String title, int messageId);

        void showError(int titleId, String message);

        void showError(int titleId, int messageId);

        void showLoadingDialogFragment();

        void hideLoadingDialogFragment();

        View showLoadingView();

        void hideLoadingView();

        void closeKeyboard();

        void setParticipant(Participant participant);
        Participant getParticipant();

        void setParticipantTeam(Team team);
        Team getParticipantTeam();
    }

    interface Presenter {
        String getTag();
    }

    interface Host {
    }
}
