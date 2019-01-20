package com.android.wcf.home.Campaign;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.wcf.R;
import com.android.wcf.base.BaseFragment;
import com.android.wcf.model.Event;
import com.android.wcf.model.Team;
import com.android.wcf.utils.SharedPreferencesUtil;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CampaignFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CampaignFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CampaignFragment extends BaseFragment implements CampaignMvp.CampaignView {

    private static final String TAG = CampaignFragment.class.getSimpleName();

    /* the fragment initialization parameters */
    private static final String ARG_MY_FACEBOOK_ID = "my_fbid";
    private static final String ARG_MY_ACTIVE_EVENT_ID = "my_active_event_id";
    private static final String ARG_MY_TEAM_ID = "my_team_id";

    /* class constants */
    public static final int MIN_TEAM_NAME_SIZE = 3;
    public static final int MIN_TEAM_LEAD_NAME_SIZE = 0;

    /* UI elements */
    private Button showCreateTeamButton = null;
    private Button createTeamButton = null;
    private Button cancelCreateTeamButton = null;
    private Button showJoinTeamsButton = null;
    private EditText teamNameEditText = null;
    private EditText teamLeadNameEditText = null;

    /* non-ui class properties */
    private String facebookId;
    private int activeEventId;
    private int teamId;


    private OnFragmentInteractionListener mListener;

    private CampaignMvp.Presenter campaignPresenter = new CampaignPresenter(this);

    public CampaignFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param fbid   facebookId.
     * @param teamId teamId.
     * @return A new instance of fragment CampaignFragment.
     */
    public static CampaignFragment newInstance(String fbid, int eventId, int teamId) {
        CampaignFragment fragment = new CampaignFragment();
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
        View fragmentView = inflater.inflate(R.layout.fragment_campaign, container, false);

        setupView(fragmentView);
        return fragmentView;

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        campaignPresenter.getEvent(activeEventId);
    }

    @Override
    public void onResume() {
        super.onResume();
        campaignPresenter.getTeams(); //TODO: next version, we will have to associate teams to event
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onCampaignFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onFacebookIdMissing() {
        Toast.makeText(getContext(), "FacebookId needed. Please login", Toast.LENGTH_SHORT).show();
        //TODO: navigate to login activity and finish Home activity
    }

    @Override
    public void showJourney(Event event) {

    }

    @Override
    public void enableJoinExistingTeam(boolean enabledFlag) {
        showJoinTeamsButton.setEnabled(enabledFlag);

    }

    @Override
    public void enableShowCreateTeam(boolean enabledFlag) {
        showCreateTeamButton.setEnabled(enabledFlag);

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
                    campaignPresenter.showCreateTeamClick();
                    break;
                case R.id.create_team_button:
                    closeKeyboard();
                    campaignPresenter.createTeamClick(teamNameEditText.getText().toString());
                    break;
                case R.id.cancel_create_team_button:
                    closeKeyboard();
                    campaignPresenter.cancelCreateTeamClick();
                    break;
                case R.id.show_join_teams_button:
                    campaignPresenter.showTeamsToJoinClick();
                    break;
//                case R.id.join_team_button:
//                    teamSelectedToJoin();
//                    break;
//                case R.id.cancel_join_team:
//                    closeTeamSelection();
//                    break;

            }
        }
    };

    void setupView(View view) {
        showCreateTeamButton = view.findViewById(R.id.show_create_team_button);
        if (showCreateTeamButton != null) {
            showCreateTeamButton.setOnClickListener(onClickListener);
        }

        createTeamButton = view.findViewById(R.id.create_team_button);
        if (createTeamButton != null) {
            createTeamButton.setOnClickListener(onClickListener);
        }

        cancelCreateTeamButton = view.findViewById(R.id.cancel_create_team_button);
        if (cancelCreateTeamButton != null) {
            cancelCreateTeamButton.setOnClickListener(onClickListener);
        }

        showJoinTeamsButton = view.findViewById(R.id.show_join_teams_button);
        if (showJoinTeamsButton != null) {
            showJoinTeamsButton.setOnClickListener(onClickListener);
        }

        teamNameEditText = view.findViewById(R.id.team_name);
        if (teamNameEditText != null) {
            teamNameEditText.addTextChangedListener(creatTeamEditWatcher);
        }

        teamLeadNameEditText = view.findViewById(R.id.team_lead_name);
        if (teamLeadNameEditText != null) {
            teamLeadNameEditText.addTextChangedListener(creatTeamEditWatcher);
        }
    }

    @Override
    public void showCreateNewTeamView() {
        View mainView = getView().findViewById(R.id.main_content);
        View view = getView().findViewById(R.id.new_team_content);
        if (view != null) {
            view.setVisibility(View.VISIBLE);
            enableCreateTeamButton();
            mainView.setVisibility(View.GONE);
        }
    }

    @Override
    public void hideCreateNewTeamView() {
        View mainView = getView().findViewById(R.id.main_content);
        View view = getView().findViewById(R.id.new_team_content);
        if (view != null) {
            closeKeyboard();
            mainView.setVisibility(View.VISIBLE);
            view.setVisibility(View.GONE);
        }
    }

    @Override
    public void teamCreated(Team team) {
        this.teamId = team.getId();
        showMessage("New team " + teamId + " created");
        // campaignPresenter. update teamLeader();
        hideCreateNewTeamView();
        showJoinTeamsButton.setEnabled(true);
        campaignPresenter.getTeams();
    }

    @Override
    public void showTeamList(List<Team> teams) {
        //TODO: Show panel of teams and enable selecting a team if it has capacity for additional members
        showMessage("Joining team comming soon");
    }

    private void teamSelectedToJoin() {
        int teamId = 1; // get the teamId from selected row
        campaignPresenter.assignParticipantToTeam(facebookId, teamId);
    }

    private void closeTeamSelection() {
        //TODO: disable and hide team list panel
    }


    @Override
    public void participantJoinedTeam(String fbid, int teamId) {
        SharedPreferencesUtil.saveMyTeamId(teamId);
        // refresh the objects
        //TODO: see if these refresh is needed, perhaps it is sufficient for presenter to update its data using the response
        campaignPresenter.getParticipant(fbid);
        campaignPresenter.getTeam(teamId);
        campaignPresenter.getTeams();
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onCampaignFragmentInteraction(Uri uri);
    }
}
