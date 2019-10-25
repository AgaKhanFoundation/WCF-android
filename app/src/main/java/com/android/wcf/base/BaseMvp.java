package com.android.wcf.base;

import android.view.View;

import androidx.annotation.StringRes;

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

        void showError(String title, String message, ErrorDialogCallback errorDialogCallback);

        void showError(String title, int messageId, ErrorDialogCallback errorDialogCallback);

        void showError(int titleId, String message, ErrorDialogCallback errorDialogCallback);

        void showError(int titleId, int messageId, ErrorDialogCallback errorDialogCallback);

        void showLoadingDialogFragment();

        void hideLoadingDialogFragment();

        View showLoadingView();

        void hideLoadingView();

        void closeKeyboard();

        void cacheEvent(Event event);

        Event getEvent();

        void cacheParticipant(Participant participant);

        Participant getParticipant();

        void cacheParticipantTeam(Team team);

        void updateTeamVisibilityInCache(boolean hidden);

        Team getParticipantTeam();

        void cacheTeamList(List<Team> teams);

        List<Team> getTeamList();

        void clearCachedParticipantTeam();

        void clearCachedEvent();

        void clearCachedParticipant();

        void clearCacheTeamList();

        boolean isAttached();

        boolean isNetworkConnected();

        void showNetworkErrorMessage(@StringRes int error_title_res_id);

    }

    interface Presenter {
        String getTag();

        void onStop();
    }

    interface Host {
        void showDeviceConnection();

        void setToolbarTitle(String title, boolean homeAffordance);

        void popBackStack(String tag);

    }
}
