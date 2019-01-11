package com.android.wcf.fragments.child;
/**
 * Copyright © 2017 Aga Khan Foundation
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/
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
        ((MainTabActivity) getActivity()).textAppName.setText(getResources().getString(R.string.tabMyTeam));
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
