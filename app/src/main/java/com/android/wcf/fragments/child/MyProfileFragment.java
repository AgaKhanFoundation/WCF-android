package com.android.wcf.fragments.child;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.wcf.R;
import com.android.wcf.activity.MainTabActivity;
import com.android.wcf.utils.AppUtil;
import com.android.wcf.utils.CircleImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Malik Khoja on 4/27/2017.
 */

public class MyProfileFragment extends Fragment implements View.OnClickListener {

    private View mView;
    private Context mContext;
    @BindView(R.id.imageMyProfile)
    CircleImageView imageMyProfile;
    @BindView(R.id.textMyName)
    TextView textMyName;
    @BindView(R.id.textTeamName)
    TextView textTeamName;
    @BindView(R.id.spinnerSorting)
    Spinner spinnerSorting;
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
    @BindView(R.id.imageDaysCount)
    ImageView imageDaysCount;
    @BindView(R.id.textDays)
    TextView textDays;
    @BindView(R.id.textMilestone)
    TextView textMilestone;
    @BindView(R.id.textCurrentSupportersCount)
    TextView textCurrentSupportersCount;
    @BindView(R.id.textNameFirstUser)
    TextView textNameFirstUser;
    @BindView(R.id.textCurrentMoneyFirstUser)
    TextView textCurrentMoneyFirstUser;
    @BindView(R.id.textPledgedFirstUser)
    TextView textPledgedFirstUser;
    @BindView(R.id.textNameSecondUser)
    TextView textNameSecondUser;
    @BindView(R.id.textCurrentMoneySecondUser)
    TextView textCurrentMoneySecondUser;
    @BindView(R.id.textPledgedSecondUser)
    TextView textPledgedSecondUser;
    @BindView(R.id.textCurrentSupporterSeeMore)
    TextView textCurrentSupporterSeeMore;
    @BindView(R.id.textPastEventsCount)
    TextView textPastEventsCount;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_myprofile, container, false);
        mContext = getActivity();
        ButterKnife.bind(this, mView);
        ((MainTabActivity) getActivity()).mTextBack.setVisibility(View.GONE);
        ((MainTabActivity) getActivity()).mImageSettings.setVisibility(View.VISIBLE);
        ((MainTabActivity) getActivity()).textAppName.setText(getResources().getString(R.string.myprofileheadertext));
        textCurrentSupporterSeeMore.setOnClickListener(this);
        return mView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textCurrentSupporterSeeMore:
                FragmentTransaction ft = getParentFragment()
                        .getChildFragmentManager().beginTransaction();
                SupportersSponsersFragment supportersSponsersFragment = new SupportersSponsersFragment();
                ft.replace(R.id.frame_container_child, supportersSponsersFragment, AppUtil.SPONSERERS_FRAGMENT);
                ft.addToBackStack(AppUtil.MYPROFILE_FRAGMENT);
                ft.commit();
                break;
        }
    }
}
