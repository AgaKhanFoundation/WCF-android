package com.android.wcf.onboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.wcf.R;
import com.android.wcf.home.HomeActivity;
import com.rd.PageIndicatorView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class OnboardActivity extends AppCompatActivity implements OnboardMvp.View {

    OnboardMvp.Presenter onboardPresenter;
    View welcomeContainer;
    View tutorialContainer;

    private PageIndicatorView pageIndicatorView;

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.skip_tutorial_link:
                    onboardPresenter.onSkipOnboardingTutorial();
                    break;
                case R.id.start_tutorial_button:
                    onboardPresenter.onStartOnboardingTutorial();
                    break;
            }
        }
    };

    public static Intent createIntent(Context context) {
        Intent intent =  new Intent(context, OnboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onboardPresenter = new OnboardPresenter(this);
        setContentView(R.layout.activity_onboard);
        setupView();
        onboardPresenter.onCreate();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void setupView() {
        welcomeContainer = findViewById(R.id.welcome_container);
        tutorialContainer = findViewById(R.id.tutorial_container);
    }

    @Override
    public void showWelcomeView() {
        welcomeContainer.setVisibility(View.VISIBLE);
        tutorialContainer.setVisibility(View.GONE);


//        HomeAdapter adapter = new HomeAdapter();
//        adapter.setData(createPageList());

        final ViewPager pager = tutorialContainer.findViewById(R.id.viewPager);
//        pager.setAdapter(adapter);

        pageIndicatorView = findViewById(R.id.pageIndicatorView);

        TextView skipTutorialTextView = findViewById(R.id.skip_tutorial_link);
        skipTutorialTextView.setText(generateLinkText(getString(R.string.onboard_welcome_skip)));
        skipTutorialTextView.setOnClickListener(onClickListener);

        Button startTutorialButton = findViewById(R.id.start_tutorial_button);
        startTutorialButton.setOnClickListener(onClickListener);
    }

    @Override
    public void startOnboarding() {
        welcomeContainer.setVisibility(View.GONE);
        tutorialContainer.setVisibility(View.VISIBLE);



        TextView skipTutorialTextView = findViewById(R.id.skip_tutorial_link);
        skipTutorialTextView.setText(generateLinkText(getString(R.string.onboard_welcome_skip)));
        skipTutorialTextView.setOnClickListener(onClickListener);

        Button startTutorialButton = findViewById(R.id.start_tutorial_button);
        startTutorialButton.setOnClickListener(onClickListener);
    }

    @Override
    public void showHomeActivity() {
        Intent intent = HomeActivity.createIntent(this);
        this.startActivity(intent);
    }

    private CharSequence generateLinkText(String text) {
        SpannableString spanText = new SpannableString(text);
        spanText.setSpan(new UnderlineSpan(), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanText;
    }

}
