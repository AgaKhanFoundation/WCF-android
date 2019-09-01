package com.android.wcf.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.wcf.BuildConfig;
import com.android.wcf.R;
import com.android.wcf.base.BaseFragment;
import com.android.wcf.helper.SharedPreferencesUtil;
import com.android.wcf.model.Team;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;


public class SettingsFragment extends BaseFragment implements SettingsMvp.View {

    private static final String TAG = SettingsFragment.class.getSimpleName();

    SettingsMvp.Host host;
    SettingsMvp.Presenter settingsPresenter;

    View participantProfileContainer;
    View participantSettingsContainer;
    View teamSettingsContainer;

    boolean isTeamLead = false;
    Team team;

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.participant_miles_setting:
                    editParticipantCommitment();
                    break;
                case R.id.navigate_to_connect_app_or_device:
                    if (host != null) {
                        showDeviceConnection();
                    }
                case R.id.team_view_team_icon:
                    showTeamMembershipDetail();
                    break;
                case R.id.btn_signout:
                    signout();
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
        setToolbarTitle(getString(R.string.settings), true);

        setupParticipantProfileView(view);
        setupParticipantSettingsView(view);
        setupTeamSettingsView(view);

        view.findViewById(R.id.btn_signout).setOnClickListener(onClickListener);

        TextView appVersionTv = view.findViewById(R.id.app_version);
        appVersionTv.setText("v" + BuildConfig.VERSION_NAME);
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

        isTeamLead = false;
        team = getParticipantTeam();
        if (team != null) {
            isTeamLead = team.isTeamLeader(SharedPreferencesUtil.getMyParticipantId());
        }

        updateInfoDisplay();
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

    public void editParticipantCommitment() {
        final TextView participantMiles = participantSettingsContainer.findViewById(R.id.participant_miles);
        int currentMiles = 0;
        try {
            currentMiles = NumberFormat.getNumberInstance().parse(participantMiles.getText().toString()).intValue();
        } catch (Exception e) {
            currentMiles = 0;
        }
        settingsPresenter.onShowMilesCommitmentSelected(currentMiles, new EditTextDialogListener() {
            @Override
            public void onDialogDone(@NotNull String editedValue) {
                participantMiles.setText(editedValue);
            }

            @Override
            public void onDialogCancel() {
            }
        });
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
                settingsPresenter.removeFromTeam(SharedPreferencesUtil.getMyParticipantId());
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
    }

    void updateInfoDisplay() {
        showParticipantInfo();
        TextView particpantMiles = participantSettingsContainer.findViewById(R.id.participant_miles);
        particpantMiles.setText(SharedPreferencesUtil.getMyMilesCommitted() + "");

        setupLeaveTeamClickListeners();
    }

    void showParticipantInfo() {
        ImageView participantImage = participantProfileContainer.findViewById(R.id.participant_image);
        TextView participantNameTv = participantProfileContainer.findViewById(R.id.participant_name);
        TextView teamNameTv = participantProfileContainer.findViewById(R.id.team_name);
        TextView teamLeadLabelTv = participantProfileContainer.findViewById(R.id.team_lead_label);

        String profileImageUrl = SharedPreferencesUtil.getUserProfilePhotoUrl();
        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            Log.d(TAG, "profileImageUrl=" + profileImageUrl);

            Glide.with(getContext())
                    .load(profileImageUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(participantImage);
        }

        participantNameTv.setText(SharedPreferencesUtil.getUserFullName());
        if (team != null) {
            teamNameTv.setText(team.getName());
            teamLeadLabelTv.setText(isTeamLead ?
                    getResources().getString(R.string.team_lead_label)
                    : getResources().getString(R.string.team_member_label));

            teamLeadLabelTv.setVisibility(View.VISIBLE);
        } else {
            teamLeadLabelTv.setVisibility(View.GONE);
        }
    }

    void setupParticipantProfileView(View parentView) {
        participantProfileContainer = parentView.findViewById(R.id.participant_profile_container);
    }

    void setupParticipantSettingsView(View parentView) {
        participantSettingsContainer = parentView.findViewById(R.id.participant_settings_container);
        participantSettingsContainer.findViewById(R.id.participant_miles_setting).setOnClickListener(onClickListener);
        setupConnectDeviceClickListeners(participantSettingsContainer);
    }

    void setupConnectDeviceClickListeners(View parentView) {
        View container = parentView.findViewById(R.id.connect_device_container);
        View image = container.findViewById(R.id.navigate_to_connect_app_or_device);
        image.setOnClickListener(onClickListener);
        expandViewHitArea(image, container);
    }

    void setupTeamSettingsView(View parentView) {
        teamSettingsContainer = parentView.findViewById(R.id.team_settings_container);
        setupTeamSettingsClickListeners(teamSettingsContainer);
    }

    void setupTeamSettingsClickListeners(View parentView) {
        View container = parentView.findViewById(R.id.view_team_container);
        View image = container.findViewById(R.id.team_view_team_icon);
        image.setOnClickListener(onClickListener);
        expandViewHitArea(image, container);
    }

    void setupLeaveTeamClickListeners() {
        View leaveTeamContainer = teamSettingsContainer.findViewById(R.id.leave_team_container);
        View leaveLabel = leaveTeamContainer.findViewById(R.id.leave_team_label);
        View image = leaveTeamContainer.findViewById(R.id.leave_team_icon);

        boolean leaveEnabled = false;
        int teamId = SharedPreferencesUtil.getMyTeamId();
        if (teamId > 0 && team != null && !isTeamLead) {
            leaveEnabled = true;
            image.setOnClickListener(onClickListener);
            expandViewHitArea(image, leaveTeamContainer);
        } else {
            image.setOnClickListener(null);
        }
        image.setEnabled(leaveEnabled);
        leaveLabel.setEnabled(leaveEnabled);
        leaveTeamContainer.setEnabled(leaveEnabled);
        leaveTeamContainer.setVisibility(leaveEnabled ? View.VISIBLE : View.GONE);

        View teamVisibiltyContainer = teamSettingsContainer.findViewById(R.id.team_visibilty_container);
        teamVisibiltyContainer.setVisibility(isTeamLead ? View.VISIBLE : View.GONE);
    }

    public void showTeamMembershipDetail(){
        Log.d(TAG, "showTeamMembershipDetail");
        if (team != null) {
            host.showTeamMembershipDetail();
        }
    }

    public void showDeviceConnection() {
        host.showDeviceConnection();
    }

    public void setToolbarTitle(String title, boolean homeAffordance) {
        if (host != null) {
            host.setToolbarTitle(getString(R.string.settings), true);
        }
    }

    public void signout(){
        if (host != null) {
            host.signout();
        }
    }
}
