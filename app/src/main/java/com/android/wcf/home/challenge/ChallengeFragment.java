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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.wcf.R;
import com.android.wcf.application.WCFApplication;
import com.android.wcf.base.BaseFragment;
import com.android.wcf.helper.SharedPreferencesUtil;
import com.android.wcf.model.Constants;
import com.android.wcf.model.Event;
import com.android.wcf.model.Participant;
import com.android.wcf.model.Team;
import com.android.wcf.settings.EditTextDialogListener;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

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
    private View journeyCard = null;
    private View challengeTeamInviteCard = null;
    private View participantTeamSummaryCard = null;
    private Button showCreateTeamButton = null;
    private Button showJoinTeamButton = null;
    private View challengeFundraisingProgressCard = null;

    //non-ui properties
    private ChallengeMvp.Presenter challengePresenter;

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
                case R.id.participant_committed_miles_edit:
                    editParticipantCommitment();
                    break;
                case R.id.navigate_team_commitment_breakdown:
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
    public void onParticipantIdMissing() {
        Toast.makeText(getContext(), "Login Id needed. Please login", Toast.LENGTH_SHORT).show();
        WCFApplication.instance.requestLogin();
    }

    @Override
    public void hideJourneyBeforeStartView() {
        View journeyBeforeStartView = mainContentView.findViewById(R.id.journey_active_view);
        if (journeyBeforeStartView != null && journeyBeforeStartView.getVisibility() != View.GONE) {
            journeyBeforeStartView.setVisibility(View.GONE);
        }
    }

    @Override
    public void showJourneyBeforeStartView(Event event) {
        View journeyBeforeStartView = mainContentView.findViewById(R.id.journey_before_start_view);
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
    public void hideJourneyActiveView() {
        if (journeyCard != null) {
            View journeyActiveView = journeyCard.findViewById(R.id.journey_active_view);
            if (journeyActiveView != null && journeyActiveView.getVisibility() != View.GONE) {
                journeyActiveView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void showJourneyActiveView(Event event) {
        if (journeyCard != null) {

            if (event != null) {
                //update view data
            }

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
    public void hideCreateOrJoinTeamCard() {
        View createOrJoinTeamCard = mainContentView.findViewById(R.id.challenge_create_or_join_team_card);

        if (createOrJoinTeamCard != null) {
            showCreateTeamButton = createOrJoinTeamCard.findViewById(R.id.show_create_team_button);
            if (showCreateTeamButton != null) {
                showCreateTeamButton.setOnClickListener(null);
            }
            showJoinTeamButton = createOrJoinTeamCard.findViewById(R.id.show_join_team_button);
            if (showJoinTeamButton != null) {
                showJoinTeamButton.setOnClickListener(null);
            }
        }
        if (createOrJoinTeamCard.getVisibility() != View.GONE) {
            createOrJoinTeamCard.setVisibility(View.GONE);
        }
    }

    @Override
    public void showCreateOrJoinTeamCard() {
        View createOrJoinTeamCard = mainContentView.findViewById(R.id.challenge_create_or_join_team_card);

        if (createOrJoinTeamCard != null) {
            showCreateTeamButton = createOrJoinTeamCard.findViewById(R.id.show_create_team_button);
            if (showCreateTeamButton != null) {
                showCreateTeamButton.setOnClickListener(onClickListener);
            }
            showJoinTeamButton = createOrJoinTeamCard.findViewById(R.id.show_join_team_button);
            if (showJoinTeamButton != null) {
                showJoinTeamButton.setOnClickListener(onClickListener);
            }
        }

        if (createOrJoinTeamCard.getVisibility() != View.VISIBLE) {
            createOrJoinTeamCard.setVisibility(View.VISIBLE);
        }
    }

    public void hideMyTeamSummaryCard() {

        if (participantTeamSummaryCard != null && participantTeamSummaryCard.getVisibility() != View.GONE) {
            participantTeamSummaryCard.setVisibility(View.GONE);
        }
    }

    @Override
    public void showMyTeamSummaryCard(Team team) {
        if (participantTeamSummaryCard != null) {
            Participant participant = getParticipant();
            Event event = getEvent();
            View teamProfile = participantTeamSummaryCard.findViewById(R.id.team_profile);
            if (team != null) {
                TextView teamNameTv = teamProfile.findViewById(R.id.team_name);
                TextView teamLeadLabelTv = teamProfile.findViewById(R.id.team_lead_label);
                TextView teamLeadNameTv = teamProfile.findViewById(R.id.team_lead_name);
                TextView teamSizeTv = teamProfile.findViewById(R.id.team_size_label);
                TextView teamMilesCommitmentStatusLabelTv = teamProfile.findViewById(R.id.team_miles_commitment_status_label);
                TextView participantCommittedMilesTv = teamProfile.findViewById(R.id.participant_committed_miles);
                TextView teamCommittedMilesTv = teamProfile.findViewById(R.id.team_committed_miles);
                TextView remainingGoalMilesTv = teamProfile.findViewById(R.id.remaining_goal_miles);
                ImageView teamBreakdownBtn = teamProfile.findViewById(R.id.navigate_team_commitment_breakdown);
                TextView editParticipantCommitmentTv = teamProfile.findViewById(R.id.participant_committed_miles_edit);
                editParticipantCommitmentTv.setOnClickListener(onClickListener);

                DecimalFormat formatter = new DecimalFormat("#,###,###");
                int currentTeamSize = team.getParticipants().size();
                int teamMiles = currentTeamSize * Constants.PARTICIPANT_COMMITMENT_MILES_DEFAULT;
                int teamGoal = event.getTeamLimit() * Constants.PARTICIPANT_COMMITMENT_MILES_DEFAULT;
                int remainingTeamGoalMiles = teamGoal - teamMiles;
                if (remainingTeamGoalMiles < 0) remainingTeamGoalMiles = 0;

                String participantMiles = formatter.format(SharedPreferencesUtil.getMyMilesCommitted());
                String teamMilesCommitted = formatter.format(teamMiles);
                teamBreakdownBtn.setOnClickListener(onClickListener);

                teamNameTv.setText(team.getName());
                teamLeadLabelTv.setVisibility(View.VISIBLE);
                teamLeadNameTv.setText(participant.getParticipantId()); //TODO get participant name
                teamSizeTv.setText(getResources().getQuantityString(R.plurals.team_members_count, currentTeamSize, currentTeamSize));
                teamMilesCommitmentStatusLabelTv.setText( getString(R.string.team_miles_commitment_status_template, teamMiles, teamGoal));
                teamCommittedMilesTv.setText(teamMilesCommitted);
                participantCommittedMilesTv.setText(participantMiles);
                remainingGoalMilesTv.setText(formatter.format( remainingTeamGoalMiles));
            }

            if (participantTeamSummaryCard.getVisibility() != View.VISIBLE) {
                participantTeamSummaryCard.setVisibility(View.VISIBLE);
            }
        }
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
        challengeTeamInviteCard.setVisibility(View.GONE);
    }

    @Override
    public void showInviteTeamMembersCard(int openSlots) {
        challengeTeamInviteCard.setVisibility(View.VISIBLE);

        TextView inviteMessage = challengeTeamInviteCard.findViewById(R.id.invite_team_members_message);
        inviteMessage.setText(getString(R.string.challenge_invite_team_members_message, openSlots));

        TextView inviteLabel = challengeTeamInviteCard.findViewById(R.id.team_invite_label);
        inviteLabel.setText(getString(R.string.team_invite_more_members_message, openSlots));
    }

    @Override
    public void showFundraisingInvite() {
        View fundraisingBeforeChallengeStartView = challengeFundraisingProgressCard.findViewById(R.id.fundraising_progress_card_before);
        boolean challengeStarted = false;
        if (!challengeStarted) {
            fundraisingBeforeChallengeStartView.setVisibility(View.VISIBLE);
        } else {
            fundraisingBeforeChallengeStartView.setVisibility(View.GONE);
        }
    }

    void setupView(View fragmentView) {

        mainContentView = fragmentView.findViewById(R.id.challenge_main_content);

        setHasOptionsMenu(true);

        setupViewForMainContent(mainContentView);
    }

    private void setupViewForMainContent(View mainView) {

        journeyCard = mainView.findViewById(R.id.challenge_journey_card);
        View journeyBeforeStartView = journeyCard.findViewById(R.id.journey_before_start_view);
        View journeyActiveView = journeyCard.findViewById(R.id.journey_active_view);

        participantTeamSummaryCard = mainView.findViewById(R.id.challenge_participant_team_card);
        challengeTeamInviteCard = mainView.findViewById(R.id.challenge_team_invite_card);
        setupTeamInviteClickListeners(challengeTeamInviteCard);
        challengeFundraisingProgressCard = mainView.findViewById(R.id.challenge_fundraising_progress_card);
        setupDashboardFundraisingCard();
    }


    void setupTeamInviteClickListeners(View parentView) {
        View container = parentView.findViewById(R.id.team_invite_container);
        View image = container.findViewById(R.id.team_invite_chevron);
        image.setOnClickListener(onClickListener);
        expandViewHitArea(image, container);
    }

    void setupDashboardFundraisingCard() {
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
        int currentMiles = 0;
        try {
            View teamProfile = participantTeamSummaryCard.findViewById(R.id.team_profile);
            TextView participantCommittedMilesTv = teamProfile.findViewById(R.id.participant_committed_miles);

            currentMiles = Integer.parseInt(participantCommittedMilesTv.getText().toString().replaceAll(",", ""));
        }
        catch(Exception e) {
            currentMiles = 0;
        }
        challengePresenter.onShowMilesCommitmentSelected(currentMiles, new EditTextDialogListener() {
            @Override
            public void onDialogDone(@NotNull String editedValue) {
//                View teamProfile = participantTeamSummaryCard.findViewById(R.id.team_profile);
//                TextView participantCommittedMilesTv = teamProfile.findViewById(R.id.participant_committed_miles);
//                participantCommittedMilesTv.setText(editedValue);
                Team team = getParticipantTeam();
                showMyTeamSummaryCard(team);
            }

            @Override
            public void onDialogCancel() {
            }
        });
    }
}
