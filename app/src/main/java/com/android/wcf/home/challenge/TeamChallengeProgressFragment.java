package com.android.wcf.home.challenge;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.wcf.R;
import com.android.wcf.base.BaseFragment;
import com.android.wcf.helper.SharedPreferencesUtil;
import com.android.wcf.helper.view.ListPaddingDecoration;
import com.android.wcf.model.Event;
import com.android.wcf.model.Participant;
import com.android.wcf.model.Team;

import java.text.DecimalFormat;
import java.util.List;

public class TeamChallengeProgressFragment extends BaseFragment implements TeamChallengeProgressMvp.View, TeamChallengeProgressAdapterMvp.Host {

    private static final String IS_TEAM_LEAD_ARG = "is_team_lead";
    private TeamChallengeProgressMvp.Host host;
    private Team team;
    private Event event;

    private boolean isTeamLead = false;

    private RecyclerView participantRecyclerView = null;
    private TeamChallengeProgressAdapter teamChallengeProgressAdapter = null;
    private View challengeTeamProgressSummaryContainer;
    private View challengeTeamMemberDetailsContainer;
    private View challengeTeamInviteContainer;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.team_invite_chevron:
                    inviteTeamMembers();
                    break;
            }
        }
    };

    public static TeamChallengeProgressFragment getInstance(boolean isTeamLead) {
        TeamChallengeProgressFragment fragment = new TeamChallengeProgressFragment();
        Bundle args = new Bundle();
        args.putBoolean(IS_TEAM_LEAD_ARG, isTeamLead);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TeamChallengeProgressMvp.Host) {
            host = (TeamChallengeProgressMvp.Host) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement TeamChallengeProgressMvp.Host");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        host = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        if (bundle != null) {
            isTeamLead = bundle.getBoolean(IS_TEAM_LEAD_ARG);
        }

        View fragmentView = inflater.inflate(R.layout.fragment_team_challenge_progress, container, false);
        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        team = getParticipantTeam();
        event = getEvent();
        host.setToolbarTitle(team.getName(), true);
        setupView(view);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean handled = super.onOptionsItemSelected(item);
        if (!handled) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    closeView();
                    handled = true;
                    break;
                default:
                    break;
            }
        }
        return handled;
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshTeamParticipantsList();
    }

    @Override
    public void onStop() {
        super.onStop();
        //  presenter.onStop();
    }


    void refreshTeamParticipantsList() {

        teamChallengeProgressAdapter.clearSelectionPosition(); //TODO: if we have a team previously selected, find its position and select that
        participantRecyclerView.scrollToPosition(0);
        teamChallengeProgressAdapter.updateParticipantsData(team.getParticipants());
    }

    void setupView(View fragmentView) {

        challengeTeamProgressSummaryContainer = fragmentView.findViewById(R.id.challenge_team_progress_summary_container);
        challengeTeamMemberDetailsContainer = fragmentView.findViewById(R.id.challenge_team_member_details_container);
        challengeTeamInviteContainer = fragmentView.findViewById(R.id.challenge_team_invite_container);

        setupChallengeTeamProgressSummaryContainer(challengeTeamProgressSummaryContainer);
        setupChallengeTeamMemberDetailsContainer(challengeTeamMemberDetailsContainer);
        setupChallengeTeamInviteContainer(challengeTeamInviteContainer);
    }

    void setupChallengeTeamProgressSummaryContainer(View container) {
        TextView distanceGoalTv = container.findViewById(R.id.team_total_distance_goal);
        TextView distanceWalkedTv = container.findViewById(R.id.team_total_distance_walked);
        TextView fundsRaisedTv = container.findViewById(R.id.team_total_funds_raised_amount);
        distanceWalkedTv.setText(getTeamTotalDistanceWalked() + "");
        distanceGoalTv.setText(getString(R.string.team_detail_distance_goal_template, event.getTeamDistanceGoal() ) );

        double fundRaiseAccrued = getTeamTotalFundRaiseAccrued();
        DecimalFormat formatter = new DecimalFormat("$###,###.##");
        fundsRaisedTv.setText(formatter.format(fundRaiseAccrued));
    }

    private int getTeamTotalDistanceWalked() {
        return 0; //TODO add participants' record steps converted to miles
    }

    private double getTeamTotalFundRaiseAccrued() {
        return 0.0;
    }

    void setupChallengeTeamMemberDetailsContainer(View container) {
        if (teamChallengeProgressAdapter == null) {
            teamChallengeProgressAdapter = new TeamChallengeProgressAdapter(this,
                    event.getTeamLimit(),
                    SharedPreferencesUtil.getMyParticipantId(),
                    event.hasChallengeStarted()
            );
        }

        participantRecyclerView = container.findViewById(R.id.team_members_list);
        participantRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        participantRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));

        participantRecyclerView.addItemDecoration(new ListPaddingDecoration(getContext()));

        participantRecyclerView.setAdapter(teamChallengeProgressAdapter);

    }

    void setupChallengeTeamInviteContainer(View container) {
        TextView inviteLabel = container.findViewById(R.id.team_invite_label);
        boolean showTeamInvite = false;
        if (isTeamLead && event != null) {
            if (event.daysToStartEvent() >= 0 && !event.hasTeamBuildingEnded()) {
                List<Participant> participants = team.getParticipants();
                if (participants != null) {
                    int openSlots = event.getTeamLimit() - participants.size();
                    if (openSlots > 0) {
                        showTeamInvite = true;
                        String openSlotMessage = getResources().getQuantityString(R.plurals.team_invite_more_members_message, openSlots, openSlots);
                        inviteLabel.setText(openSlotMessage);
                    }
                }
            }
        }
        if (showTeamInvite) {
            View image = container.findViewById(R.id.team_invite_chevron);
            expandViewHitArea(image, container);
            image.setOnClickListener(onClickListener);
        }
        container.setVisibility(showTeamInvite ? View.VISIBLE : View.GONE);
    }

    void closeView() {
        getActivity().onBackPressed();
    }
}
