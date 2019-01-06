package com.android.wcf.adapter;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.wcf.R;
import com.android.wcf.modelOld.PastEventModel;

import java.util.ArrayList;
import java.util.List;


public class PastEventAdapter extends BaseAdapter {

    private Context mContext;
    private PastEventAdapter.ViewHolder viewHolder;
    private List<PastEventModel> arrLeaderBoard = new ArrayList<>();

    public PastEventAdapter(Context mContext, List<PastEventModel> arr_model) {
        this.mContext = mContext;
        this.arrLeaderBoard = arr_model;
    }

    @Override
    public int getCount() {
        return arrLeaderBoard.size();
    }

    @Override
    public Object getItem(int position) {
        return arrLeaderBoard.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // inflate the layout
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            // inflate the layout to set the data
            convertView = inflater.inflate(R.layout.row_past_events, parent, false);
            viewHolder = new PastEventAdapter.ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (PastEventAdapter.ViewHolder) convertView.getTag();
        }
        return convertView;

    }

    public class ViewHolder {
        // Separate class to initialize the widgets of the view.
        ImageView imageEventName;
        TextView textEventName;
        TextView textEventMonthYear;
        TextView textTeamNameHeading;
        TextView textPastEventTeamName;
        TextView textMoney;
        TextView textMiles;

        public ViewHolder(final View v) {
            imageEventName = v.findViewById(R.id.imageEventName);
            textEventName = v.findViewById(R.id.textEventName);
            textEventMonthYear = v.findViewById(R.id.textEventMonthYear);
            textTeamNameHeading = v.findViewById(R.id.textTeamNameHeading);
            textPastEventTeamName = v.findViewById(R.id.textPastEventTeamName);
            textMoney = v.findViewById(R.id.textMoney);

            textMiles = v.findViewById(R.id.textMiles);
        }
    }
}

