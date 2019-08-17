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
import com.android.wcf.helper.SharedPreferencesUtil;
import com.android.wcf.helper.view.ListPaddingDecoration;
import com.android.wcf.model.Constants;

import java.util.List;


public class LeaderboardFragment extends BaseFragment implements LeaderboardMvp.LeaderboardView, LeaderboardAdapterMvp.Host {
    private static final String TAG = LeaderboardFragment.class.getSimpleName();

    private int myTeamId;

    private LeaderboardMvp.Host mFragmentHost;
    private LeaderboardMvp.Presenter leaderboardPresenter;

    View emptyLeaderboardView;
    Button refreshLeaderboardButton;

    View myTeamLeaderboardContainer;
    View myTeamLeaderboardItem;
    TextView myTeamRankTextView;
    TextView myTeamNameTextView;
    TextView myTeamDistanceCompleted;
    TextView myTeamAmountRaised;
    
    Spinner leaderboardSortSelector;
    private RecyclerView leaderboardRecyclerView = null;
    private LeaderboardAdapter leaderboardAdapter = null;

    public LeaderboardFragment() {
    }

    public static LeaderboardFragment newInstance() {
        LeaderboardFragment fragment = new LeaderboardFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_leaderboard, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myTeamId = SharedPreferencesUtil.getMyTeamId();
        setupView(view);
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
        mFragmentHost.setToolbarTitle(getString(R.string.nav_leaderboard), false);

        if (leaderboardPresenter == null) {
            leaderboardPresenter = new LeaderboardPresenter(this);
        }
        leaderboardPresenter.getLeaderboard();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LeaderboardMvp.Host) {
            mFragmentHost = (LeaderboardMvp.Host) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement LeaderboardMvp.Host");
        }
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
                    leaderboardPresenter.getLeaderboard();
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
    public void showLeaderboard(List<LeaderboardTeam> leaderboard) {
        if (leaderboardAdapter == null) {
            leaderboardAdapter = new LeaderboardAdapter(this);
        }
        emptyLeaderboardView.setVisibility(View.GONE);

        LeaderboardTeam myLeaderboardTeam = getMyTeamData(leaderboard);
        if (myLeaderboardTeam == null) {
            myTeamLeaderboardContainer.setVisibility(View.GONE);
        }
        else {
            myTeamLeaderboardContainer.setVisibility(View.VISIBLE);
            myTeamRankTextView.setText(myLeaderboardTeam.getRank() + "");
            myTeamNameTextView.setText(myLeaderboardTeam.getName());
            myTeamDistanceCompleted.setText(String.format("%,6d", (int) myLeaderboardTeam.getDistanceCompleted()));
            myTeamAmountRaised.setText(String.format("$%,.02f", myLeaderboardTeam.getAmountAccrued()));
        }

        leaderboardRecyclerView.setAdapter(leaderboardAdapter);
        leaderboardAdapter.getPresenter().updateLeaderboardData(leaderboard);
        leaderboardRecyclerView.scrollToPosition(0);
    }

    private void setupView(View view) {

        emptyLeaderboardView = view.findViewById(R.id.empty_view_container);
        refreshLeaderboardButton = emptyLeaderboardView.findViewById(R.id.refresh_leaderboard);
        refreshLeaderboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leaderboardPresenter.getLeaderboard();
            }
        });
        emptyLeaderboardView.setVisibility(View.GONE);

        myTeamLeaderboardContainer = view.findViewById(R.id.my_team_leaderboard_container);
        myTeamLeaderboardItem = myTeamLeaderboardContainer.findViewById(R.id.my_team_leaderboard_item);
        myTeamRankTextView = myTeamLeaderboardItem.findViewById(R.id.team_rank);
        myTeamNameTextView = myTeamLeaderboardItem.findViewById(R.id.team_name);
        myTeamDistanceCompleted = myTeamLeaderboardItem.findViewById(R.id.team_distance_completed);
        myTeamAmountRaised = myTeamLeaderboardItem.findViewById(R.id.team_amount_raised);

        leaderboardSortSelector = view.findViewById(R.id.leaderboard_sort_selector_spinner);
        leaderboardRecyclerView = view.findViewById(R.id.leaderboard_team_list);
        leaderboardRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        leaderboardRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));

        leaderboardRecyclerView.addItemDecoration(new ListPaddingDecoration(getContext()));

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

    private LeaderboardTeam getMyTeamData(List<LeaderboardTeam> leaderboard) {
        for (LeaderboardTeam team : leaderboard) {
            if (team.getId() == myTeamId) {
                return team;
            }
        }
        return null;
    }

    @Override
    public void showLeaderboardIsEmpty() {
        emptyLeaderboardView.setVisibility(View.VISIBLE);
    }

}
