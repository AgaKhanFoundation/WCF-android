package com.android.wcf.home.dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.wcf.R;
import com.android.wcf.base.BaseFragment;
import com.android.wcf.fitbit.FitbitHelper;
import com.android.wcf.googlefit.GoogleFitHelper;
import com.android.wcf.helper.SharedPreferencesUtil;
import com.android.wcf.model.Participant;
import com.android.wcf.model.Team;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;


public class DashboardFragment extends BaseFragment implements DashboardMvp.DashboardView {
    private static final String TAG = DashboardFragment.class.getSimpleName();
    SharedPreferences deviceSharedPreferences = null;
    private DashboardMvp.Host mFragmentHost;

    View deviceConnectionView = null;
    View activityTrackedInfoView = null;
    View challengeProgressBeforeStart = null;
    View fundraisingBeforeChallengeStartView = null;

    ImageView participantImage;
    TextView participantNameTv;
    TextView teamNameTv;
    TextView teamLeadLabelTv;

    private DashboardMvp.Presenter dashboardPresenter = new DashboardPresenter(this);
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.navigate_to_connect_app_or_device:
                    if (mFragmentHost != null) {
                        mFragmentHost.showDeviceConnection();
                    }
                    break;

                case R.id.fundraising_invite_button:
                    showSupportersInvite();
                    break;


            }
        }
    };

    public DashboardFragment() {
    }

    public static DashboardFragment newInstance() {
        DashboardFragment fragment = new DashboardFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
        mFragmentHost.setToolbarTitle(getString(R.string.nav_dashboard), false);

        showParticipantInfo();
        showDashboardActivityInfo();
        showChallengeProgress();
        showFundRaisingInfo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View fragmentView = getView();
        setupDashboardParticipantProfileCard(fragmentView);
        setupDashboardActivityCard(fragmentView);
        setupDashboardChallengeProgressCard(fragmentView);
        setupDashboardFundraisingCard(fragmentView);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DashboardMvp.Host) {
            mFragmentHost = (DashboardMvp.Host) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement DashboardMvp.Host");
        }
        deviceSharedPreferences = getActivity().getSharedPreferences(FitbitHelper.FITBIT_SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFragmentHost = null;
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
        participantNameTv.setText(SharedPreferencesUtil.getUserFullName());
        if (team != null) {
            teamNameTv.setText(team.getName());
            boolean isTeamLead = team.isTeamLeader(SharedPreferencesUtil.getMyParticipantId());

            teamLeadLabelTv.setText(isTeamLead ?
                    getResources().getString(R.string.team_lead_label)
                    : getResources().getString(R.string.team_member_label));

            teamLeadLabelTv.setVisibility(View.VISIBLE);
        } else {
            teamLeadLabelTv.setVisibility(View.GONE);
        }
    }

    void showDashboardActivityInfo() {
        boolean teamFormationStarted = true;
        boolean fitnessDeviceLoggedIn = false;
        boolean fitnessAppLoggedIn = false;

        if (deviceSharedPreferences != null) {
            fitnessDeviceLoggedIn = deviceSharedPreferences.getBoolean(FitbitHelper.FITBIT_DEVICE_LOGGED_IN, false);
            fitnessAppLoggedIn = deviceSharedPreferences.getBoolean(GoogleFitHelper.GOOGLE_FIT_APP_LOGGED_IN, false);
        }
        if (fitnessDeviceLoggedIn || fitnessAppLoggedIn) {
            deviceConnectionView.setVisibility(View.GONE);
            activityTrackedInfoView.setVisibility(View.VISIBLE);
        } else {
            deviceConnectionView.setVisibility(View.VISIBLE);
            activityTrackedInfoView.setVisibility(View.GONE);
        }
    }

    void showChallengeProgress() {
        boolean challengeStarted = false;

    }

    void showFundRaisingInfo() {
        boolean challengeStarted = false;
        if (!challengeStarted) {
            fundraisingBeforeChallengeStartView.setVisibility(View.VISIBLE);
        } else {
            fundraisingBeforeChallengeStartView.setVisibility(View.GONE);
        }
    }

    void setupDashboardParticipantProfileCard(View fragmentView) {
        View profileCard = fragmentView.findViewById(R.id.dashboard_participant_profile_card);
        participantImage = profileCard.findViewById(R.id.participant_image);
        participantNameTv = profileCard.findViewById(R.id.participant_name);
        teamNameTv = profileCard.findViewById(R.id.team_name);
        teamLeadLabelTv = profileCard.findViewById(R.id.team_lead_label);
    }

    void setupDashboardActivityCard(View fragmentView) {
        View activityCard = fragmentView.findViewById(R.id.dashboard_activity_card);

        deviceConnectionView = activityCard.findViewById(R.id.dashboard_activity_device_connection);
        activityTrackedInfoView = activityCard.findViewById(R.id.dashboard_activity_tracked_info);

        Button connectNavButton = deviceConnectionView.findViewById(R.id.navigate_to_connect_app_or_device);
        connectNavButton.setOnClickListener(onClickListener);
    }

    void setupDashboardChallengeProgressCard(View fragmentView) {
        View challengeProgressCard = fragmentView.findViewById(R.id.dashboard_challenge_progress_card);
        challengeProgressBeforeStart = challengeProgressCard.findViewById(R.id.dashboard_challenge_progress_card_before);
    }

    void setupDashboardFundraisingCard(View fragmentView) {
        View fundraisingProgressCard = fragmentView.findViewById(R.id.dashboard_fundraising_progress_card);
        fundraisingBeforeChallengeStartView = fundraisingProgressCard.findViewById(R.id.fundraising_progress_card_before);

        View container = fundraisingProgressCard.findViewById(R.id.fundraising_invite_container);
        View image = container.findViewById(R.id.fundraising_invite_button);
        expandViewHitArea(image, container);
        image.setOnClickListener(onClickListener);

    }

    void showSupportersInvite() {
        mFragmentHost.showSupportersInvite();
    }
}