package com.android.wcf.obsolete.fragments.child;
/**
 * Copyright © 2017 Aga Khan Foundation
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p>
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * <p>
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p>
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * <p>
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
import com.android.wcf.obsolete.activity.MainTabActivity;
import com.android.wcf.obsolete.adapter.PastEventAdapter;
import com.android.wcf.obsolete.modelOld.PastEventModel;
import com.android.wcf.utils.ExpandedListView;

import java.util.ArrayList;
import java.util.List;

public class PastEventFragment extends Fragment {

    private View mView;
    private Context mContext;
    ExpandedListView listPastEvents;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_pastevent, container, false);
        mContext = getActivity();
        setupView(mView);
        listPastEvents.setExpanded(true);
        ((MainTabActivity) getActivity()).mTextBack.setVisibility(View.VISIBLE);
        ((MainTabActivity) getActivity()).mImageSettings.setVisibility(View.GONE);
        ((MainTabActivity) getActivity()).textAppName.setText(getResources().getString(R.string.pastevents));
        populateData();
        return mView;
    }

    private void setupView(View view) {
        listPastEvents = view.findViewById(R.id.listPastEvents);
    }

    private void populateData() {
        List<PastEventModel> eventModelArrayList = new ArrayList<>();
        PastEventModel pastEventModel;
        for (int i = 0; i < 7; i++) {
            pastEventModel = new PastEventModel();
            pastEventModel.setEventImageLink("");
            pastEventModel.setEventName("");
            pastEventModel.setEventMonthYear("");
            pastEventModel.setEventTeamName("");
            pastEventModel.setEventMoney("");
            pastEventModel.setEventMiles("");
            eventModelArrayList.add(pastEventModel);
        }
        listPastEvents.setAdapter(new PastEventAdapter(mContext, eventModelArrayList));
    }
}

