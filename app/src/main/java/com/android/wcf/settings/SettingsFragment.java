package com.android.wcf.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.wcf.R;
import com.android.wcf.base.BaseFragment;
import com.android.wcf.helper.SharedPreferencesUtil;
import com.android.wcf.model.Participant;
import com.android.wcf.model.Team;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;


public class SettingsFragment extends BaseFragment implements SettingsMvp.View {

    private static final String TAG = SettingsFragment.class.getSimpleName();

    SettingsMvp.Host host;
    SettingsMvp.Presenter settingsPresenter;

    TextView particpantMiles;
    ImageView participantImage;
    TextView participantNameTv;
    TextView teamNameTv;
    TextView teamLeadLabelTv;

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.participant_miles_setting:
                    settingsPresenter.onShowMilesCommitmentSelected();
                    break;
                case R.id.navigate_to_connect_app_or_device:
                    if (host != null) {
                        host.showDeviceConnection();
                    }
                case R.id.team_view_team_icon:
                    break;
                case R.id.btn_signout:
                    if (host != null) {
                        host.signout();
                    }
                    break;
                case R.id.leave_team_icon:
                    settingsPresenter.onShowLeaveTeamSelected();
                    break;

            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_settings, container, false);
        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (host != null) {
            host.setToolbarTitle(getString(R.string.settings));
        }
        View fragmentView = getView();
        particpantMiles = fragmentView.findViewById(R.id.participant_miles);
        fragmentView.findViewById(R.id.participant_miles_setting).setOnClickListener(onClickListener);
        participantImage = fragmentView.findViewById(R.id.participant_image);
        participantNameTv = fragmentView.findViewById(R.id.participant_name);
        teamNameTv = fragmentView.findViewById(R.id.team_name);
        teamLeadLabelTv = fragmentView.findViewById(R.id.teamlead_label);

        setupConnectDeviceClickListeners(fragmentView);
        setupTeamSettingsClickListeners(fragmentView);
        setupLeaveTeamClickListeners(fragmentView);
        fragmentView.findViewById(R.id.btn_signout).setOnClickListener(onClickListener);

        showParticipantInfo();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        settingsPresenter = new SettingsPresenter(this);
    }

    @Nullable
    @Override
    public View getView() {
        return super.getView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SettingsMvp.Host) {
            this.host = (SettingsMvp.Host) context;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void confirmToLeaveTeam() {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.view_leave_team, null);

        Button leaveBtn = dialogView.findViewById(R.id.leave_team_button);
        Button cancelBtn = dialogView.findViewById(R.id.cancel_leave_team_button);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });
        leaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsPresenter.removeFromTeam(SharedPreferencesUtil.getMyParticipantId() );
                dialogBuilder.dismiss();
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    @Override
    public void participantRemovedFromTeam(String id) {

        String participantId = SharedPreferencesUtil.getMyParticipantId();
        if (id.equals(participantId)) { //I removed myself from team
            SharedPreferencesUtil.clearMyTeamId();
            setParticipantTeam(null);
            host.restartHomeActivity();
        }
        else {  //team lead removed a member, so refresh view

        }
    }

    @Override
    public void showMilesEditDialog() {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.view_miles_entry, null);

        final EditText editText = dialogView.findViewById(R.id.participant_miles);
        Button saveBtn = dialogView.findViewById(R.id.save);
        Button cancelBtn = dialogView.findViewById(R.id.cancel);

        editText.setText(particpantMiles.getText());
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                particpantMiles.setText(editText.getText());
                dialogBuilder.dismiss();
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    void showParticipantInfo() {

        String profileImageUrl = SharedPreferencesUtil.getUserProfilePhotoUrl();
        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            Log.d(TAG, "profileImageUrl=" + profileImageUrl);

            Glide.with(getContext())
                    .load(profileImageUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(participantImage);
        }

        Team team = getParticipantTeam();
        Participant participant = getParticipant();
        participantNameTv.setText(SharedPreferencesUtil.getUserFullName());
        boolean teamLead = false;
        if (team != null) {
            teamNameTv.setText(team.getName());
            if (team.getLeaderName().equalsIgnoreCase(SharedPreferencesUtil.getUserFullName())) {
                teamLead = true;
            }
            teamLeadLabelTv.setText(teamLead ?
                    getResources().getString(R.string.team_lead_label)
                    : getResources().getString(R.string.team_member_label));

            teamLeadLabelTv.setVisibility(View.VISIBLE);
        }
        else {
            teamLeadLabelTv.setVisibility(View.GONE);
        }
    }

    void setupConnectDeviceClickListeners(View parentView) {
        View container = parentView.findViewById(R.id.connect_device_container);
        View image = container.findViewById(R.id.navigate_to_connect_app_or_device);
        image.setOnClickListener(onClickListener);
        expandViewHitArea(image, container);
    }

    void setupTeamSettingsClickListeners(View parentView) {
        View container = parentView.findViewById(R.id.view_team_container);
        View image = container.findViewById(R.id.team_view_team_icon);
        image.setOnClickListener(onClickListener);
        expandViewHitArea(image, container);
    }

    void setupLeaveTeamClickListeners(View parentView) {
        View container = parentView.findViewById(R.id.leave_team_container);
        View image = container.findViewById(R.id.leave_team_icon);
        image.setOnClickListener(onClickListener);
        expandViewHitArea(image, container);
    }

    void expandViewHitArea(final View childView, final View parentView) {
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
