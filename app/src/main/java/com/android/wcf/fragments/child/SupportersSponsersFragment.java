package com.android.wcf.fragments.child;

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
import com.android.wcf.model.CurrentSupportersModel;
import com.android.wcf.utils.ExpandedListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Malik Khoja on 5/8/2017.
 */

public class SupportersSponsersFragment extends Fragment {

    private View mView;
    private Context mContext;
    @BindView(R.id.listCurrentSupporters)
    ExpandedListView listCurrentSupporters;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_supporterssponserer, container, false);
        mContext = getActivity();
        ButterKnife.bind(this, mView);
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

}