package com.android.wcf.home.challenge;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.wcf.R;
import com.android.wcf.base.BaseFragment;
import com.android.wcf.helper.view.ListPaddingDecoration;
import com.android.wcf.model.Event;
import com.android.wcf.model.Team;
import com.android.wcf.helper.SharedPreferencesUtil;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentHost} interface
 * to handle interaction events.
 * Use the {@link ChallengeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChallengeFragment extends BaseFragment implements ChallengeMvp.ChallengeView, TeamsAdapterMvp.Host {

    private static final String TAG = ChallengeFragment.class.getSimpleName();

    /* the fragment initialization parameters */
    private static final String ARG_MY_FACEBOOK_ID = "my_fbid";
    private static final String ARG_MY_ACTIVE_EVENT_ID = "my_active_event_id";
    private static final String ARG_MY_TEAM_ID = "my_team_id";

    /* class constants */
    public static final int MIN_TEAM_NAME_SIZE = 3;
    public static final int MIN_TEAM_LEAD_NAME_SIZE = 0;

    // host for this fragment
    FragmentHost mHostingParent;

    /* UI elements */

    private View mainContentView = null;
    private View journeyBeforeStartCard = null;
    private View journeyCard = null;

    private View createOrJoinTeamCard = null;
    private View myTeamCard = null;

    private View newTeamView = null;

    private View joinTeamView = null;

    private Button showCreateTeamButton = null;
    private Button showJoinTeamsButton = null;

    private Button createTeamButton = null;
    private Button cancelCreateTeamButton = null;
    private EditText teamNameEditText = null;
    private EditText teamLeadNameEditText = null;

    private Button joinTeamButton = null;
    private RecyclerView teamsListRecyclerView = null;
    private TeamsAdapter teamsAdapter = null;

    /* non-ui class properties */
    private String facebookId;
    private int activeEventId;
    private int teamId;

    private ChallengeMvp.Presenter challengePresenter = new ChallengePresenter(this);

    public ChallengeFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param fbid   facebookId.
     * @param teamId teamId.
     * @return A new instance of fragment ChallengeFragment.
     */
    public static ChallengeFragment newInstance(String fbid, int eventId, int teamId) {
        ChallengeFragment fragment = new ChallengeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MY_FACEBOOK_ID, fbid);
        args.putInt(ARG_MY_ACTIVE_EVENT_ID, eventId);
        args.putInt(ARG_MY_TEAM_ID, teamId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            facebookId = getArguments().getString(ARG_MY_FACEBOOK_ID);
            activeEventId = getArguments().getInt(ARG_MY_ACTIVE_EVENT_ID);
            teamId = getArguments().getInt(ARG_MY_TEAM_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_challenge, container, false);

        setupView(fragmentView);
        return fragmentView;

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        challengePresenter.getEvent(activeEventId);
        challengePresenter.getTeam(teamId);
    }

    @Override
    public void onResume() {
        super.onResume();
        challengePresenter.getTeams(); //TODO: next version, we will have to associate teams to event
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mHostingParent != null) {
            mHostingParent.onChallengeFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentHost) {
            mHostingParent = (FragmentHost) context;
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
    public void onFacebookIdMissing() {
        Toast.makeText(getContext(), "FacebookId needed. Please login", Toast.LENGTH_SHORT).show();
        //TODO: navigate to login activity and finish Home activity
    }

    @Override
    public void hideJourneyBeforeStartCard() {
        journeyBeforeStartCard = mainContentView.findViewById(R.id.journey_before_start_card);
        if (journeyBeforeStartCard != null && journeyBeforeStartCard.getVisibility() != View.GONE) {
            journeyBeforeStartCard.setVisibility(View.GONE);
        }
    }

    @Override
    public void showJourneyBeforeStartCard(Event event) {
        journeyBeforeStartCard = mainContentView.findViewById(R.id.journey_before_start_card);
        if (journeyBeforeStartCard != null) {

            if (event != null) {
                //update view data
                TextView journeyText = journeyBeforeStartCard.findViewById(R.id.journey_text);
                int daysToStart = event.daysToStartEvent();
                int flags = DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR;
                String formattedStartDate = DateUtils.formatDateTime(getContext(), event.getStartDate().getTime(), flags);
                String msg = getResources().getQuantityString(R.plurals.days_to_event_start, daysToStart, daysToStart, formattedStartDate);
                journeyText.setText(msg);
            }

            if (journeyBeforeStartCard.getVisibility() != View.VISIBLE) {
                journeyBeforeStartCard.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void hideJourneyDetails() {
        journeyCard = mainContentView.findViewById(R.id.journey_card);
        if (journeyCard != null && journeyCard.getVisibility() != View.GONE) {
            journeyCard.setVisibility(View.GONE);
        }
    }

    @Override
    public void showJourneyDetails(Event event) {
        journeyCard = mainContentView.findViewById(R.id.journey_card);
        if (journeyCard != null) {

            if (event != null) {
                //update view data
            }

            if (journeyCard.getVisibility() != View.VISIBLE) {
                journeyCard.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    public void hideCreateOrJoinTeamCard() {
        createOrJoinTeamCard = mainContentView.findViewById(R.id.create_or_join_team_card);

        if (createOrJoinTeamCard != null) {
            showCreateTeamButton = createOrJoinTeamCard.findViewById(R.id.show_create_team_button);
            if (showCreateTeamButton != null) {
                showCreateTeamButton.setOnClickListener(null);
            }
            showJoinTeamsButton = createOrJoinTeamCard.findViewById(R.id.show_join_teams_button);
            if (showJoinTeamsButton != null) {
                showJoinTeamsButton.setOnClickListener(null);
            }
        }
        if (createOrJoinTeamCard.getVisibility() != View.GONE) {
            createOrJoinTeamCard.setVisibility(View.GONE);
        }
    }

    @Override
    public void showCreateOrJoinTeamCard() {
        createOrJoinTeamCard = mainContentView.findViewById(R.id.create_or_join_team_card);

        if (createOrJoinTeamCard != null) {
            showCreateTeamButton = createOrJoinTeamCard.findViewById(R.id.show_create_team_button);
            if (showCreateTeamButton != null) {
                showCreateTeamButton.setOnClickListener(onClickListener);
            }
            showJoinTeamsButton = createOrJoinTeamCard.findViewById(R.id.show_join_teams_button);
            if (showJoinTeamsButton != null) {
                showJoinTeamsButton.setOnClickListener(onClickListener);
            }
        }

        if (createOrJoinTeamCard.getVisibility() != View.VISIBLE) {
            createOrJoinTeamCard.setVisibility(View.VISIBLE);
        }
    }

    public void hideTeamCard() {
        myTeamCard = mainContentView.findViewById(R.id.my_team_card);
        if (myTeamCard != null && myTeamCard.getVisibility() != View.GONE) {
            myTeamCard.setVisibility(View.GONE);
        }
    }

    @Override
    public void showMyTeamCard(Team team) {
        myTeamCard = mainContentView.findViewById(R.id.my_team_card);
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
        if (showJoinTeamsButton != null) {
            showJoinTeamsButton.setEnabled(enabledFlag);
        }
    }

    @Override
    public void enableShowCreateTeam(boolean enabledFlag) {
        if (showCreateTeamButton != null) {
            showCreateTeamButton.setEnabled(enabledFlag);
        }
    }

    private TextWatcher creatTeamEditWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            enableCreateTeamButton();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    void enableCreateTeamButton() {
        boolean enabled = false;
        if (teamNameEditText != null && teamLeadNameEditText != null) {
            String teamName = teamNameEditText.getText().toString();
            String teamLeadName = teamLeadNameEditText.getText().toString();
            if (teamName.trim().length() >= MIN_TEAM_NAME_SIZE && teamLeadName.trim().length() >= MIN_TEAM_LEAD_NAME_SIZE) {
                enabled = true;
            }
        }
        createTeamButton.setEnabled(enabled);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.show_create_team_button:
                    challengePresenter.showCreateTeamClick();
                    break;
                case R.id.create_team_button:
                    closeKeyboard();
                    challengePresenter.createTeamClick(teamNameEditText.getText().toString());
                    break;
                case R.id.cancel_create_team_button:
                    closeKeyboard();
                    challengePresenter.cancelCreateTeamClick();
                    break;
                case R.id.show_join_teams_button:
                    challengePresenter.showTeamsToJoinClick();
                    break;
                case R.id.join_team_button:
                    teamSelectedToJoin();
                    break;
            }
        }
    };

    void setupView(View view) {

        mainContentView = view.findViewById(R.id.main_content);

        newTeamView = view.findViewById(R.id.new_team_content);
        joinTeamView = view.findViewById(R.id.join_team_content);

        setHasOptionsMenu(true);

        setupViewForMainContent(mainContentView);
        setupViewForNewTeam(newTeamView);
    }

    private void setupViewForMainContent(View view) {

        journeyBeforeStartCard = view.findViewById(R.id.journey_before_start_card);
        myTeamCard = view.findViewById(R.id.my_team_card);

    }

    private void setupViewForNewTeam(View view) {
        createTeamButton = view.findViewById(R.id.create_team_button);
        if (createTeamButton != null) {
            createTeamButton.setOnClickListener(onClickListener);
        }

        cancelCreateTeamButton = view.findViewById(R.id.cancel_create_team_button);
        if (cancelCreateTeamButton != null) {
            cancelCreateTeamButton.setOnClickListener(onClickListener);
        }

        teamNameEditText = view.findViewById(R.id.team_name);
        if (teamNameEditText != null) {
            teamNameEditText.addTextChangedListener(creatTeamEditWatcher);
        }

        teamLeadNameEditText = view.findViewById(R.id.team_lead_name);
        if (teamLeadNameEditText != null) {
            teamLeadNameEditText.setText(SharedPreferencesUtil.getUserFullName());
            teamLeadNameEditText.addTextChangedListener(creatTeamEditWatcher);
        }
    }

    private void setupViewForJoinTeam(View view) {
        if (teamsAdapter == null) {
            teamsAdapter = new TeamsAdapter(this);
        }

        teamsListRecyclerView = view.findViewById(R.id.teams_list);
        teamsListRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        teamsListRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));

        teamsListRecyclerView.addItemDecoration(new ListPaddingDecoration(getContext()));

        teamsListRecyclerView.setAdapter(teamsAdapter);

        joinTeamButton = view.findViewById(R.id.join_team_button);

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
        if (newTeamView == null) {
            return;
        }
        newTeamView.setVisibility(View.VISIBLE);
        enableCreateTeamButton();

        if (mainContentView != null) {
            mainContentView.setVisibility(View.GONE);
        }
    }

    @Override
    public void hideCreateNewTeamView() {
        if (newTeamView != null) {
            closeKeyboard();
            newTeamView.setVisibility(View.GONE);
        }
        mainContentView.setVisibility(View.VISIBLE);
    }

    @Override
    public void teamCreated(Team team) {
        this.teamId = team.getId();
        showMessage("New team " + teamId + " created");
        // challengePresenter. update teamLeader();
        hideCreateNewTeamView();
        showJoinTeamsButton.setEnabled(true);
        challengePresenter.getTeams();
    }

    @Override
    public void showTeamList(List<Team> teams) {

        setupViewForJoinTeam(joinTeamView);

        if (joinTeamView == null) {
            showMessage("Joining team coming soon");
            return;
        }
        teamsAdapter.clearTeamSelectionPosition(); //TODO: if we have a team previously selected, find its position and select that
        teamsListRecyclerView.scrollToPosition(0);
        teamsAdapter.updateTeamsData(teams);

        joinTeamView.setVisibility(View.VISIBLE);
        mainContentView.setVisibility(View.GONE);
    }

    private void teamSelectedToJoin() {
        Team selectedTeam = teamsAdapter.getSelectedTeam();
        if (selectedTeam == null) {
            showMessage("Please select a team to join");
            return;
        }
        //TODO ensure capacity, if not show message

        challengePresenter.assignParticipantToTeam(facebookId, selectedTeam.getId());
    }

    private void closeTeamSelection() {
        mainContentView.setVisibility(View.VISIBLE);
        joinTeamView.setVisibility(View.GONE);
        mHostingParent.showToolbarUpAffordance(false);
    }

    @Override
    public void participantJoinedTeam(String fbid, int teamId) {
        SharedPreferencesUtil.saveMyTeamId(teamId);
        // refresh the objects
        closeTeamSelection();
        //TODO: see if these refresh is needed, perhaps it is sufficient for presenter to update its data using the response
        challengePresenter.getParticipant(fbid);
        challengePresenter.getTeam(teamId);
        challengePresenter.getTeams();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface FragmentHost {
        void onChallengeFragmentInteraction(Uri uri);

        void showToolbarUpAffordance(boolean showFlag);

        void setViewTitle(String title);
    }
}
