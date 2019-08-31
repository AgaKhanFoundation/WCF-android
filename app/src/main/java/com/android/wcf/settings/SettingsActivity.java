package com.android.wcf.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.android.wcf.R;
import com.android.wcf.application.WCFApplication;
import com.android.wcf.base.BaseActivity;
import com.android.wcf.facebook.FacebookHelper;
import com.android.wcf.helper.SharedPreferencesUtil;

public class SettingsActivity extends BaseActivity implements SettingsMvp.Host, TeamMembershipMvp.Host {

    private Toolbar toolbar;

    protected static final String TAG = SettingsActivity.class.getSimpleName();

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setupView();

        showSettingsConfiguration();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showSettingsConfiguration() {
        Fragment fragment = new SettingsFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public void signout() {
        FacebookHelper.logout();
        SharedPreferencesUtil.clearMyLogin();
        WCFApplication.instance.openHomeActivity();
    }

    @Override
    public void restartHomeActivity() {
        WCFApplication.instance.openHomeActivity();
    }

    private void setupView() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void showTeamMembershipDetail() {
        Fragment fragment = new TeamMembershipFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
