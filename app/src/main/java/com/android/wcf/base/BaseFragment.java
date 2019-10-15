package com.android.wcf.base;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
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

import androidx.core.app.ShareCompat;
import androidx.fragment.app.Fragment;

import com.android.wcf.R;
import com.android.wcf.helper.DistanceConverter;
import com.android.wcf.helper.SharedPreferencesUtil;
import com.android.wcf.model.Event;
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

    public BaseFragment() {
    }

    //TODO: implement the proper dialogFragment for showing error messages

    @Override
    public void showError(int messageId) {
        Toast.makeText(getContext(), getString(messageId), Toast.LENGTH_SHORT).show();
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

        final AlertDialog dialogBuilder = new AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.view_error_dialog, null);
        ((TextView) dialogView.findViewById(R.id.error_title)).setText(title);
        ((TextView) dialogView.findViewById(R.id.error_message)).setText(message);

        Button okBtn = dialogView.findViewById(R.id.ok_button);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
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

}
