package com.android.wcf.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.android.wcf.R;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

public class SettingsActivity extends AppCompatActivity implements SettingsMvp.Host, DeviceConnectionMvp.Host {

    private Toolbar toolbar;

    public static Intent createIntent(Context context) {
        Intent intent =  new Intent(context, SettingsActivity.class);
        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupView();

        showSettingsConfiguration();
    }

    private void showSettingsConfiguration() {
        Fragment fragment = new SettingsFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

    }

    @Override
    public void showDeviceConnection() {
        Fragment fragment = new DeviceConnectionFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void setupView() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public void setToolbarTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(title);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
