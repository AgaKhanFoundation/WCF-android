package com.android.wcf.home;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;

import com.android.wcf.R;
import com.android.wcf.base.BaseActivity;
import com.android.wcf.home.Campaign.CampaignFragment;
import com.android.wcf.home.Dashboard.DashboardFragment;
import com.android.wcf.home.Leaderboard.LeaderboardFragment;
import com.android.wcf.home.Notifications.NotificationsFragment;
import com.android.wcf.helper.SharedPreferencesUtil;

public class HomeActivity extends BaseActivity
        implements HomeMvp.HomeView
        , DashboardFragment.FragmentHost
        , CampaignFragment.FragmentHost
        , LeaderboardFragment.FragmentHost
        , NotificationsFragment.FragmentHost {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final String BACK_STACK_ROOT_TAG = "root_fragment";


    private HomePresenter homePresenter;
    private DashboardFragment dashboardFragment;
    private CampaignFragment campaignFragment;
    private LeaderboardFragment leaderboardFragment;
    private NotificationsFragment notificationsFragment;

    private String myFacebookId;
    private int myParticipantId;
    private int myActiveEventId;
    private int myTeamId;
    private Toolbar toolbar;

    private int currentNavigationId;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (currentNavigationId == item.getItemId()) {
                return false;
            }

            switch (item.getItemId()) {
                case R.id.nav_dashboard:
                    loadFragment(dashboardFragment, getString(R.string.nav_dashboard), R.id.nav_dashboard);
                    return true;
                case R.id.nav_campaign:
                    loadFragment(campaignFragment, getString(R.string.nav_campaign), R.id.nav_campaign);
                    return true;
                case R.id.nav_leaderboard:
                    loadFragment(leaderboardFragment, getString(R.string.nav_leaderboard), R.id.nav_leaderboard);
                    leaderboardFragment.setMyTeamId(myTeamId);
                    return true;
                case R.id.nav_notifications:
                    loadFragment(notificationsFragment, getString(R.string.nav_notifications), R.id.nav_notifications);
                    return true;
            }

            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        myFacebookId = SharedPreferencesUtil.getMyFacebookId();
        myParticipantId = SharedPreferencesUtil.getMyParticipantId();
        myActiveEventId = SharedPreferencesUtil.getMyActiveEventId();
        myTeamId = SharedPreferencesUtil.getMyTeamId();

        homePresenter = new HomePresenter(this);

        if (myFacebookId == null || TextUtils.isEmpty(myFacebookId)) {
            //TODO: navigate to login activity
        }

        dashboardFragment = DashboardFragment.newInstance(null, null);
        campaignFragment = CampaignFragment.newInstance(myFacebookId, myActiveEventId, myTeamId);
        leaderboardFragment = LeaderboardFragment.newInstance(myTeamId);
        notificationsFragment = NotificationsFragment.newInstance(null, null);

        setupView();
    }

    private void setupView() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);

        BottomNavigationView navigation = findViewById(R.id.home_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.nav_campaign);
    }

    private void loadFragment(Fragment fragment, String title, int navItemId) {
        FragmentManager fm = getSupportFragmentManager();
        // Pop off everything up to and including the current tab
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(BACK_STACK_ROOT_TAG)
                .commit();

        setViewTitle(title);
        currentNavigationId = navItemId;
    }

    @Override
    public void setViewTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (title != null) {
            actionBar.setTitle(title);
            actionBar.setDisplayShowTitleEnabled(true);
        } else {
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public void showToolbarUpAffordance(boolean show) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(show);
        actionBar.setDisplayShowHomeEnabled(show);
    }

    public void setMyFacebookId(String fbid) {
        this.myFacebookId = fbid;
    }

    public void setMyParticipantId(int myParticipantId) {
        this.myParticipantId = myParticipantId;
    }

    public void setMyTeamId(int myTeamId) {
        this.myTeamId = myTeamId;
    }

    public void setMyActiveEventId(int myActiveEventId) {
        this.myActiveEventId = myActiveEventId;
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
