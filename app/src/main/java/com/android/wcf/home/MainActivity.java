package com.android.wcf.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.wcf.R;
import com.android.wcf.base.BaseActivity;

public class MainActivity extends BaseActivity implements HomeMvp.HomeView {

    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView mTextMessage;

    private HomePresenter homePresenter;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_dashboard:
                    mTextMessage.setText(R.string.nav_dashboard);
                    return true;
                case R.id.nav_campaign:
                    mTextMessage.setText(R.string.nav_campaign);
                    return true;
                case R.id.nav_leaderboard:
                    mTextMessage.setText(R.string.nav_leaderboard);
                    return true;
                case R.id.nav_notifications:
                    mTextMessage.setText(R.string.nav_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        homePresenter = new HomePresenter(this);
    }

}


