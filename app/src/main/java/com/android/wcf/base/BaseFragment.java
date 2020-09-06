package com.android.wcf.base;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.app.ShareCompat;
import androidx.fragment.app.Fragment;

import com.android.wcf.R;
import com.android.wcf.helper.DistanceConverter;
import com.android.wcf.helper.SharedPreferencesUtil;
import com.android.wcf.model.Event;
import com.android.wcf.model.Milestone;
import com.android.wcf.model.Notification;
import com.android.wcf.model.Participant;
import com.android.wcf.model.Team;
import com.android.wcf.settings.EditTextDialogListener;

import java.text.SimpleDateFormat;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */



 abstract public class BaseFragment extends Fragment implements BaseMvp.BaseView {

    private BaseMvp.BaseView baseView;
    protected static String TAG = BaseFragment.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        TAG = getClass().getSimpleName();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void showNetworkErrorMessage(@StringRes int error_title_res_id) {
        showError(getString(error_title_res_id), getString(R.string.no_network_message), null);
    }

    @Override
    public void initializeLoadingProgressView(String logMarker) {
        Log.d(TAG, logMarker + " " + "initializeLoadingProgressView");
         baseView.initializeLoadingProgressView(logMarker);
    }

    @Override
    public void showLoadingProgressView(String logMarker) {
        Log.d(TAG, logMarker + " " + "showLoadingProgressView");
        baseView.showLoadingProgressView(logMarker);
    }

    @Override
    public void hideLoadingProgressView(String logMarker) {
        Log.d(TAG,  logMarker + " " + "hideLoadingProgressView");
        baseView.hideLoadingProgressView(logMarker);
    }

    @Override
    public boolean hideLoadingProgressViewUnStack(String logMarker) {
        Log.d(TAG, logMarker + " " + "hideLoadingProgressViewUnStack");
        return baseView.hideLoadingProgressViewUnStack(logMarker);
    }

//TODO: implement the proper dialogFragment for showing error messages

    @Override
    public void showError(int messageId) {
        baseView.showError(messageId);
    }


    @Override
    public void showMessage(String message) {
        baseView.showMessage(message);
    }

    @Override
    public void showError(Throwable error) {
        baseView.showError(error.getMessage());
    }

    @Override
    public void showError(String message) {
        baseView.showError(message);
    }

    @Override
    public void showError(String title, String message, final ErrorDialogCallback errorDialogCallback) {
        baseView.showError(title, message, errorDialogCallback);
    }

    @Override
    public void showError(String title, int messageId, final ErrorDialogCallback errorDialogCallback) {
        baseView.showError(title, messageId, errorDialogCallback);
    }

    @Override
    public void showError(int titleId, String message, final ErrorDialogCallback errorDialogCallback) {
        baseView.showError(titleId, message, errorDialogCallback);
    }

    @Override
    public void showError(int titleId, int messageId, ErrorDialogCallback errorDialogCallback) {
        baseView.showError(titleId, messageId, errorDialogCallback);
    }

    @Override
    public void cacheEvent(Event event) {
        baseView.cacheEvent(event);
    }

    @Override
    public Event getEvent() {
        return baseView.getEvent();
    }

    @Override
    public void cacheParticipant(Participant participant) {
        baseView.cacheParticipant(participant);
    }

    @Override
    public Participant getParticipant() {
        return baseView.getParticipant();
    }

    @Override
    public void cacheParticipantTeam(Team team) {
        baseView.cacheParticipantTeam(team);
    }

    @Override
    public Team getParticipantTeam() {
        return baseView.getParticipantTeam();
    }

    @Override
    public void cacheTeamList(List<Team> teams) {
        baseView.cacheTeamList(teams);
    }

    @Override
    public List<Team> getTeamList() {
        return baseView.getTeamList();
    }

    @Override
    public void cacheMilestones(List<Milestone> journeyMilestones) {
        baseView.cacheMilestones(journeyMilestones);
    }

    @Override
    public List<Milestone> getMilestones() {
        return baseView.getMilestones();
    }

    @Override
    public void clearMilestones() {
        baseView.clearMilestones();
    }

    @Override
    public void clearCachedParticipantTeam() {
        baseView.clearCachedParticipantTeam();
    }

    @Override
    public void clearCachedEvent() {
        baseView.clearCachedEvent();
    }

    @Override
    public void clearCachedParticipant() {
        baseView.clearCachedParticipant();
    }

    @Override
    public void clearCacheTeamList() {
        baseView.clearCacheTeamList();
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
    public boolean isAttached() {
        return !isDetached() && getContext() != null && isAdded();
    }

//    @Override
//        public void closeKeyboard() {
//        View view = getView();
//        if (view != null) {
//            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(view.getRootView().getWindowToken(), 0);
//        }
//    }

//    @Override
//    public void closeKeyboard() {
//        Activity activity = getActivity();
//
//        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
//        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
//    }

    @Override
    public void closeKeyboard() {
        Activity activity = getActivity();
        if (activity == null) return;

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        boolean closed = imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        if (!closed) {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
    }

    @Override
    public boolean isNetworkConnected() {
        return baseView.isNetworkConnected();
    }

    public void inviteTeamMembers() {

        String teamName = getParticipantTeam().getName();
        String eventName = getEvent().getName();

        String shareMessage = getString(R.string.invite_team_member_template, teamName, eventName);

        try {
            ShareCompat.IntentBuilder intentBuilder = ShareCompat.IntentBuilder.from(getActivity());
            Intent shareIntent = intentBuilder
                    .setType("text/plain")
                    .setText(shareMessage)
                    .setSubject("Join my team '" + teamName + "' on Steps 4 Impact")
                    .setChooserTitle("Share Via")
                    .createChooserIntent();

            if (shareIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(shareIntent);
            }
        } catch (Exception e) {
            Log.e(getTag(), "Team invitation share error: " + e.getMessage());
        }
    }

    public void inviteSupporters() {

        Event event = getEvent();
        Participant participant = getParticipant();
        int milesCommitted = 0;

        if (event == null || participant == null) {
            //TODO: show message that committed mile have to valid
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d");
        String startDate = sdf.format(event.getStartDate());
        String endDate = sdf.format(event.getEndDate());
        String eventDescription = event.getDescription();
        milesCommitted = (int) DistanceConverter.distance(participant.getCommittedSteps());
        int days = event.getDaysInChallenge();

        String shareMessage = getString(R.string.invite_supporter_template_3, startDate, endDate, eventDescription, milesCommitted, days);

        try {
            ShareCompat.IntentBuilder intentBuilder = ShareCompat.IntentBuilder.from(getActivity());
            Intent shareIntent = intentBuilder
                    .setType("text/plain")
                    .setText(shareMessage)
                    .setSubject("Support my \"Steps\" 4 \"Impact\"")
                    .setChooserTitle("Share Via")
                    .createChooserIntent();

            if (shareIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(shareIntent);
            }
        } catch (Exception e) {
            Log.e(getTag(), "Supporter invitation share error: " + e.getMessage());
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

    public void showMilesEditDialog(int currentMiles, final EditTextDialogListener editTextDialogListener) {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.view_miles_entry, null);

        final EditText editText = dialogView.findViewById(R.id.participant_committed_miles);
        Button saveBtn = dialogView.findViewById(R.id.save);
        Button cancelBtn = dialogView.findViewById(R.id.cancel);

        editText.setText(currentMiles + "");
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();

                if (editTextDialogListener != null) {
                    editTextDialogListener.onDialogCancel();
                }
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newDistanceText = editText.getText().toString();
                int distance = Integer.parseInt(newDistanceText);
                int stepsCommitted = DistanceConverter.steps((distance));
                SharedPreferencesUtil.savetMyStepsCommitted(stepsCommitted);

                dialogBuilder.dismiss();

                if (editTextDialogListener != null) {
                    editTextDialogListener.onDialogDone(newDistanceText);
                }
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    @Override
    public void updateTeamVisibilityInCache(boolean hidden) {
        baseView.updateTeamVisibilityInCache(hidden);
    }

    protected int getUnreadNotificationsCount(List<Notification> notifications) {
        int count = 0;
        if (notifications != null && !notifications.isEmpty()) {
            for (Notification notification : notifications) {
                if (!notification.getReadFlag()) {
                    count++;
                }
            }
        }
        return count;
    }
}