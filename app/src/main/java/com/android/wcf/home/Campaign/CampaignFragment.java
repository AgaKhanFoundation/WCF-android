package com.android.wcf.home.Campaign;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    // the fragment initialization parameters
    private static final String ARG_MY_FACEBOOK_ID = "my_fbid";
    private static final String ARG_MY_ACTIVE_EVENT_ID = "my_active_event_id";
    private static final String ARG_MY_TEAM_ID = "my_team_id";

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
     * @param fbid facebookId.
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
        return inflater.inflate(R.layout.fragment_campaign, container, false);
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
    public void enableCreateTeam() {

    }

    @Override
    public void enableJoinExistingTeam(boolean showFlag) {

    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.create_team_button:
                    campaignPresenter.onCreateTeamClick();
                    break;
//                case R.id.show_teams:
//                    campaignPresenter.onShowTeamsClick();
//                    break;
                case R.id.join_team_button:
                    teamSelectedToJoin();
                    break;
//                case R.id.cancel_join_team:
//                    closeTeamSelection();
//                    break;

            }
        }
    };

    @Override
    public void showCreateNewTeamView() {
        //TODO: show panel/modeal to create team
    }

    @Override
    public void showTeamList(List<Team> teams) {
        //TODO: Show panel of teams and enable selecting a team if it has capacity for additional members
    }

    private void teamSelectedToJoin() {
        int teamId = 1; // get the teamId from selected row
        campaignPresenter.assignParticipantToTeam(facebookId, teamId);
    }

    private void closeTeamSelection() {
        //TODO: disable and hide team list panel
    }


    @Override
    public void onParticipantAssignedToTeam(String fbid, int teamId) {
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
