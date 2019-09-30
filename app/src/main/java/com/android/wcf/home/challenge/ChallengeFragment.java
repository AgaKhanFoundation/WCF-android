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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.wcf.R;
import com.android.wcf.application.WCFApplication;
import com.android.wcf.base.BaseFragment;
import com.android.wcf.helper.DistanceConverter;
import com.android.wcf.helper.SharedPreferencesUtil;
import com.android.wcf.model.Event;
import com.android.wcf.model.Participant;
import com.android.wcf.model.Team;
import com.android.wcf.settings.EditTextDialogListener;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class ChallengeFragment extends BaseFragment implements ChallengeMvp.ChallengeView {

    private static final String TAG = ChallengeFragment.class.getSimpleName();

    /* the fragment initialization parameters */
    private static final String ARG_MY_PARTICIPANT_ID = "my_participant_id";
    private static final String ARG_MY_ACTIVE_EVENT_ID = "my_active_event_id";
    private static final String ARG_MY_TEAM_ID = "my_team_id";

    /* class constants */

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
        super.onCreate(savedInstanceState);
        challengePresenter = new ChallengePresenter(this);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_challenge, container, false);

        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView(view);
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
        mHostingParent.setToolbarTitle(getString(R.string.nav_challenge), false);
        participantId = SharedPreferencesUtil.getMyParticipantId();
        if(participantId == null || participantId.isEmpty()) {
            onParticipantIdMissing();
            return;
        }

        teamId = SharedPreferencesUtil.getMyTeamId();
        activeEventId = SharedPreferencesUtil.getMyActiveEventId();

        challengePresenter.getEvent(activeEventId);
        challengePresenter.getTeam(teamId);
        challengePresenter.getParticipant(participantId);
    }

    @Override
    public void onResume() {
        super.onResume();
        challengePresenter.getTeams(); //TODO: next version, we will have to associate teams to event
    }

    @Override
    public void onStop() {
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
        SharedPreferencesUtil.cleartMyActiveEventId();
    }

    @Override
    public void onGetTeamError(Throwable error) {
        showError(R.string.teams_manager_error, error.getMessage());
        cacheParticipantTeam(null);
        SharedPreferencesUtil.clearMyTeamId();
        teamId = SharedPreferencesUtil.getMyTeamId();
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
                String msg = getResources().getQuantityString(R.plurals.days_to_event_start, daysToStart, daysToStart, formattedStartDate);
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

            //TODO: show Milestones view

            View journeyActiveView = journeyCard.findViewById(R.id.journey_active_view);
            if (journeyActiveView != null && journeyActiveView.getVisibility() != View.VISIBLE) {
                journeyActiveView.setVisibility(View.VISIBLE);
            }

            View journeyStartView = journeyCard.findViewById(R.id.journey_before_start_view);
            if (journeyStartView != null && journeyStartView.getVisibility() != View.GONE) {
                journeyStartView.setVisibility(View.GONE);
            }
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
    public void showParticipantTeamSummaryCard(Team team) {
        if (!isAttached()) {
            return;
        }

        if (participantTeamSummaryCard != null) {
            Participant participant = getParticipant();
            Event event = getEvent();
            View teamProfileView = participantTeamSummaryCard.findViewById(R.id.challenge_participant_team_profile_view);

            if (team != null) {
                TextView teamNameTv = teamProfileView.findViewById(R.id.team_name);
                TextView teamLeadNameTv = teamProfileView.findViewById(R.id.team_lead_name);
                TextView teamSizeTv = teamProfileView.findViewById(R.id.team_size_label);
                TextView teamMilesCommitmentStatusLabelTv = teamProfileView.findViewById(R.id.team_miles_commitment_status_label);
                TextView participantCommittedMilesTv = teamProfileView.findViewById(R.id.participant_committed_miles);
                TextView teamCommittedMilesTv = teamProfileView.findViewById(R.id.team_committed_miles);
                TextView remainingGoalMilesTv = teamProfileView.findViewById(R.id.remaining_goal_miles);

                setupTeamCommitmentBreakdown(teamProfileView);
                TextView editParticipantCommitmentTv = teamProfileView.findViewById(R.id.participant_committed_miles_edit);
                editParticipantCommitmentTv.setOnClickListener(onClickListener);

                int currentTeamSize = team.getParticipants().size();
                int teamDistance = (int) DistanceConverter.Companion.distance(currentTeamSize * event.getDefaultParticipantCommitment()); //TODO, this should from commitments for participants
                int teamGoal = (int) DistanceConverter.Companion.distance( event.getTeamLimit() * event.getDefaultParticipantCommitment());
                int remainingTeamGoalMiles = teamGoal - teamDistance;
                if (remainingTeamGoalMiles < 0) remainingTeamGoalMiles = 0;

                String participantDistanceCommitted = numberFormatter.format((int) participant.getCommitmentDistance());
                String teamDistanceCommitted = numberFormatter.format(teamDistance);

                teamNameTv.setText(team.getName());
                teamLeadNameTv.setText(team.getLeaderName());
                teamSizeTv.setText(getResources().getQuantityString(R.plurals.team_members_count, currentTeamSize, currentTeamSize));
                teamMilesCommitmentStatusLabelTv.setText( getString(R.string.team_miles_commitment_status_template, numberFormatter.format(teamDistance), numberFormatter.format(teamGoal)));
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
        inviteMessage.setText(getResources().getQuantityString(R.plurals.challenge_invite_team_members_message, openSlots, openSlots));

        TextView inviteLabel = challengeTeamInviteCard.findViewById(R.id.team_invite_label);
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
        participantTeamSummaryCard = afterTeamContainer.findViewById(R.id.challenge_participant_team_card);

        challengeTeamInviteCard = afterTeamContainer.findViewById(R.id.challenge_team_invite_card);
        setupChallengeTeamInviteCard(challengeTeamInviteCard);

        challengeFundraisingProgressCard = afterTeamContainer.findViewById(R.id.challenge_fundraising_progress_card);
        setupChallengeFundraisingCard(challengeFundraisingProgressCard);
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
                committedDistanceTv.setText(editedValue);
                int newCommitmentDistance = Integer.parseInt(editedValue);
                int newCommittedSteps = DistanceConverter.Companion.steps(newCommitmentDistance);
                int commitmentId = getEvent().getParticipantCommitmentId(activeEventId);
                if (commitmentId == 0) {
                    challengePresenter.createParticipantCommitment(participantId, activeEventId, newCommittedSteps);
                }
                else {
                    challengePresenter.updateParticipantCommitment(commitmentId, participantId, activeEventId, newCommittedSteps);
                }

                Team team = getParticipantTeam();
                showParticipantTeamSummaryCard(team);
            }

            @Override
            public void onDialogCancel() {
            }
        });
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
}
