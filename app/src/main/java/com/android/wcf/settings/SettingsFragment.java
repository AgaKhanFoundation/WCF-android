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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.android.wcf.BuildConfig;
import com.android.wcf.R;
import com.android.wcf.application.WCFApplication;
import com.android.wcf.base.BaseFragment;
import com.android.wcf.helper.DistanceConverter;
import com.android.wcf.helper.SharedPreferencesUtil;
import com.android.wcf.model.Commitment;
import com.android.wcf.model.Team;
import com.android.wcf.network.WCFClient;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;


public class SettingsFragment extends BaseFragment implements SettingsMvp.View {

    private static final String TAG = SettingsFragment.class.getSimpleName();

    SettingsMvp.Host host;
    SettingsMvp.Presenter settingsPresenter;

    View participantProfileContainer;
    View participantSettingsContainer;
    View teamSettingsContainer;

    DecimalFormat numberFormatter = new DecimalFormat("#,###,###");

    boolean isTeamLead = false;
    Team team;

    SwitchCompat.OnCheckedChangeListener onCheckedChangeListener = new SwitchCompat.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.team_public_visibility_enabled:
                    View teamVisibilityContainer = teamSettingsContainer.findViewById(R.id.team_visibilty_container);
                    TextView teamVisibilityMessageTv = teamVisibilityContainer.findViewById(R.id.team_visibility_message);
                    if (isChecked) {
                        teamVisibilityMessageTv.setText(getString(R.string.settings_team_public_visibility_on_message));
                    }
                    else {
                        teamVisibilityMessageTv.setText(getString(R.string.settings_team_public_visibility_off_message));
                    }
                    settingsPresenter.updateTeamPublicVisibility(team.getId(), isChecked);
                    break;
            }
        }
    };

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
                    break;
                case R.id.navigate_to_akf_create_profile_label:
                    if (host != null) {
                        showCreateAKFProfile();
                    }
                    break;
                case R.id.team_view_team_icon:
                    showTeamMembershipDetail();
                    break;
                case R.id.btn_signout:
                    signout(false);
                    break;
                case R.id.leave_team_icon:
                    settingsPresenter.onShowLeaveTeamSelected();
                    break;
                case R.id.delete_account:
                    settingsPresenter.onDeleteAccountSelected();
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

        setupParticipantProfileView(view);
        setupParticipantSettingsView(view);
        setupTeamSettingsView(view);

        view.findViewById(R.id.btn_signout).setOnClickListener(onClickListener);
        view.findViewById(R.id.delete_account).setOnClickListener(onClickListener);

        TextView appVersionTv = view.findViewById(R.id.app_version);
        appVersionTv.setText(WCFApplication.instance.getAppVersion());
//        appVersionTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                switchServerForTestingTeam();
//            }
//        });

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
        setToolbarTitle(getString(R.string.settings_title), true);

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
        final TextView committedDistanceTv = participantSettingsContainer.findViewById(R.id.participant_committed_miles);
        int currentDistance = 0;
        try {
            currentDistance = NumberFormat.getNumberInstance().parse(committedDistanceTv.getText().toString()).intValue();
        } catch (Exception e) {
            currentDistance = 0;
        }
        settingsPresenter.onShowMilesCommitmentSelected(currentDistance, new EditTextDialogListener() {
            @Override
            public void onDialogDone(@NotNull String editedValue) {
                int newCommitmentDistance = Integer.parseInt(editedValue);
                if (newCommitmentDistance <= 0) {
                    return;
                }
                committedDistanceTv.setText(editedValue);

                int activeEventId = SharedPreferencesUtil.getMyActiveEventId();
                String participantId = SharedPreferencesUtil.getMyParticipantId();

                int newCommittedSteps = DistanceConverter.steps(newCommitmentDistance);
                Commitment commitment = getParticipant().getCommitment();
                int commitmentId = 0;
                if (commitment != null) {
                    commitmentId = commitment.getId();
                }

                if (commitmentId == 0) {
                    settingsPresenter.createParticipantCommitment(participantId, activeEventId, newCommittedSteps);
                }
                else {
                    settingsPresenter.updateParticipantCommitment(commitmentId, participantId, activeEventId, newCommittedSteps);
                }
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
            cacheParticipantTeam(null);
            host.restartHomeActivity();
        }
    }

    @Override
    public boolean isAccountDeletable() {
        if (isTeamLead) {
            Team team = getParticipantTeam();
            if (team != null) {
                List participants = team.getParticipants();
                if (participants != null && participants.size() > 1) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void cannotDeleteLeadAccountWithMembers() {
        showError(getString(R.string.delete_team_title), getString(R.string.team_lead_remove_members_first), null);
    }

    @Override
    public void confirmToDeleteAccount() {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.view_delete_account, null);

        Button leaveBtn = dialogView.findViewById(R.id.delete_account_button);
        Button cancelBtn = dialogView.findViewById(R.id.cancel_delete_account_button);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });
        leaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsPresenter.deleteParticipant(SharedPreferencesUtil.getMyParticipantId());
                dialogBuilder.dismiss();
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    @Override
    public void participantDeleted() {
        if (isTeamLead) {
            settingsPresenter.deleteLeaderTeam(SharedPreferencesUtil.getMyTeamId());
        }
        else {
            signout(true);
        }
    }

    @Override
    public void onParticipantDeleteError(Throwable error) {
        if (error instanceof IOException) {
            showNetworkErrorMessage(R.string.settings_title);
        }
        else {
            showError(getString(R.string.delete_account_label), error.getMessage(), null);
        }
    }

    @Override
    public void onParticipantLeaveFromTeamError(Throwable error, String participantId) {
        if (error instanceof IOException) {
            showNetworkErrorMessage(R.string.settings_title);
        }
        else {
            showError(getString(R.string.leave_team_label), error.getMessage(), null);
        }
    }

    @Override
    public void onTeamDeleteError(Throwable error) {
        if (error instanceof IOException) {
            showNetworkErrorMessage(R.string.settings_title);
        }
        else {
            showError(getString(R.string.delete_team), error.getMessage(), null);
        }
    }

    void updateInfoDisplay() {
        showParticipantInfo();
        TextView committedDistanceTv = participantSettingsContainer.findViewById(R.id.participant_committed_miles);
        String formattedDistance = numberFormatter.format(DistanceConverter.distance(SharedPreferencesUtil.getMyStepsCommitted()));
        committedDistanceTv.setText(formattedDistance);

        setupLeaveTeamClickListeners();
        setupTeamPublicVisibility();
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
        setupAKFProfileCreateClickListeners(participantSettingsContainer);
    }

    void setupConnectDeviceClickListeners(View parentView) {
        View container = parentView.findViewById(R.id.connect_device_container);
        View image = container.findViewById(R.id.navigate_to_connect_app_or_device);
        image.setOnClickListener(onClickListener);
        expandViewHitArea(image, container);
    }

    void setupAKFProfileCreateClickListeners(View parentView) {
        View container = parentView.findViewById(R.id.akf_create_profile_container);
        View image = container.findViewById(R.id.navigate_to_akf_create_profile_label);
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

    void setupTeamPublicVisibility() {
        View teamVisibilityContainer = teamSettingsContainer.findViewById(R.id.team_visibilty_container);
        SwitchCompat teamVisibiltySwitch = teamVisibilityContainer.findViewById(R.id.team_public_visibility_enabled);
        TextView teamVisibilityMessageTv = teamVisibilityContainer.findViewById(R.id.team_visibility_message);

        if (isTeamLead) {
            teamVisibiltySwitch.setEnabled(true);
            teamVisibiltySwitch.setOnCheckedChangeListener(onCheckedChangeListener);
        }else {
            teamVisibiltySwitch.setEnabled(false);
            teamVisibiltySwitch.setOnCheckedChangeListener(null);
        }
        if (team != null && !team.getVisibility()) {
            teamVisibiltySwitch.setChecked(false);
            teamVisibilityMessageTv.setText(getString(R.string.settings_team_public_visibility_off_message));
        }
        else {
            teamVisibiltySwitch.setChecked(true);
            teamVisibilityMessageTv.setText(getString(R.string.settings_team_public_visibility_on_message));
        }
    }

    @Override
    public void teamPublicVisibilityUpdateError(Throwable error) {
        View teamVisibilityContainer = teamSettingsContainer.findViewById(R.id.team_visibilty_container);
        SwitchCompat teamVisibiltySwitch = teamVisibilityContainer.findViewById(R.id.team_public_visibility_enabled);
        teamVisibiltySwitch.setOnCheckedChangeListener(null);
        teamVisibiltySwitch.setChecked(!teamVisibiltySwitch.isChecked());
        teamVisibiltySwitch.setOnCheckedChangeListener(onCheckedChangeListener);

        if (error instanceof IOException) {
            showNetworkErrorMessage(R.string.settings_title);
        }
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
            host.showTeamMembershipDetail(isTeamLead);
        }
    }

    public void showDeviceConnection() {
        host.showDeviceConnection();
    }

    public void showCreateAKFProfile() {
        host.showAKFProfileView();
    }

    public void setToolbarTitle(String title, boolean homeAffordance) {
        if (host != null) {
            host.setToolbarTitle(getString(R.string.settings_title), true);
        }
    }

    public void signout(boolean complete){
        if (host != null) {
            host.signout(complete);
        }
    }
}
