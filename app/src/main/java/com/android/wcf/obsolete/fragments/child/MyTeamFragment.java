package com.android.wcf.obsolete.fragments.child;
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
import com.android.wcf.obsolete.activity.MainTabActivity;
import com.android.wcf.obsolete.adapter.LeaderBoardMyTeamAdapter;
import com.android.wcf.obsolete.modelOld.LeaderBoardModel;
import com.android.wcf.utils.CircleImageView;
import com.android.wcf.utils.ExpandedListView;

import java.util.ArrayList;
import java.util.List;

public class MyTeamFragment extends Fragment {

    private View mView;
    private Context mContext;
    CircleImageView teamProfileImage;
    TextView textTeamName;
    TextView textNumberofMembers;
    TextView textTeamMoneyCurrency;
    TextView textTeamMoney;
    ProgressBar progressBarMyTeamMiles;
    TextView textCurrentMiles;
    TextView textOutofMiles;
    TextView textMilestoneCount;
    Spinner spinnerSorting;
    ExpandedListView listLeaderBoard;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_myteam, container, false);
        mContext = getActivity();
        setupView(mView);
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

    protected void setupView(View v) {
        teamProfileImage = v.findViewById(R.id.imageTeamProfile);
        textTeamName = v.findViewById(R.id.textTeamName);
        textNumberofMembers = v.findViewById(R.id.textNumberofMembers);
        textTeamMoneyCurrency = v.findViewById(R.id.textTeamMoneyCurrency);
        textTeamMoney = v.findViewById(R.id.textTeamMoney);
        progressBarMyTeamMiles = v.findViewById(R.id.progressBarMyTeamMiles);
        textCurrentMiles = v.findViewById(R.id.textCurrentMiles);
        textOutofMiles = v.findViewById(R.id.textOutofMiles);
        textMilestoneCount = v.findViewById(R.id.textMilestoneCount);
        spinnerSorting = v.findViewById(R.id.spinnerSorting);
        listLeaderBoard = v.findViewById(R.id.listLeaderBoard);
    }

}
