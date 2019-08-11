package com.android.wcf.home.challenge;

import android.content.Context;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.wcf.R;
import com.android.wcf.application.WCFApplication;
import com.android.wcf.base.BaseFragment;
import com.android.wcf.helper.SharedPreferencesUtil;
import com.android.wcf.helper.view.ListPaddingDecoration;
import com.android.wcf.model.Event;
import com.android.wcf.model.Team;

import java.util.List;

public class ChallengeFragment extends BaseFragment implements ChallengeMvp.ChallengeView, TeamsAdapterMvp.Host {

    private static final String TAG = ChallengeFragment.class.getSimpleName();

    /* the fragment initialization parameters */
    private static final String ARG_MY_PARTICIPANT_ID = "my_participant_id";
    private static final String ARG_MY_ACTIVE_EVENT_ID = "my_active_event_id";
    private static final String ARG_MY_TEAM_ID = "my_team_id";

    /* class constants */
    public static final int MIN_TEAM_NAME_SIZE = 3;
    public static final int MIN_TEAM_LEAD_NAME_SIZE = 0;

    // host for this fragment
    ChallengeMvp.Host mHostingParent;

    /* UI elements */

    private View mainContentView = null;
    private View journeyCard = null;
    private View joinTeamView = null;

    private Button showCreateTeamButton = null;
    private Button showJoinTeamButton = null;

    private Button joinTeamButton = null;
    private RecyclerView teamsListRecyclerView = null;
    private TeamsAdapter teamsAdapter = null;

    /* non-ui class properties */
    private String participantId;
    private int activeEventId;
    private int teamId;

    private ChallengeMvp.Presenter challengePresenter;

    public ChallengeFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param participantId   participantId.
     * @param teamId teamId.
     * @return A new instance of fragment ChallengeFragment.
     */
    public static ChallengeFragment newInstance(String participantId, int eventId, int teamId) {
        ChallengeFragment fragment = new ChallengeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MY_PARTICIPANT_ID, participantId);
        args.putInt(ARG_MY_ACTIVE_EVENT_ID, eventId);
        args.putInt(ARG_MY_TEAM_ID, teamId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        challengePresenter = new ChallengePresenter(this);

        if (getArguments() != null) {
            participantId = getArguments().getString(ARG_MY_PARTICIPANT_ID);
            activeEventId = getArguments().getInt(ARG_MY_ACTIVE_EVENT_ID);
            teamId = getArguments().getInt(ARG_MY_TEAM_ID);
        }
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
        setupView(getView());
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        mHostingParent.setViewTitle(getString(R.string.nav_challenge));
        challengePresenter.getEvent(activeEventId);
        Team team = getParticipantTeam();
        if (team != null) {
            teamId = team.getId();
        }
        challengePresenter.getTeam(teamId);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean handled = super.onOptionsItemSelected(item);
        if (!handled) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    closeTeamSelection();
                    handled = true;
                    break;
                default:
                    break;
            }
        }
        return handled;
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
        View journeyBeforeStartView = mainContentView.findViewById(R.id.journey_before_start_view);
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
        View createOrJoinTeamCard = mainContentView.findViewById(R.id.create_or_join_team_card);

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
        View createOrJoinTeamCard = mainContentView.findViewById(R.id.create_or_join_team_card);

        if (createOrJoinTeamCard != null) {
            showCreateTeamButton = createOrJoinTeamCard.findViewById(R.id.show_create_team_button);
            if (showCreateTeamButton != null) {
                showCreateTeamButton.setOnClickListener(onClickListener);
            }
            showJoinTeamButton = createOrJoinTeamCard.findViewById(R.id.show_join_team_button);
            if (showJoinTeamButton != null) {
                showJoinTeamButton.setOnClickListener(onClickListener);
                List<Team> teams = getTeamList();
                showJoinTeamButton.setEnabled((teams == null || teams.size() == 0) ? false : true);
            }
        }

        if (createOrJoinTeamCard.getVisibility() != View.VISIBLE) {
            createOrJoinTeamCard.setVisibility(View.VISIBLE);
        }
        // challengePresenter.getTeams();
    }

    public void hideTeamCard() {
        View myTeamCard = mainContentView.findViewById(R.id.my_team_card);
        if (myTeamCard != null && myTeamCard.getVisibility() != View.GONE) {
            myTeamCard.setVisibility(View.GONE);
        }
    }

    @Override
    public void showMyTeamCard(Team team) {
        View myTeamCard = mainContentView.findViewById(R.id.my_team_card);
        if (myTeamCard != null) {

            if (team != null) {
                //update team view data
            }

            if (myTeamCard.getVisibility() != View.VISIBLE) {
                myTeamCard.setVisibility(View.VISIBLE);
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

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.show_create_team_button:
                    challengePresenter.showCreateTeamClick();
                    break;
                case R.id.show_join_team_button:
                    challengePresenter.showTeamsToJoinClick();
                    break;
                case R.id.join_team_button:
                    teamSelectedToJoin();
                    break;
            }
        }
    };

    void setupView(View fragmentView) {

        mainContentView = fragmentView.findViewById(R.id.main_content);
        joinTeamView = fragmentView.findViewById(R.id.join_team_content);

        setHasOptionsMenu(true);

        setupViewForMainContent(mainContentView);
    }

    private void setupViewForMainContent(View mainView) {

        journeyCard = mainView.findViewById(R.id.journey_card);
        View journeyBeforeStartView = journeyCard.findViewById(R.id.journey_before_start_view);
        View journeyActiveView = journeyCard.findViewById(R.id.journey_active_view);

        View myTeamCard = mainView.findViewById(R.id.my_team_card);
    }

    private void setupViewForJoinTeam(View joinTeamView) {
        if (teamsAdapter == null) {
            teamsAdapter = new TeamsAdapter(this);
        }

        teamsListRecyclerView = joinTeamView.findViewById(R.id.teams_list);
        teamsListRecyclerView.setLayoutManager(new LinearLayoutManager(joinTeamView.getContext()));
        teamsListRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));

        teamsListRecyclerView.addItemDecoration(new ListPaddingDecoration(getContext()));

        teamsListRecyclerView.setAdapter(teamsAdapter);

        joinTeamButton = joinTeamView.findViewById(R.id.join_team_button);

        if (joinTeamButton != null) {
            joinTeamButton.setOnClickListener(onClickListener);
            joinTeamButton.setEnabled(false); // will be enabled when a team is selected
        }

        mHostingParent.showToolbarUpAffordance(true);
    }

    @Override
    public void teamRowSelected(int pos) {
        //TODO: enable selecting a team if it has capacity for additional members or show message
        joinTeamButton.setEnabled(pos >= 0);
    }

    @Override
    public void showCreateNewTeamView() {
        mHostingParent.showCreateTeam();
    }

    @Override
    public void showTeamList() {
        List<Team> teams = getTeamList();
        setupViewForJoinTeam(joinTeamView);

        if (joinTeamView == null) {
            showMessage("Joining team coming soon");
            return;
        }
        teamsAdapter.clearTeamSelectionPosition(); //TODO: if we have a team previously selected, find its position and select that
        teamsListRecyclerView.scrollToPosition(0);
        teamsAdapter.updateTeamsData(teams);

        joinTeamView.setVisibility(View.VISIBLE);

        if (mainContentView != null && mainContentView.getVisibility() != View.GONE) {
            mainContentView.setVisibility(View.GONE);
        }
    }

    private void teamSelectedToJoin() {
        Team selectedTeam = teamsAdapter.getSelectedTeam();
        if (selectedTeam == null) {
            showMessage("Please select a team to join");
            return;
        }
        //TODO ensure capacity, if not show message

        challengePresenter.assignParticipantToTeam(participantId, selectedTeam.getId());
    }

    private void closeTeamSelection() {
        mainContentView.setVisibility(View.VISIBLE);

        if (joinTeamView != null && joinTeamView.getVisibility() != View.GONE) {
            joinTeamView.setVisibility(View.GONE);
        }
        mHostingParent.showToolbarUpAffordance(false);
    }

    @Override
    public void participantJoinedTeam(String participantId, int teamId) {
        SharedPreferencesUtil.saveMyTeamId(teamId);
        // refresh the objects
        closeTeamSelection();
        //TODO: see if these refresh is needed, perhaps it is sufficient for presenter to update its data using the response
        challengePresenter.getParticipant(participantId);
        challengePresenter.getTeam(teamId);
        challengePresenter.getTeams();
    }

}
