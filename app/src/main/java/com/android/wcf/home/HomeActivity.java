package com.android.wcf.home;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.android.wcf.R;
import com.android.wcf.base.BaseActivity;
import com.android.wcf.home.Campaign.CampaignFragment;
import com.android.wcf.home.Dashboard.DashboardFragment;
import com.android.wcf.home.Leaderboard.LeaderboardFragment;
import com.android.wcf.home.Notifications.NotificationsFragment;

public class HomeActivity extends BaseActivity
        implements HomeMvp.HomeView
        , DashboardFragment.OnFragmentInteractionListener
        , CampaignFragment.OnFragmentInteractionListener
        , LeaderboardFragment.OnFragmentInteractionListener
        , NotificationsFragment.OnFragmentInteractionListener {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final String BACK_STACK_ROOT_TAG = "root_fragment";


    private HomePresenter homePresenter;
    private DashboardFragment dashboardFragment;
    private CampaignFragment campaignFragment;
    private LeaderboardFragment leaderboardFragment;
    private NotificationsFragment notificationsFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_dashboard:
                    loadFragment(dashboardFragment, getString(R.string.nav_dashboard));
                    return true;
                case R.id.nav_campaign:
                    loadFragment(campaignFragment, getString(R.string.nav_campaign));
                    return true;
                case R.id.nav_leaderboard:
                    loadFragment(leaderboardFragment, getString(R.string.nav_leaderboard));
                    return true;
                case R.id.nav_notifications:
                    loadFragment(notificationsFragment, getString(R.string.nav_notifications));
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        homePresenter = new HomePresenter(this);

        dashboardFragment = DashboardFragment.newInstance(null, null);
        campaignFragment = CampaignFragment.newInstance(null, null);
        leaderboardFragment = LeaderboardFragment.newInstance(null, null);
        notificationsFragment = NotificationsFragment.newInstance(null, null);

        BottomNavigationView navigation = findViewById(R.id.home_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.nav_dashboard);
    }

    private void loadFragment(Fragment fragment, String title) {
        FragmentManager fm = getSupportFragmentManager();
        // Pop off everything up to and including the current tab
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(BACK_STACK_ROOT_TAG)
                .commit();

        setTitle(title);
    }

    private void setTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(title);
    }

    @Override
    public void onDashboardFragmentInteraction(Uri uri) {

    }

    @Override
    public void onCampaignFragmentInteraction(Uri uri) {

    }

    @Override
    public void onLeaderboardFragmentInteraction(Uri uri) {

    }

    @Override
    public void onNotificationFragmentInteraction(Uri uri) {


    }
}


//        ((MainTabActivity) getActivity()).mTextBack.setVisibility(View.GONE);
//                ((MainTabActivity) getActivity()).mImageSettings.setVisibility(View.VISIBLE);
//                ((MainTabActivity) getActivity()).textAppName.setText(getResources().getString(R.string.leaderboard));
