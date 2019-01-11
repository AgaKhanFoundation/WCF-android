package com.android.wcf.adapter;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.wcf.R;
import com.android.wcf.modelOld.LeaderBoardModel;
import com.android.wcf.utils.CircleImageView;

import java.util.ArrayList;
import java.util.List;

public class LeaderBoardMyTeamAdapter extends BaseAdapter {

    private Context mContext;
    private ViewHolder viewHolder;
    private List<LeaderBoardModel> arrLeaderBoard = new ArrayList<>();

    public LeaderBoardMyTeamAdapter(Context mContext, List<LeaderBoardModel> arr_model) {
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
            convertView = inflater.inflate(R.layout.row_leaderboard_myteam, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textSerialNumber.setText(position + 1 + ".");


        return convertView;

    }

    public class ViewHolder {
        // Separate class to initialize the widgets of the view.
        TextView textSerialNumber;
        CircleImageView imageViewProfile;
        TextView textName;
        TextView textMiles;
        TextView textMoney;

        public ViewHolder(final View v) {
            textSerialNumber = v.findViewById(R.id.textSerialNumber);
            imageViewProfile = v.findViewById(R.id.imageView_profile);
            textName = v.findViewById(R.id.textName);
            textMiles = v.findViewById(R.id.textMiles);
            textMoney = v.findViewById(R.id.textMoney);
        }
    }
}
