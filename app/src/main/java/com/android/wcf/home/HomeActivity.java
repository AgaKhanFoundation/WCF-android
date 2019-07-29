package com.android.wcf.home;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.android.wcf.login.LoginActivity;
import com.android.wcf.model.Participant;
import com.android.wcf.settings.SettingsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.android.wcf.R;
import com.android.wcf.base.BaseActivity;
import com.android.wcf.home.campaign.CampaignFragment;
import com.android.wcf.home.dashboard.DashboardFragment;
import com.android.wcf.home.leaderboard.LeaderboardFragment;
import com.android.wcf.home.notifications.NotificationsFragment;
import com.android.wcf.helper.SharedPreferencesUtil;

public class HomeActivity extends BaseActivity
        implements HomeMvp.HomeView
        , DashboardFragment.FragmentHost
        , CampaignFragment.FragmentHost
        , LeaderboardFragment.FragmentHost
        , NotificationsFragment.FragmentHost {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final String BACK_STACK_ROOT_TAG = "root_fragment";

    private final int SPLASH_TIMER = 3000;

    private HomePresenter homePresenter;
    private DashboardFragment dashboardFragment;
    private CampaignFragment campaignFragment;
    private LeaderboardFragment leaderboardFragment;
    private NotificationsFragment notificationsFragment;

    private String myFacebookId;
    private String myFbEmail;
    private String myFacebookName;
    private String myFacebookProfileUrl;

    private int myActiveEventId;
    private int myTeamId;
    private Toolbar toolbar;

    private int currentNavigationId;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (currentNavigationId == item.getItemId()) {
                return false;
            }

            switch (item.getItemId()) {
                case R.id.nav_dashboard:
                    if (dashboardFragment != null) {
                        loadFragment(dashboardFragment, getString(R.string.nav_dashboard), R.id.nav_dashboard);
                    }
                    return true;
                case R.id.nav_campaign:
                    if (campaignFragment != null) {
                        loadFragment(campaignFragment, getString(R.string.nav_campaign), R.id.nav_campaign);
                    }
                    return true;
                case R.id.nav_leaderboard:
                    if (leaderboardFragment != null) {
                        loadFragment(leaderboardFragment, getString(R.string.nav_leaderboard), R.id.nav_leaderboard);
                        leaderboardFragment.setMyTeamId(myTeamId);
                    }
                    return true;
                case R.id.nav_notifications:
                    if (notificationsFragment != null) {
                        loadFragment(notificationsFragment, getString(R.string.nav_notifications), R.id.nav_notifications);
                    }
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
        myActiveEventId = SharedPreferencesUtil.getMyActiveEventId();
        myTeamId = SharedPreferencesUtil.getMyTeamId();

        homePresenter = new HomePresenter(this);

        setupView();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (myActiveEventId < 1 ) {
            showErrorAndCloseApp(R.string.events_not_selected_error);
            return;
        }
        if (myFacebookId == null || TextUtils.isEmpty(myFacebookId)) {
            showLoginActivity();
            finish();
            return;
        }
        homePresenter.getParticipant(myFacebookId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings) {
            Intent intent = SettingsActivity.createIntent(this);
            startActivity(intent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupView() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);

        BottomNavigationView navigation = findViewById(R.id.home_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
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

    public void showLoginActivity() {
        Intent intent = LoginActivity.createIntent(this);
        this.startActivity(intent);
    }

    public void setMyFacebookId(String fbid) {
        this.myFacebookId = fbid;
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

    @Override
    public void showErrorAndCloseApp(@StringRes int messageId) {
        showError(messageId);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, SPLASH_TIMER);
    }

    @Override
    public void onGetParticipant(Participant participant) {
        if (participant == null) {
            return;
        }
        Integer participantTeamId = participant.getTeamId();
        if (participantTeamId == null && myTeamId > 0) {
            myTeamId = 0;
            homePresenter.participantLeaveFromTeam(myFacebookId);
        }
        else if (participantTeamId != null) {
            myTeamId = participantTeamId; // team must have been assigned remotely
        }

        if (participant.getEventId() == null || participant.getEventId() != myActiveEventId ) {
            homePresenter.updateParticipantEvent(myFacebookId, myActiveEventId);
        }
        else {
            addNavigationFragments();
        }
    }

    @Override
    public void onGetParticipantNotFound() {
        homePresenter.createParticipant(myFacebookId);
    }

    @Override
    public void onParticipantCreated(Participant participant) {
        homePresenter.updateParticipantEvent(myFacebookId, myActiveEventId);
    }

    @Override
    public void onAssignedParticipantToEvent(String fbId, int eventId) {
        addNavigationFragments();
    }

    protected void addNavigationFragments() {
        if (dashboardFragment == null) {
            dashboardFragment = DashboardFragment.newInstance(null, null);
        }
        if (notificationsFragment == null) {
            notificationsFragment = NotificationsFragment.newInstance(null, null);
        }

        if (campaignFragment == null) {
            campaignFragment = CampaignFragment.newInstance(myFacebookId, myActiveEventId, myTeamId);
        }
        if (leaderboardFragment == null) {
            leaderboardFragment = LeaderboardFragment.newInstance(myTeamId);
        }

        BottomNavigationView navigation = findViewById(R.id.home_navigation);
        navigation.setSelectedItemId(R.id.nav_campaign);

    }
}
