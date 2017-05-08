package com.android.wcf.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.wcf.R;
import com.android.wcf.model.CurrentSupportersModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Malik Khoja on 5/8/2017.
 */

public class CurrentSupportersAdapter extends BaseAdapter {

    private Context mContext;
    private CurrentSupportersAdapter.ViewHolder viewHolder;
    private List<CurrentSupportersModel> arrLeaderBoard = new ArrayList<>();

    public CurrentSupportersAdapter(Context mContext, List<CurrentSupportersModel> arr_model) {
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
            convertView = inflater.inflate(R.layout.row_current_supporters, parent, false);
            viewHolder = new CurrentSupportersAdapter.ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (CurrentSupportersAdapter.ViewHolder) convertView.getTag();
        }


        return convertView;

    }

    public class ViewHolder {
        // Separate class to initialize the widgets of the view.
        @BindView(R.id.textNameUser)
        TextView textNameUser;
        @BindView(R.id.textCurrentMoneyUser)
        TextView textCurrentMoneyUser;
        @BindView(R.id.textPledgedUser)
        TextView textPledgedUser;

        public ViewHolder(final View v) {
            ButterKnife.bind(this, v);
        }
    }
}

