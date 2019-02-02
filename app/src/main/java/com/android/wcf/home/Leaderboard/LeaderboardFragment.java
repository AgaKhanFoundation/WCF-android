package com.android.wcf.home.Leaderboard;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.wcf.R;
import com.android.wcf.base.BaseFragment;
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
public class LeaderboardFragment extends BaseFragment implements LeaderboardMvp.LeaderboardView {
    private static final String TAG = LeaderboardFragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_MY_TEAM_ID = "my_team_id";

    private int myTeamId;

    private FragmentHost mFragmentHost;
    private LeaderboardMvp.Presenter leaderboardPresenter;

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
        return inflater.inflate(R.layout.fragment_leaderboard, container, false);
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
