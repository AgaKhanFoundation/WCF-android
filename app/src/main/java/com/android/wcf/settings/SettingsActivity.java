package com.android.wcf.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.android.wcf.R;
import com.android.wcf.application.DataHolder;
import com.android.wcf.application.WCFApplication;
import com.android.wcf.base.BaseActivity;
import com.android.wcf.facebook.FacebookHelper;
import com.android.wcf.helper.SharedPreferencesUtil;
import com.android.wcf.login.AKFParticipantProfileFragment;
import com.android.wcf.login.AKFParticipantProfileMvp;

public class SettingsActivity extends BaseActivity implements SettingsMvp.Host, TeamMembershipMvp.Host, AKFParticipantProfileMvp.Host {

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

    @Override
    public boolean isAttached() {
        return !isDestroyed() && !isFinishing();
    }

    private void showSettingsConfiguration() {
        Fragment fragment = new SettingsFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public void showToolbar() {
        toolbar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideToolbar() {
        toolbar.setVisibility(View.GONE);
    }

    @Override
    public void signout() {
        DataHolder.clearCache();
        SharedPreferencesUtil.clearMyLogin();
        FacebookHelper.logout();
        restartApp();
    }

    @Override
    public void restartApp() {
        WCFApplication.instance.restartApp();
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
    public void showTeamMembershipDetail(boolean isTeamLead) {
        Fragment fragment = TeamMembershipFragment.getInstance(isTeamLead);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void showAKFProfileView() {
        Fragment fragment = new AKFParticipantProfileFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void akfProfileCreationComplete() {
        super.onBackPressed();
    }
}
