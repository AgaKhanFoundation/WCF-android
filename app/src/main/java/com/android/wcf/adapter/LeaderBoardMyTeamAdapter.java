package com.android.wcf.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.wcf.R;
import com.android.wcf.model.LeaderBoardModel;
import com.android.wcf.utils.CircleImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Malik Khoja on 5/2/2017.
 */

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
        @BindView(R.id.textSerialNumber)
        TextView textSerialNumber;
        @BindView(R.id.imageView_profile)
        CircleImageView imageViewProfile;
        @BindView(R.id.textName)
        TextView textName;
        @BindView(R.id.textMiles)
        TextView textMiles;
        @BindView(R.id.textMoney)
        TextView textMoney;

        public ViewHolder(final View v) {
            ButterKnife.bind(this, v);
        }
    }
}
