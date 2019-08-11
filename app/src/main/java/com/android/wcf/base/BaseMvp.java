package com.android.wcf.base;

import android.view.View;

import com.android.wcf.model.Event;
import com.android.wcf.model.Participant;
import com.android.wcf.model.Team;

import java.util.List;

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

        void setEvent(Event event);

        Event getEvent();

        void setParticipant(Participant participant);

        Participant getParticipant();

        void setParticipantTeam(Team team);

        Team getParticipantTeam();

        void setTeamList(List<Team> teams);

        List<Team> getTeamList();
    }

    interface Presenter {
        String getTag();
    }

    interface Host {
        void showDeviceConnection();

        void showToolbarUpAffordance(boolean showFlag);

        void setViewTitle(String title);

        void setToolbarTitle(String title);

    }
}
