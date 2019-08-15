package com.android.wcf.base;


import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.util.Log;
import android.view.TouchDelegate;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.core.app.ShareCompat;
import androidx.fragment.app.Fragment;

import com.android.wcf.BuildConfig;
import com.android.wcf.R;
import com.android.wcf.model.Event;
import com.android.wcf.model.Participant;
import com.android.wcf.model.Team;

import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
abstract public class BaseFragment extends Fragment implements BaseMvp.BaseView {

    private BaseMvp.BaseView baseView;

    public BaseFragment() {
    }

    //TODO: implement the proper dialogFragment for showing error messages

    @Override
    public void showError(int messageId) {
        Toast.makeText(getContext(), getString(messageId) , Toast.LENGTH_SHORT).show();
    }


    @Override
    public void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showError(Throwable error) {
        showMessage(error.getMessage());
    }

    @Override
    public void showError(String message) {
        showMessage(message);
    }

    @Override
    public void showError(String title, String message) {
        showMessage(message);
    }

    @Override
    public void showError(String title, int messageId) {
        Toast.makeText(getContext(), getString(messageId), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showError(int titleId, String message) {
        showMessage(message);
    }

    @Override
    public void showError(int titleId, int messageId) {
        Toast.makeText(getContext(), getString(messageId), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoadingDialogFragment() {

    }

    @Override
    public void hideLoadingDialogFragment() {

    }

    @Override
    public View showLoadingView() {
        return null;
    }

    @Override
    public void hideLoadingView() {

    }

    @Override
    public void setEvent(Event event) {
        baseView.setEvent(event);
    }

    @Override
    public Event getEvent() {
        return baseView.getEvent();
    }

    @Override
    public void setParticipant(Participant participant) {
        baseView.setParticipant(participant);
    }

    @Override
    public Participant getParticipant() {
        return baseView.getParticipant();
    }

    @Override
    public void setParticipantTeam(Team team) {
        baseView.setParticipantTeam(team);
    }

    @Override
    public Team getParticipantTeam() {
        return baseView.getParticipantTeam();
    }

    @Override
    public void setTeamList(List<Team> teams) {
        baseView.setTeamList(teams);
    }

    @Override
    public List<Team> getTeamList() {
        return baseView.getTeamList();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        baseView = (BaseMvp.BaseView) context;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void closeKeyboard() {
        View view = view = getView();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getRootView().getWindowToken(), 0);
        }
    }

    public void inviteTeamMembers() {

        String teamName = getParticipantTeam().getName();
        String eventName = getEvent().getName();
        String appLink = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
        String teamLink = getParticipantTeam().getName();

        String shareMessage = getString(R.string.invite_team_member_template, teamName, eventName, appLink);

        try {

            ShareCompat.IntentBuilder intentBuilder = ShareCompat.IntentBuilder.from(getActivity());
            Intent shareIntent = intentBuilder
                    .setType("text/plain")
                    .setText(shareMessage)
                    .setSubject("Join my team " + teamName + " on Steps4Change")
                    .setChooserTitle("Share Via")
                    .createChooserIntent();

            if (shareIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(shareIntent);
            }
        } catch (Exception e) {
            Log.e(getTag(), "Team invitation share error: " + e.getMessage());
        }
    }


    public void expandViewHitArea(final View childView, final View parentView) {
        parentView.post(new Runnable() {
            @Override
            public void run() {
                Rect parentRect = new Rect();
                Rect childRect = new Rect();
                parentView.getHitRect(parentRect);
                childView.getHitRect(childRect);
                childRect.left = parentRect.left;
                childRect.right = parentRect.width();

                parentView.setTouchDelegate(new TouchDelegate(childRect, childView));
            }
        });
    }
}
