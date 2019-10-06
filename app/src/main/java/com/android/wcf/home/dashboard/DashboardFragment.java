package com.android.wcf.home.dashboard;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.android.wcf.R;
import com.android.wcf.base.BaseFragment;
import com.android.wcf.helper.DateTimeHelper;
import com.android.wcf.helper.SharedPreferencesUtil;
import com.android.wcf.model.Event;
import com.android.wcf.model.Participant;
import com.android.wcf.model.Team;
import com.android.wcf.tracker.TrackerStepsCallback;
import com.android.wcf.tracker.TrackingHelper;
import com.android.wcf.tracker.fitbit.FitbitHelper;
import com.android.wcf.tracker.googlefit.GoogleFitHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fitbitsdk.service.models.ActivitySteps;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;


public class DashboardFragment extends BaseFragment implements DashboardMvp.DashboardView {
    private static final String TAG = DashboardFragment.class.getSimpleName();
    private DashboardMvp.Host mFragmentHost;

    ParticipantActivityFragment dailyFrag;
    ParticipantActivityFragment weeklyFrag;
    int trackerSourceId = 0;

    Event event = null;
    Team team = null;
    Participant participant = null;
    boolean isTeamLead = false;
    int openSlots = 0;

    boolean challengeStarted = false;
    boolean challengeEnded = false;
    boolean teamFormationStarted = true;
    boolean fitnessDeviceLoggedIn = false;
    boolean fitnessAppLoggedIn = false;

    View participantProfileView = null;
    View deviceConnectionView = null;
    View activityTrackedInfoView = null;
    View challengeProgressBeforeStartView = null;
    View challengeProgressView = null;
    View fundraisingBeforeChallengeStartView = null;
    View fundraisingView = null;

    ImageView participantImage;
    TextView participantNameTv;
    TextView teamNameTv;
    TextView challengeNameTv;
    TextView challengeDatesTv;

    TextView challengeDaysRemainingMessage = null;

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
                case R.id.view_badge_chevron:
                    showParticipantBadgesEarned();
                    break;

                case R.id.team_invite_chevron:
                    inviteTeamMembers();
                    break;

                case R.id.fundraising_invite_button:
                    showSupportersInvite();
                    break;

                case R.id.view_supporters_icon:
                    showSupportersList();
                    break;

            }
        }
    };

    private TrackerStepsCallback trackerStepsCallback = new TrackerStepsCallback() {
        @Override
        public void onTrackerStepsError(@NotNull Throwable error) {
            activityTrackedInfoView.setVisibility(View.GONE);

            Toast.makeText(getContext(), getString(R.string.tracker_needs_reconnection), Toast.LENGTH_LONG).show();
        }

        @Override
        public void trackerNeedsRelogin(int trackerId) {
            String title = "";
            if (trackerId == TrackingHelper.FITBIT_TRACKING_SOURCE_ID) {
                title = getString(R.string.tracker_connection_title_template, "Fitbit");
            } else if (trackerId == TrackingHelper.GOOGLE_FIT_TRACKING_SOURCE_ID) {
                title = getString(R.string.tracker_connection_title_template, "Google Fit App");
            }

           String message = getString(R.string.tracker_needs_reconnection);

           new AlertDialog.Builder(getContext())
                    .setTitle(title)
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                .show();
             Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onTrackerStepsRetrieved(@NotNull ActivitySteps data) {
            dailyFrag.setDaysInChallenge(event.getDaysInChallenge());
            dailyFrag.setDistanceGoal((int) participant.getCommitmentDistance());
            dailyFrag.setStepsData(data);

            weeklyFrag.setDaysInChallenge(event.getDaysInChallenge());
            weeklyFrag.setDistanceGoal((int) participant.getCommitmentDistance());
            weeklyFrag.setStepsData(data);

            activityTrackedInfoView.setVisibility(View.VISIBLE);

            if (TrackingHelper.isTimeToSave()) {
                String lastSavedDate = TrackingHelper.lastTrackerDataSavedDate();
                dashboardPresenter.saveStepsData(participant.getId(), trackerSourceId, data, event.getStartDate(), event.getEndDate(), lastSavedDate);
            }
        }
    };

    public static DashboardFragment newInstance() {
        DashboardFragment fragment = new DashboardFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        setupDashboardParticipantProfileCard(view);
        setupDashboardActivityCard(view);
        setupDashboardChallengeProgressCard(view);
        setupDashboardFundraisingCard(view);
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
        mFragmentHost.setToolbarTitle(getString(R.string.nav_dashboard), false);

        event = getEvent();
        team = getParticipantTeam();
        participant = getParticipant();

        if (event != null && team != null) {
            openSlots = event.getTeamLimit() - team.getParticipants().size();
            if (openSlots < 0) openSlots = 0;
        }

        if (team != null && participant != null) {
            isTeamLead = team.isTeamLeader(participant.getParticipantId());
        }

        challengeStarted = false;
        if (event != null) {
            challengeStarted = event.hasChallengeStarted();
            challengeEnded = event.hasChallengeEnded();
        }

        showParticipantInfo();
        showDashboardActivityInfo();
        showChallengeProgress();
        showFundRaisingInfo();
    }

    @Override
    public void onResume() {
        super.onResume();
        getTrackedStepsData();
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach");
        super.onAttach(context);
        if (context instanceof DashboardMvp.Host) {
            mFragmentHost = (DashboardMvp.Host) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement DashboardMvp.Host");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        dashboardPresenter.onStop();
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

        Event event = getEvent();
        Team team = getParticipantTeam();

        participantNameTv.setText(SharedPreferencesUtil.getUserFullName());
        if (team != null) {
            teamNameTv.setText(team.getName());
        }

        if (event != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy");
            String startDate = sdf.format(event.getStartDate());
            String endDate = sdf.format(event.getEndDate());

            challengeNameTv.setText(event.getName());
            challengeDatesTv.setText(startDate + " to " + endDate);
        }

        //TODO: add "Show badges"
    }

    void showDashboardActivityInfo() {

        fitnessDeviceLoggedIn = TrackingHelper.fitnessDeviceLoggedIn();
        fitnessAppLoggedIn = TrackingHelper.fitnessDeviceLoggedIn();

        if (fitnessDeviceLoggedIn) {
            trackerSourceId = TrackingHelper.FITBIT_TRACKING_SOURCE_ID;
        } else {
            trackerSourceId = TrackingHelper.GOOGLE_FIT_TRACKING_SOURCE_ID;
        }

        if (fitnessDeviceLoggedIn || fitnessAppLoggedIn) {
            activityTrackedInfoView.setVisibility(View.VISIBLE);
            deviceConnectionView.setVisibility(View.GONE);
        } else {
            deviceConnectionView.setVisibility(View.VISIBLE);
            activityTrackedInfoView.setVisibility(View.GONE);
        }
    }

    void getTrackedStepsData() {

        Date startDate = null;
        Date endDate = null;


        Date today = new Date();
        Date weekAgo = DateTimeHelper.dateWeekAgo();

        if (event != null) {
            if (event.getTeamBuildingStart() != null) {
                startDate = event.getTeamBuildingStart();
            } else {
                startDate = weekAgo;
            }

            if (event.getEndDate() != null) {
                if (event.hasChallengeEnded()) {
                    endDate = event.getEndDate();
                } else {
                    endDate = new Date();
                }
            } else {
                endDate = today;
            }
        }

        if (fitnessDeviceLoggedIn) {
            FitbitHelper.getSteps(getActivity(), startDate, endDate, trackerStepsCallback);
        } else if (fitnessAppLoggedIn) {
            GoogleFitHelper.getSteps(getActivity(), startDate, endDate, trackerStepsCallback);
        }
    }

    void showChallengeProgress() {
        if (!challengeStarted) {
            challengeProgressView.setVisibility(View.GONE);
            View teamInviteContainer = challengeProgressBeforeStartView.findViewById(R.id.challenge_team_invite_container);
            if (isTeamLead && openSlots > 0) {
                TextView teamLnviteLabel = teamInviteContainer.findViewById(R.id.team_invite_label);
                String openSlotMessage = getResources().getQuantityString(R.plurals.team_invite_more_members_message, openSlots, openSlots);
                teamLnviteLabel.setText(openSlotMessage);
                teamInviteContainer.setVisibility(View.VISIBLE);
            }
            else {
                teamInviteContainer.setVisibility(View.GONE);
            }
            challengeProgressBeforeStartView.setVisibility(View.VISIBLE);

        } else {
            int daysRemaining = event.daysToEndEvent();
            challengeDaysRemainingMessage.setText(getResources().getQuantityString(R.plurals.dashboard_challenge_remaining_days_template, daysRemaining, daysRemaining));
            challengeDaysRemainingMessage.setVisibility(View.VISIBLE);

            challengeProgressBeforeStartView.setVisibility(View.GONE);
            challengeProgressView.setVisibility(View.VISIBLE);
        }
    }

    void showFundRaisingInfo() {

        if (!challengeStarted) {
            fundraisingView.setVisibility(View.GONE);
            fundraisingBeforeChallengeStartView.setVisibility(View.VISIBLE);
        } else {
            fundraisingBeforeChallengeStartView.setVisibility(View.GONE);
            fundraisingView.setVisibility(View.VISIBLE);
        }
    }

    void setupDashboardParticipantProfileCard(View fragmentView) {
        View profileCard = fragmentView.findViewById(R.id.dashboard_participant_profile_card);
        participantProfileView = profileCard.findViewById(R.id.participant_profile_view);
        participantImage = participantProfileView.findViewById(R.id.participant_image);
        participantNameTv = participantProfileView.findViewById(R.id.participant_name);
        teamNameTv = participantProfileView.findViewById(R.id.team_name);

        challengeNameTv = participantProfileView.findViewById(R.id.challenge_name);
        challengeDatesTv = participantProfileView.findViewById(R.id.challenge_dates);

        View viewBadgesContainer = profileCard.findViewById(R.id.dashboard_badges_container);
        View viewBadgesImage = profileCard.findViewById(R.id.view_badge_chevron);
        expandViewHitArea(viewBadgesImage, viewBadgesContainer);
        viewBadgesImage.setOnClickListener(onClickListener);

    }

    void setupDashboardActivityCard(View fragmentView) {
        View activityCard = fragmentView.findViewById(R.id.dashboard_activity_card);
        setupDeviceConnectionView(activityCard);
        setupTrackedInfoView(activityCard);
    }

    void setupDeviceConnectionView(View activityCard) {
        deviceConnectionView = activityCard.findViewById(R.id.dashboard_activity_device_connection_view);
        Button connectNavButton = deviceConnectionView.findViewById(R.id.navigate_to_connect_app_or_device);
        connectNavButton.setOnClickListener(onClickListener);
    }

    void setupTrackedInfoView(View activityCard) {
        activityTrackedInfoView = activityCard.findViewById(R.id.dashboard_activity_tracked_info_view);
        TabLayout tabs = activityTrackedInfoView.findViewById(R.id.tracked_info_tabs);
        ViewPager viewPager = activityTrackedInfoView.findViewById(R.id.tracked_info_viewPager);

        TrackedInfoViewPagerAdapter adapter = new TrackedInfoViewPagerAdapter(getChildFragmentManager());
        dailyFrag = ParticipantActivityFragment.instanceDaily();
        weeklyFrag = ParticipantActivityFragment.instanceWeekly();

        adapter.addFragment(dailyFrag, getString(R.string.tracked_info_tab_daily_label));
        adapter.addFragment(weeklyFrag, getString(R.string.tracked_info_tab_weekly_label));

        viewPager.setAdapter(adapter);
        tabs.setupWithViewPager(viewPager);
    }

    void setupDashboardChallengeProgressCard(View fragmentView) {
        View challengeProgressCard = fragmentView.findViewById(R.id.dashboard_challenge_progress_card);
        challengeProgressBeforeStartView = challengeProgressCard.findViewById(R.id.dashboard_challenge_progress_before_view);
        challengeProgressView = challengeProgressCard.findViewById(R.id.dashboard_challenge_progress_view);
        challengeDaysRemainingMessage = challengeProgressView.findViewById(R.id.challenge_days_remaining_message);

        setupChallengeTeamInviteCard(challengeProgressBeforeStartView);
    }

    void setupChallengeTeamInviteCard(View parentView) {
        View container = parentView.findViewById(R.id.challenge_team_invite_container);
        View image = container.findViewById(R.id.team_invite_chevron);
        expandViewHitArea(image, container);
        image.setOnClickListener(onClickListener);
    }

    void setupDashboardFundraisingCard(View fragmentView) {
        View fundraisingProgressCard = fragmentView.findViewById(R.id.dashboard_fundraising_progress_card);
        fundraisingBeforeChallengeStartView = fundraisingProgressCard.findViewById(R.id.fundraising_progress_before_view);
        fundraisingView = fundraisingProgressCard.findViewById(R.id.fundraising_progress_view);

        View supporterListContainer = fundraisingProgressCard.findViewById(R.id.view_supporters_container);
        View supportersListimage = supporterListContainer.findViewById(R.id.view_supporters_icon);
        expandViewHitArea(supportersListimage, supporterListContainer);
        supportersListimage.setOnClickListener(onClickListener);

        View inviteContainer = fundraisingProgressCard.findViewById(R.id.fundraising_invite_container);
        View image = inviteContainer.findViewById(R.id.fundraising_invite_button);
        expandViewHitArea(image, inviteContainer);
        image.setOnClickListener(onClickListener);

    }

    void showParticipantBadgesEarned() {
        mFragmentHost.showParticipantBadgesEarned();

    }

    void showSupportersInvite() {
        mFragmentHost.showSupportersInvite();
    }

    void showSupportersList() {
        mFragmentHost.showSupportersList();
    }


    @Override
    public void stepsRecorded(String lastSavedDate) {
        TrackingHelper.trackerDataSaved(lastSavedDate);
        dashboardPresenter.getParticipantStats(participant.getParticipantId());
    }
}