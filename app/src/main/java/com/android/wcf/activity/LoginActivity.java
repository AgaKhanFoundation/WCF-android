package com.android.wcf.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.android.wcf.R;
import com.android.wcf.utils.Preferences;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Malik Khoja on 4/27/2017.
 */

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.buttonFacebookLogin)
    Button btnFacebookLogin;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
        ButterKnife.bind(this);
        btnFacebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Preferences.setPreferencesBoolean("isUserLoggedIn", true, mContext);
                Intent mainTabActivity = new Intent(LoginActivity.this, MainTabActivity.class);
                startActivity(mainTabActivity);
                finish();
            }
        });
    }
}
