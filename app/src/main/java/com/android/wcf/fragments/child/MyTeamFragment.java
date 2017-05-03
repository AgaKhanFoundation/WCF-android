package com.android.wcf.fragments.child;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.wcf.R;
import com.android.wcf.activity.MainTabActivity;
import com.android.wcf.adapter.LeaderBoardMyTeamAdapter;
import com.android.wcf.model.LeaderBoardModel;
import com.android.wcf.utils.CircleImageView;
import com.android.wcf.utils.ExpandedListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Malik Khoja on 4/27/2017.
 */

public class MyTeamFragment extends Fragment {

    private View mView;
    private Context mContext;
    @BindView(R.id.imageTeamProfile)
    CircleImageView teamProfileImage;
    @BindView(R.id.textTeamName)
    TextView textTeamName;
    @BindView(R.id.textNumberofMembers)
    TextView textNumberofMembers;
    @BindView(R.id.textTeamMoneyCurrency)
    TextView textTeamMoneyCurrency;
    @BindView(R.id.textTeamMoney)
    TextView textTeamMoney;
    @BindView(R.id.progressBarMyTeamMiles)
    ProgressBar progressBarMyTeamMiles;
    @BindView(R.id.textCurrentMiles)
    TextView textCurrentMiles;
    @BindView(R.id.textOutofMiles)
    TextView textOutofMiles;
    @BindView(R.id.textMilestoneCount)
    TextView textMilestoneCount;
    @BindView(R.id.spinnerSorting)
    Spinner spinnerSorting;
    @BindView(R.id.listLeaderBoard)
    ExpandedListView listLeaderBoard;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_myteam, container, false);
        mContext = getActivity();
        ButterKnife.bind(this, mView);
        listLeaderBoard.setExpanded(true);
        ((MainTabActivity) getActivity()).mTextBack.setVisibility(View.GONE);
        ((MainTabActivity) getActivity()).mImageSettings.setVisibility(View.VISIBLE);
        populateData();
        return mView;
    }

    private void populateData() {
        List<LeaderBoardModel> arrLeaderBoard = new ArrayList<>();
        LeaderBoardModel leaderBoardModel;
        for (int i = 0; i < 7; i++) {
            leaderBoardModel = new LeaderBoardModel();
            leaderBoardModel.setName("Natasha Patel");
            leaderBoardModel.setTotalMiles("375 miles");
            leaderBoardModel.setTotalMoneyRaised("$423.50");
            arrLeaderBoard.add(leaderBoardModel);
        }
        listLeaderBoard.setAdapter(new LeaderBoardMyTeamAdapter(mContext, arrLeaderBoard));
    }

}
