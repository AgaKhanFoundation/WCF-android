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

import com.android.wcf.R;
import com.android.wcf.activity.MainTabActivity;
import com.android.wcf.adapter.CurrentSupportersAdapter;
import com.android.wcf.modelOld.CurrentSupportersModel;
import com.android.wcf.utils.ExpandedListView;

import java.util.ArrayList;
import java.util.List;

public class SupportersSponsersFragment extends Fragment {

    private View mView;
    private Context mContext;
    ExpandedListView listCurrentSupporters;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_supporterssponserer, container, false);
        mContext = getActivity();
       setupView(mView);
        listCurrentSupporters.setExpanded(true);
        ((MainTabActivity) getActivity()).mTextBack.setVisibility(View.VISIBLE);
        ((MainTabActivity) getActivity()).mImageSettings.setVisibility(View.GONE);
        ((MainTabActivity) getActivity()).textAppName.setText(getResources().getString(R.string.supporters));
        populateData();
        return mView;
    }

    private void populateData() {
        List<CurrentSupportersModel> arrCurrentSupporters = new ArrayList<>();
        CurrentSupportersModel currentSupportersModel;
        for (int i = 0; i < 7; i++) {
            currentSupportersModel = new CurrentSupportersModel();
            currentSupportersModel.setName("Jenny Fields");
            currentSupportersModel.setCurrentMoney("$75");
            currentSupportersModel.setPledged("Pledged $100");
            arrCurrentSupporters.add(currentSupportersModel);
        }
        listCurrentSupporters.setAdapter(new CurrentSupportersAdapter(mContext, arrCurrentSupporters));
    }

    protected void setupView(View v) {
        listCurrentSupporters = v.findViewById(R.id.listCurrentSupporters);
    }
}