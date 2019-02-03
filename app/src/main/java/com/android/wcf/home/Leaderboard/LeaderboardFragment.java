package com.android.wcf.home.Leaderboard;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.wcf.R;
import com.android.wcf.base.BaseFragment;
import com.android.wcf.helper.view.EndOffsetItemDecoration;
import com.android.wcf.helper.view.StartOffsetItemDecoration;
import com.android.wcf.model.Team;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentHost} interface
 * to handle interaction events.
 * Use the {@link LeaderboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LeaderboardFragment extends BaseFragment implements LeaderboardMvp.LeaderboardView, LeaderboardAdapterMvp.Host {
    private static final String TAG = LeaderboardFragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_MY_TEAM_ID = "my_team_id";

    private int myTeamId;

    private FragmentHost mFragmentHost;
    private LeaderboardMvp.Presenter leaderboardPresenter;

    TextView myTeamRankTextView;
    TextView myTeamNameTextView;
    TextView myTeamDistanceCompleted;
    TextView myTeamAmountRaised;
    Spinner leaderboardSortSelector;

    private RecyclerView leaderboardRecyclerView = null;
    private LeaderboardAdapter leaderboardAdapter = null;

    public LeaderboardFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param myTeamId int.
     * @return A new instance of fragment LeaderboardFragment.
     */

    public static LeaderboardFragment newInstance(int myTeamId) {
        LeaderboardFragment fragment = new LeaderboardFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MY_TEAM_ID, myTeamId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            myTeamId = getArguments().getInt(ARG_MY_TEAM_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        setupView(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        if (leaderboardPresenter == null) {
            leaderboardPresenter = new LeaderboardPresenter(this);
        }
        if (myTeamId > 0) {
            getLeaderboard();
        }
        else {
            showLeaderboardIsEmpty();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentHost) {
            mFragmentHost = (FragmentHost) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentHost");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFragmentHost = null;
    }

    private void getLeaderboard(){
        leaderboardPresenter.getTeams();
    }

    public void setMyTeamId(int teamId) {
        myTeamId = teamId;
    }

    @Override
    public void showLeaderboard(List<Team> teams) {
        if (leaderboardAdapter == null) {
            leaderboardAdapter = new LeaderboardAdapter(this);
        }

        Team myTeam = getMyTeamData(teams);

        //TODO: get the proper model for dashboard
        myTeamRankTextView.setText("9");
        myTeamNameTextView.setText(myTeam.getName());
        myTeamDistanceCompleted.setText("116");
        myTeamAmountRaised.setText("65.20");

        leaderboardRecyclerView.setAdapter(leaderboardAdapter);
        leaderboardAdapter.getPresenter().updateLeaderboardData(teams);
        leaderboardRecyclerView.scrollToPosition(0);
    }

    private void setupView(View view) {
        View myTeamLeaderboadItem = view.findViewById(R.id.my_team_leaderboard_item);
        myTeamRankTextView = myTeamLeaderboadItem.findViewById(R.id.team_rank);
        myTeamNameTextView = myTeamLeaderboadItem.findViewById(R.id.team_name);
        myTeamDistanceCompleted = myTeamLeaderboadItem.findViewById(R.id.team_distance_completed);
        myTeamAmountRaised = myTeamLeaderboadItem.findViewById(R.id.team_amount_raised);

        leaderboardSortSelector = view.findViewById(R.id.leaderboard_sort_selector_spinner);
        leaderboardRecyclerView = view.findViewById(R.id.leaderboard_team_list);
        leaderboardRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        leaderboardRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));

        int dp = 1;
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.density);

        int offsetPx = Math.round(px);

        leaderboardRecyclerView.addItemDecoration(new StartOffsetItemDecoration(offsetPx));
        leaderboardRecyclerView.addItemDecoration(new EndOffsetItemDecoration(offsetPx));

        if (leaderboardAdapter == null) {
            leaderboardAdapter = new LeaderboardAdapter(this);
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.leaderboard_sort_types,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        leaderboardSortSelector.setAdapter(adapter);


        leaderboardSortSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String sortColumn = "miles";
                switch (position) {
                    case 0:
                        leaderboardPresenter.sortTeamsBy(LeaderboardPresenter.SORT_COLUMN_MILES, LeaderboardPresenter.SORT_MODE_DESCENDING);
                    break;
                    case 1:
                        leaderboardPresenter.sortTeamsBy(LeaderboardPresenter.SORT_COLUMN_AMOUNT, LeaderboardPresenter.SORT_MODE_DESCENDING);
                        break;
                    case 2:
                        leaderboardPresenter.sortTeamsBy(LeaderboardPresenter.SORT_COLUMN_AMOUNT, LeaderboardPresenter.SORT_MODE_ASCENDING);
                        break;
                    case 3:
                        leaderboardPresenter.sortTeamsBy(LeaderboardPresenter.SORT_COLUMN_AMOUNT, LeaderboardPresenter.SORT_MODE_DESCENDING);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private Team getMyTeamData(List<Team> teams) {
        //TODO find my teamId's data
        return teams.get(2);
    }

    @Override
    public void showLeaderboardIsEmpty() {

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mFragmentHost != null) {
            mFragmentHost.onLeaderboardFragmentInteraction(uri);
        }
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
        void onLeaderboardFragmentInteraction(Uri uri);
        void showToolbarUpAffordance(boolean showFlag);
        void setViewTitle(String title);

    }
}
