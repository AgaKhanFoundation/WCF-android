package com.android.wcf.home.leaderboard;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.wcf.R;
import com.android.wcf.base.BaseFragment;
import com.android.wcf.helper.DistanceConverter;
import com.android.wcf.helper.view.ListPaddingDecoration;
import com.android.wcf.model.Constants;
import com.android.wcf.model.Event;
import com.android.wcf.model.Participant;

import java.text.DecimalFormat;
import java.util.List;


public class LeaderboardFragment extends BaseFragment implements LeaderboardMvp.LeaderboardView, LeaderboardAdapterMvp.Host {
    private static final String TAG = LeaderboardFragment.class.getSimpleName();

    private int myTeamId = 0;

    private LeaderboardMvp.Host mFragmentHost;
    private LeaderboardMvp.Presenter leaderboardPresenter;

    View leaderboardMainContainer;
    View emptyLeaderboardView;
    View medalwinnersContainer;
    View leaderboardListContainer;

    Spinner leaderboardSortSelector;
    private RecyclerView leaderboardRecyclerView = null;
    private LeaderboardAdapter leaderboardAdapter = null;
    private Event event;

    public LeaderboardFragment() {
    }

    public static LeaderboardFragment newInstance() {
        LeaderboardFragment fragment = new LeaderboardFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (leaderboardPresenter == null) {
            leaderboardPresenter = new LeaderboardPresenter(this );
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_leaderboard, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        Participant participant = getParticipant();
        if (participant != null && participant.getTeamId() != null) {
            myTeamId = participant.getTeamId();
        }
        else {
            myTeamId = 0;
        }
        setupView(view);
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
        mFragmentHost.setToolbarTitle(getString(R.string.nav_leaderboard), false);

        event = getEvent();
        leaderboardPresenter.getLeaderboard(event, myTeamId);
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach");
        super.onAttach(context);
        if (context instanceof LeaderboardMvp.Host) {
            mFragmentHost = (LeaderboardMvp.Host) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement LeaderboardMvp.Host");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        leaderboardPresenter.onStop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFragmentHost = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean handled = super.onOptionsItemSelected(item);
        if (!handled) {
            switch (item.getItemId()) {
                case R.id.menu_item_refresh:
                    handled = true;
                    leaderboardPresenter.refreshLeaderboard(event);
            }
        }
        return handled;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu();
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
    }

    @Override
    public void showMedalWinners(LeaderboardTeam goldTeam, LeaderboardTeam silverTeam, LeaderboardTeam bronzeTeam) {
        DecimalFormat numberFormatter = new DecimalFormat("###,###");
        DecimalFormat decimalFormatter = new DecimalFormat("##,###.##");


        View bronzeContainer = medalwinnersContainer.findViewById(R.id.bronze_team_container);
        TextView teamNameTv = bronzeContainer.findViewById(R.id.bronze_team_name);
        TextView teamMiles = bronzeContainer.findViewById(R.id.bronze_team_miles_completed);
        if (bronzeTeam != null){
           teamNameTv.setText(bronzeTeam.getName());
           double miles = DistanceConverter.distance(bronzeTeam.getStepsCompleted());
            teamMiles.setText(numberFormatter.format(miles) );
        }
        else {
            teamNameTv.setText("");
            teamMiles.setText("");
        }

        View silverContainer = medalwinnersContainer.findViewById(R.id.silver_team_container);
        teamNameTv = silverContainer.findViewById(R.id.silver_team_name);
        teamMiles = silverContainer.findViewById(R.id.silver_team_miles_completed);

        if (silverTeam != null){
            teamNameTv.setText(silverTeam.getName());
            double miles = DistanceConverter.distance(silverTeam.getStepsCompleted());
            teamMiles.setText(numberFormatter.format(miles) );
        }
        else {
            teamNameTv.setText("");
            teamMiles.setText("");
        }

        View goldContainer = medalwinnersContainer.findViewById(R.id.gold_team_container);
        teamNameTv = goldContainer.findViewById(R.id.gold_team_name);
        teamMiles = goldContainer.findViewById(R.id.gold_team_miles_completed);

        if (goldTeam != null) {
            teamNameTv.setText(goldTeam.getName());

            double miles = DistanceConverter.distance(goldTeam.getStepsCompleted());
            teamMiles.setText(numberFormatter.format(miles));
        }
        else {
            teamNameTv.setText("");
            teamMiles.setText("");
        }
        medalwinnersContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void noMedalWinners() {
        showMedalWinners(null, null, null);
    }

    @Override
    public void showLeaderboard(List<LeaderboardTeam> leaderboard) {
        leaderboardAdapter.updateLeaderboardData(leaderboard);
    }

    private void setupView(View view) {

        emptyLeaderboardView = view.findViewById(R.id.empty_view_container);
        emptyLeaderboardView.setVisibility(View.VISIBLE);

        leaderboardMainContainer = view.findViewById(R.id.leaderboard_main_container);
        leaderboardMainContainer.setVisibility(View.GONE);

        medalwinnersContainer = view.findViewById(R.id.leaderboard_medal_winners_container);
        medalwinnersContainer.setVisibility(View.GONE);

        leaderboardListContainer = view.findViewById(R.id.leaderboard_list_container);
        leaderboardListContainer.setVisibility(View.VISIBLE);

        setupLeaderboardSorting(view);
        setupLeaderboardList(view);

    }

    private void setupLeaderboardSorting(View view) {
        leaderboardSortSelector = view.findViewById(R.id.leaderboard_sort_selector_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.leaderboard_sort_types,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        leaderboardSortSelector.setAdapter(adapter);

        leaderboardSortSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {
                    case 0:
                        leaderboardPresenter.sortTeamsBy(LeaderboardTeam.SORT_COLUMN_DISTANCE_COMPLETED, Constants.SORT_MODE_DESCENDING);
                        break;
                    case 1:
                        leaderboardPresenter.sortTeamsBy(LeaderboardTeam.SORT_COLUMN_AMOUNT_ACCRUED, Constants.SORT_MODE_DESCENDING);
                        break;
                    case 2:
                        leaderboardPresenter.sortTeamsBy(LeaderboardTeam.SORT_COLUMN_NAME, Constants.SORT_MODE_ASCENDING);
                        break;
                    case 3:
                        leaderboardPresenter.sortTeamsBy(LeaderboardTeam.SORT_COLUMN_NAME, Constants.SORT_MODE_DESCENDING);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void setupLeaderboardList(View view) {

        leaderboardAdapter = new LeaderboardAdapter(this, myTeamId);
        leaderboardRecyclerView = view.findViewById(R.id.leaderboard_team_list);
        leaderboardRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        leaderboardRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));

        leaderboardRecyclerView.addItemDecoration(new ListPaddingDecoration(getContext()));
        leaderboardRecyclerView.setAdapter(leaderboardAdapter);
    }

    @Override
    public void showLeaderboardIsEmpty() {
        emptyLeaderboardView.setVisibility(View.VISIBLE);
        leaderboardMainContainer.setVisibility(View.GONE);
    }

    @Override
    public void hideEmptyLeaderboardView() {
        emptyLeaderboardView.setVisibility(View.GONE);
        leaderboardMainContainer.setVisibility(View.VISIBLE);
    }

    private LeaderboardTeam getMyTeamData(List<LeaderboardTeam> leaderboard) {
        for (LeaderboardTeam team : leaderboard) {
            if (team.getId() == myTeamId) {
                return team;
            }
        }
        return null;
    }

}
