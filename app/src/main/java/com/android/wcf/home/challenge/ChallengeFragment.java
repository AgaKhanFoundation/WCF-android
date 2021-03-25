package com.android.wcf.home.challenge;

import android.content.Context;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.android.wcf.R;
import com.android.wcf.application.WCFApplication;
import com.android.wcf.base.BaseFragment;
import com.android.wcf.base.ErrorDialogCallback;
import com.android.wcf.helper.DistanceConverter;
import com.android.wcf.helper.SharedPreferencesUtil;
import com.android.wcf.model.Commitment;
import com.android.wcf.model.Constants;
import com.android.wcf.model.Event;
import com.android.wcf.model.Milestone;
import com.android.wcf.model.Notification;
import com.android.wcf.model.Participant;
import com.android.wcf.model.Team;
import com.android.wcf.settings.EditTextDialogListener;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import static com.android.wcf.application.WCFApplication.isProdBackend;

public class ChallengeFragment extends BaseFragment implements ChallengeMvp.ChallengeView {

    private static final String TAG = ChallengeFragment.class.getSimpleName();

    // host for this fragment
    ChallengeMvp.Host mHostingParent;

    /* UI elements */

    private View mainContentView = null;
    private View beforeTeamContainer = null;
    private View afterTeamContainer = null;
    private View journeyCard = null;
    private View challengeTeamInviteCard = null;
    private View participantTeamSummaryCard = null;
    private Button showCreateTeamButton = null;
    private Button showJoinTeamButton = null;
    private View challengeFundraisingProgressCard = null;

    private ErrorDialogCallback errorDialogCallback = new ErrorDialogCallback() {
        @Override
        public void onOk() {
            retrieveViewData();
        }
    };

    //non-ui properties
    private ChallengeMvp.Presenter challengePresenter;

    DecimalFormat numberFormatter = new DecimalFormat("#,###,###");

    private String participantId;
    private int activeEventId;
    private int teamId;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.show_create_team_button:
                    challengePresenter.showCreateTeamView();
                    break;
                case R.id.show_join_team_button:
                    challengePresenter.showTeamsToJoinView();
                    break;
                case R.id.view_milestones_chevron:
                    showMilestonesInfo();
                    break;
                case R.id.team_invite_chevron:
                    inviteTeamMembers();
                    break;
                case R.id.fundraising_invite_button:
                    showSupportersInvite();
                    break;
                case R.id.participant_committed_miles_edit:
                    editParticipantCommitment();
                    break;
                case R.id.navigate_team_commitment_breakdown:
                    showTeamCommitmentBreakdown();
                    break;
            }
        }
    };

    public ChallengeFragment() {
    }

    public static ChallengeFragment newInstance() {
        ChallengeFragment fragment = new ChallengeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        challengePresenter = new ChallengePresenter(this);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View fragmentView = inflater.inflate(R.layout.fragment_challenge, container, false);

        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        setupView(view);
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
        participantId = SharedPreferencesUtil.getMyParticipantId();
        if(participantId == null || participantId.isEmpty()) {
            onParticipantIdMissing();
            return;
        }

        retrieveViewData();

    }

    private void retrieveViewData() {

        teamId = SharedPreferencesUtil.getMyTeamId();
        activeEventId = SharedPreferencesUtil.getMyActiveEventId();

        challengePresenter.getEvent(activeEventId);
        challengePresenter.getTeam(teamId);
        challengePresenter.getParticipant(participantId);
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        mHostingParent.setToolbarTitle(getString(R.string.nav_challenge), false);
        challengePresenter.getTeamsList(); //TODO: next version, we will have to associate teams to event
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
        challengePresenter.onStop();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.menu_home, menu);
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach");
        super.onAttach(context);
        if (context instanceof ChallengeMvp.Host) {
            mHostingParent = (ChallengeMvp.Host) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentHost");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mHostingParent = null;
    }

    @Override
    public void onGetEventError(Throwable error) {
        if (error instanceof IOException) {
            showNetworkErrorMessage(R.string.data_error);
        }
        else {
            SharedPreferencesUtil.cleartMyActiveEventId();
        }
    }

    @Override
    public void showNetworkErrorMessage() {
        showNetworkErrorMessage(R.string.data_error);
    }

    @Override
    public void onGetParticipantError(Throwable error) {
        if (error instanceof IOException) {
            showNetworkErrorMessage();
        }
        else {
            showError(R.string.participants_data_error, error.getMessage(), null);
        }
    }

    @Override
    public void onGetParticipantStatsError(Throwable error) {
        if (error instanceof IOException) {
            showNetworkErrorMessage(R.string.data_error);
        }
        else {
            showError(R.string.participants_data_error, error.getMessage(), null);
        }
    }

    @Override
    public void onDeleteParticipantError(Throwable error) {
        if (error instanceof IOException) {
            showNetworkErrorMessage(R.string.data_error);
        } else {
            showError(R.string.participants_data_error, error.getMessage(), null);
        }
    }

    @Override
    public void onGetTeamError(Throwable error) {
        showError(R.string.teams_data_error, error.getMessage(), null);
        cacheParticipantTeam(null);
        SharedPreferencesUtil.clearMyTeamId();
        teamId = SharedPreferencesUtil.getMyTeamId();
    }

    @Override
    public void onGetTeamListError(Throwable error) {
        if (error instanceof IOException) {
            showNetworkErrorMessage();
        } else {
            enableShowCreateTeam(true);
            enableJoinExistingTeam(false);
            showError(R.string.teams_data_error, error.getMessage(), null);
        }
    }

    @Override
    public void onGetTeamStatsError(Throwable error) {
        if (error instanceof IOException) {
            showNetworkErrorMessage();
        } else {
            showError(R.string.teams_data_error, error.getMessage(), null);
        }
    }

    @Override
    public void onDeleteTeamError(Throwable error) {

        if (error instanceof IOException) {
            showNetworkErrorMessage();
        } else {
            showError(R.string.teams_data_error, error.getMessage(), null);
        }
    }

    @Override
    public void showNetworkErrorMessage(@StringRes int error_title_res_id) {
        challengePresenter.onStop();
        showError(getString(error_title_res_id), getString(R.string.no_network_message), errorDialogCallback);
    }

    @Override
    public void noActiveEventFound() {
        mHostingParent.noActiveEventFound();
    }

    @Override
    public void onParticipantIdMissing() {
        Toast.makeText(getContext(), "Login Id needed. Please login", Toast.LENGTH_SHORT).show();
        WCFApplication.instance.requestLogin();
        getActivity().finish();
    }

    @Override
    public void showJourneyBeforeStartView(Event event) {
        if (journeyCard == null) {
            return;
        }

        View journeyBeforeStartView = journeyCard.findViewById(R.id.journey_before_start_view);
        if (journeyBeforeStartView != null) {

            if (event != null) {
                //update view data
                TextView journeyText = journeyBeforeStartView.findViewById(R.id.journey_text);
                int daysToStart = event.daysToStartEvent();
                int flags = DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR;
                String formattedStartDate = DateUtils.formatDateTime(getContext(), event.getStartDate().getTime(), flags);

                String msg;
                if (daysToStart > 0)
                    msg = getResources().getQuantityString(R.plurals.days_to_event_start, daysToStart, daysToStart, formattedStartDate);
                else
                    msg = getResources().getString(R.string.journey_starts_today);
                journeyText.setText(msg);
            }

            if (journeyBeforeStartView.getVisibility() != View.VISIBLE) {
                journeyBeforeStartView.setVisibility(View.VISIBLE);
            }
        }

        View journeyActiveView = journeyCard.findViewById(R.id.journey_active_view);
        if (journeyActiveView != null && journeyActiveView.getVisibility() != View.GONE) {
            journeyActiveView.setVisibility(View.GONE);
        }
    }

    @Override
    public void showJourneyActiveView(Event event) {
        if (journeyCard != null) {

            View journeyActiveView = journeyCard.findViewById(R.id.journey_active_view);
            if (journeyActiveView != null) {
                TextView journeyText = journeyActiveView.findViewById(R.id.journey_card_message);

                if (isProdBackend() && Constants.getChallengeStartSoonMessage()) {
                    journeyText.setText(R.string.message_journey_starting_soon);
                }
                else {
                    journeyText.setText(R.string.message_journey_started_initial);
                    challengePresenter.getJourneyMilestones(event.getId());
                }
                journeyActiveView.setVisibility(View.VISIBLE);
            }

            View journeyStartView = journeyCard.findViewById(R.id.journey_before_start_view);
            if (journeyStartView != null && journeyStartView.getVisibility() != View.GONE) {
                journeyStartView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void updateJourneyMilestones(List<Milestone> journeyMilestones) {
        if (journeyMilestones == null) {
            Log.d(TAG, "No Journey milestones returned");
            return;
        }

        //we have an extra milestone "start" to show the starting map. Remove it from counting of milestones total and milestonesCompleted
        int milestones = journeyMilestones != null ? journeyMilestones.size() - 1 : -1;
        int milestonesCompleted = 0;
        Team team = getParticipantTeam();
        if (team != null) {
            int teamStepsCompleted = team.geTotalParticipantCompletedSteps();
            for (Milestone milestone : journeyMilestones) {
                boolean reached = milestone.hasReached(teamStepsCompleted);
                if (reached) milestonesCompleted++;
            }
            milestonesCompleted--;
        }
        cacheMilestones(journeyMilestones);

        if (milestones > 0) {
            View journeyActiveView = journeyCard.findViewById(R.id.journey_active_view);
            ImageView journeyImage = journeyActiveView.findViewById(R.id.journey_card_icon_journey);
            String imageUrl = journeyMilestones.get(milestonesCompleted).getMapImage();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(getContext())
                        .load(imageUrl)
                        .into(journeyImage);
            }
            else {
                journeyImage.setImageResource(R.drawable.journey_map_placeholder);
            }
        }

        TextView journeyMessageTv = journeyCard.findViewById(R.id.journey_card_message);
        if (milestonesCompleted > 0 && milestones > 0) {
            journeyMessageTv.setText(getResources().getString(R.string.journey_milestones_completed, milestonesCompleted, milestones));
        } else if (milestones > 0) {
            journeyMessageTv.setText(getString(R.string.message_journey_started, milestones));
        } else {
            journeyMessageTv.setText(getString(R.string.message_no_journey_information));
        }
    }

    @Override
    public void onGetJourneyMilestoneError(Throwable error) {
        if (error instanceof IOException) {
            showNetworkErrorMessage(R.string.data_error);
        }
        //TODO: show error?
    }

    protected void showMilestonesInfo(){
        Log.d(TAG, "showMilestonesInfo");
        List<Milestone> milestonesList = getMilestones();
        if (milestonesList != null) {
            mHostingParent.showMilestones();
        }
    }

    @Override
    public void showParticipantTeamProfileView() {
        View teamProfileView = participantTeamSummaryCard.findViewById(R.id.challenge_participant_team_profile_view);
        if (teamProfileView.getVisibility() != View.VISIBLE){
            teamProfileView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showParticipantTeamSummaryCard(Team participantTeam) {
        if (!isAttached()) {
            return;
        }

        if (participantTeamSummaryCard != null) {
            Participant participant = getParticipant();
            Event event = getEvent();
            View teamProfileView = participantTeamSummaryCard.findViewById(R.id.challenge_participant_team_profile_view);

            if (participantTeam != null) {
                TextView teamNameTv = teamProfileView.findViewById(R.id.team_name);
                TextView teamLeadNameTv = teamProfileView.findViewById(R.id.team_lead_name);
                TextView teamSizeTv = teamProfileView.findViewById(R.id.team_size_label);
                TextView teamMilesCommitmentStatusLabelTv = teamProfileView.findViewById(R.id.team_miles_commitment_status_label);
                TextView participantCommittedMilesTv = teamProfileView.findViewById(R.id.participant_committed_miles);
                TextView teamCommittedMilesTv = teamProfileView.findViewById(R.id.team_committed_miles);
                TextView remainingGoalMilesTv = teamProfileView.findViewById(R.id.remaining_goal_miles);
                ProgressBar commitmentPV = teamProfileView.findViewById(R.id.team_miles_commitment_status_graph);

                setupTeamCommitmentBreakdown(teamProfileView);
                TextView editParticipantCommitmentTv = teamProfileView.findViewById(R.id.participant_committed_miles_edit);
                editParticipantCommitmentTv.setOnClickListener(onClickListener);

                int currentTeamSize = participantTeam.getParticipants().size();
                int teamCommitmentSteps = 0;
                if (participantTeam != null) {
                    teamCommitmentSteps = participantTeam.geTotalParticipantCommitmentSteps();
                }
                int teamCommitmentDistance = (int) DistanceConverter.distance(teamCommitmentSteps);
                int teamGoal = (int) DistanceConverter.distance( event.getTeamLimit() * event.getDefaultSteps());
                int remainingTeamGoalMiles = teamGoal - teamCommitmentDistance;
                if (remainingTeamGoalMiles < 0) remainingTeamGoalMiles = 0;

                int participantCommitmentSteps = participant.getCommittedSteps();
                int participantCommitmentDistance = (int) DistanceConverter.distance(participantCommitmentSteps);

                String participantDistanceCommitted = numberFormatter.format(participantCommitmentDistance );
                String teamDistanceCommitted = numberFormatter.format(teamCommitmentDistance);

                teamNameTv.setText(participantTeam.getName());
                teamLeadNameTv.setText(participantTeam.getLeaderName());
                teamSizeTv.setText(getResources().getQuantityString(R.plurals.team_members_count, currentTeamSize, currentTeamSize));
                teamMilesCommitmentStatusLabelTv.setText( getString(R.string.team_miles_commitment_status_template, numberFormatter.format(teamCommitmentDistance), numberFormatter.format(teamGoal)));
                int teamProgressPct = (int) (100.0 * teamCommitmentDistance / teamGoal);
                int participantPct =  (int) (100.0 * participantCommitmentDistance / teamGoal);
                commitmentPV.setSecondaryProgress( teamProgressPct);
                commitmentPV.setProgress(participantPct);
                teamCommittedMilesTv.setText( teamDistanceCommitted);
                participantCommittedMilesTv.setText(participantDistanceCommitted);
                remainingGoalMilesTv.setText(numberFormatter.format( remainingTeamGoalMiles));

                if (teamProfileView.getVisibility() != View.VISIBLE){
                    teamProfileView.setVisibility(View.VISIBLE);
                }
            }

            if (participantTeamSummaryCard.getVisibility() != View.VISIBLE) {
                participantTeamSummaryCard.setVisibility(View.VISIBLE);
            }
        }
    }

    void setupTeamCommitmentBreakdown(View teamProfileView) {
        View container = teamProfileView.findViewById(R.id.team_commitment_container);
        View image = container.findViewById(R.id.navigate_team_commitment_breakdown);
        image.setOnClickListener(onClickListener);
        expandViewHitArea(image, container);
    }

    @Override
    public void enableJoinExistingTeam(boolean enabledFlag) {
        if (showJoinTeamButton != null) {
            showJoinTeamButton.setEnabled(enabledFlag);
        }
    }

    @Override
    public void enableShowCreateTeam(boolean enabledFlag) {
        if (showCreateTeamButton != null) {
            showCreateTeamButton.setEnabled(enabledFlag);
        }
    }

    @Override
    public void hideInviteTeamMembersCard() {
        if (challengeTeamInviteCard != null && challengeTeamInviteCard.getVisibility()!= View.GONE){
            challengeTeamInviteCard.setVisibility(View.GONE);
        }
    }

    @Override
    public void showInviteTeamMembersCard(int openSlots) {
        challengeTeamInviteCard.setVisibility(View.VISIBLE);

        TextView inviteMessage = challengeTeamInviteCard.findViewById(R.id.invite_team_members_message);
        TextView inviteLabel = challengeTeamInviteCard.findViewById(R.id.team_invite_label);
        inviteMessage.setText(getResources().getQuantityString(R.plurals.challenge_invite_team_members_message, openSlots, openSlots));
        inviteLabel.setText(getResources().getQuantityString(R.plurals.team_invite_more_members_message, openSlots, openSlots));
    }

    @Override
    public void showFundraisingInvite() {
        View fundraisingBeforeChallengeStartView = challengeFundraisingProgressCard.findViewById(R.id.fundraising_progress_before_view);
        fundraisingBeforeChallengeStartView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideFundraisingInvite() {
        View fundraisingBeforeChallengeStartView = challengeFundraisingProgressCard.findViewById(R.id.fundraising_progress_before_view);
        fundraisingBeforeChallengeStartView.setVisibility(View.GONE);
    }

    void setupView(View fragmentView) {

        mainContentView = fragmentView.findViewById(R.id.challenge_main_content);

        setHasOptionsMenu(true);

        setupViewForMainContent(mainContentView);
        if (getParticipantTeam() == null) { //optimize, don't show before container when team exists
            showBeforeTeamContainer();
        }
    }

    private void setupViewForMainContent(View mainView) {

        beforeTeamContainer = mainView.findViewById(R.id.before_team_container);
        View createOrJoinTeamView = beforeTeamContainer.findViewById(R.id.challenge_create_or_join_team_view);
        if (createOrJoinTeamView != null) {
            showCreateTeamButton = createOrJoinTeamView.findViewById(R.id.show_create_team_button);
            if (showCreateTeamButton != null) {
                showCreateTeamButton.setOnClickListener(onClickListener);
            }
            showJoinTeamButton = createOrJoinTeamView.findViewById(R.id.show_join_team_button);
            if (showJoinTeamButton != null) {
                showJoinTeamButton.setOnClickListener(onClickListener);
            }
        }

        afterTeamContainer = mainView.findViewById(R.id.after_team_container);

        journeyCard = afterTeamContainer.findViewById(R.id.challenge_journey_card);
        setupChallengeJourneyCard(journeyCard);
        participantTeamSummaryCard = afterTeamContainer.findViewById(R.id.challenge_participant_team_card);

        challengeTeamInviteCard = afterTeamContainer.findViewById(R.id.challenge_team_invite_card);
        setupChallengeTeamInviteCard(challengeTeamInviteCard);

        challengeFundraisingProgressCard = afterTeamContainer.findViewById(R.id.challenge_fundraising_progress_card);
        setupChallengeFundraisingCard(challengeFundraisingProgressCard);
    }

    void setupChallengeJourneyCard(View parentView) {
        View journeyActiveView = parentView.findViewById(R.id.journey_active_view);
        View container = journeyActiveView.findViewById(R.id.challenge_journey_view_milestones_container);
        View image = container.findViewById(R.id.view_milestones_chevron);
        expandViewHitArea(image, container);
        image.setOnClickListener(onClickListener);
    }

    void setupChallengeTeamInviteCard(View parentView) {
        View container = parentView.findViewById(R.id.challenge_team_invite_container);
        View image = container.findViewById(R.id.team_invite_chevron);
        expandViewHitArea(image, container);
        image.setOnClickListener(onClickListener);
    }

    void setupChallengeFundraisingCard(View parentView) {
        View container = parentView.findViewById(R.id.fundraising_invite_container);
        View image = container.findViewById(R.id.fundraising_invite_button);
        expandViewHitArea(image, container);
        image.setOnClickListener(onClickListener);

        parentView.setVisibility(Constants.getFeatureFundraising() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showBeforeTeamContainer() {
        if (beforeTeamContainer.getVisibility() != View.VISIBLE){
            beforeTeamContainer.setVisibility(View.VISIBLE);
        }
        if (afterTeamContainer.getVisibility() != View.GONE) {
            afterTeamContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void showAfterTeamContainer() {
        if (afterTeamContainer.getVisibility() != View.VISIBLE) {
            afterTeamContainer.setVisibility(View.VISIBLE);
        }

        if (beforeTeamContainer.getVisibility() != View.GONE){
            beforeTeamContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void showCreateNewTeamView() {
        mHostingParent.showCreateTeam();
    }

    @Override
    public void showJoinTeamView() {
        mHostingParent.showJoinTeam();
    }

    public void editParticipantCommitment() {
        View teamProfile = participantTeamSummaryCard.findViewById(R.id.challenge_participant_team_profile_view);
        final TextView committedDistanceTv = teamProfile.findViewById(R.id.participant_committed_miles);
        int currentDistance = 0;
        try {
            currentDistance = NumberFormat.getNumberInstance().parse(committedDistanceTv.getText().toString()).intValue();
        }
        catch(Exception e) {
            currentDistance = 0;
        }
        challengePresenter.onShowMilesCommitmentSelected(currentDistance, new EditTextDialogListener() {
            @Override
            public void onDialogDone(@NotNull String editedValue) {
                closeKeyboard();

                committedDistanceTv.setText(editedValue);
                int newCommitmentDistance = Integer.parseInt(editedValue);
                int newCommittedSteps = DistanceConverter.steps(newCommitmentDistance);
                Commitment commitment = getParticipant().getCommitment();
                int commitmentId = 0;
                if (commitment != null) {
                    commitmentId = commitment.getId();
                }

                if (commitmentId == 0) {
                    challengePresenter.createParticipantCommitment(participantId, activeEventId, newCommittedSteps);
                }
                else {
                    challengePresenter.updateParticipantCommitment(commitmentId, participantId, activeEventId, newCommittedSteps);
                }
            }

            @Override
            public void onDialogCancel() {
                closeKeyboard();
            }
        });
    }

    @Override
    public void onCreateParticipantCommitmentToEvent(String participantId, int eventId, Commitment commitment) {
        //commitment is created in HomeActivity. It will be created in ChallengeFragment only if it failed in HomeActivity
        // Refresh data to sync up views
        teamId = SharedPreferencesUtil.getMyTeamId();
        activeEventId = SharedPreferencesUtil.getMyActiveEventId();

        challengePresenter.getEvent(activeEventId);
        challengePresenter.getTeam(teamId);
        challengePresenter.getParticipant(participantId);
    }

    @Override
    public void onUpdateParticipantCommitmentToEvent(String participantId, int eventId, int commitmentSteps) {
        Team team = getParticipantTeam();
        showParticipantTeamSummaryCard(team);
    }

    public void showTeamCommitmentBreakdown(){
        Log.d(TAG, "showTeamCommitmentBreakdown");
        Team team = getParticipantTeam();
        if (team != null) {
            boolean isTeamLead = team.isTeamLeader(participantId);
            mHostingParent.showTeamChallengeProgress(isTeamLead);
        }
    }

    void showSupportersInvite() {
        mHostingParent.showSupportersInvite();
    }

    @Override
    public void onParticipantNotificationsRetrieved(List<Notification> notifications) {
        int unreadMessageCount = getUnreadNotificationsCount( notifications);
        mHostingParent.showNotificationsCount(unreadMessageCount);
    }

    @Override
    public void onGetParticipantNotificationsError(Throwable error) {

    }
}
