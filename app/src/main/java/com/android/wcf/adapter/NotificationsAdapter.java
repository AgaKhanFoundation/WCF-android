package com.android.wcf.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.wcf.R;
import com.android.wcf.model.Notification;
import com.android.wcf.utils.CircleImageView;

import java.util.ArrayList;
import java.util.List;

public class NotificationsAdapter extends BaseAdapter {

    private Context mContext;
    private NotificationsAdapter.ViewHolder viewHolder;
    private List<Notification> arrNotification = new ArrayList<>();

    public NotificationsAdapter(Context mContext, List<Notification> arr_model) {
        this.mContext = mContext;
        this.arrNotification = arr_model;
    }

    @Override
    public int getCount() {
        return arrNotification.size();
    }

    @Override
    public Object getItem(int position) {
        return arrNotification.get(position);
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
            convertView = inflater.inflate(R.layout.row_notification, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (NotificationsAdapter.ViewHolder) convertView.getTag();
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
