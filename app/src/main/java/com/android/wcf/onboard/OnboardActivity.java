package com.android.wcf.onboard;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.wcf.R;
import com.android.wcf.helper.SharedPreferencesUtil;
import com.android.wcf.home.HomeActivity;
import com.rd.PageIndicatorView;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class OnboardActivity extends AppCompatActivity implements OnboardMvp.View, OnboardTutorialAdapterMvp.Host {

    OnboardMvp.Presenter onboardPresenter;
    View welcomeContainer;
    View tutorialContainer;

    private PageIndicatorView pageIndicatorView;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.skip_tutorial_link:
                onboardPresenter.onSkipOnboardingTutorial();
                break;
            case R.id.tutorial_step_button:
                onboardPresenter.onStartOnboardingTutorial();
                break;
        }
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, OnboardActivity.class);
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

        TextView skipTutorialTextView = findViewById(R.id.skip_tutorial_link);
        skipTutorialTextView.setText(generateSkipLinkText());
        skipTutorialTextView.setOnClickListener(this);

        Button startTutorialButton = findViewById(R.id.tutorial_step_button);
        startTutorialButton.setOnClickListener(this);
    }

    @Override
    public void showOnboardingTutorial(List<OnboardTutorialItem> stepsData) {
        OnboardTutorialAdapter adapter = new OnboardTutorialAdapter(this);
        adapter.setData(stepsData);

        final ViewPager viewPager = tutorialContainer.findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        pageIndicatorView = findViewById(R.id.pageIndicatorView);
        pageIndicatorView.setViewPager(viewPager);

        welcomeContainer.setVisibility(View.GONE);
        tutorialContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void showHomeActivity() {
        Intent intent = HomeActivity.createIntent(this);
        this.startActivity(intent);
    }

    @Override
    public OnboardTutorialItem loadOnboardingStepFromResource(int step) {
        TypedArray stepImages = getResources().obtainTypedArray(R.array.onboard_tutorial_step_images);
        String stepTitles[] = getResources().getStringArray(R.array.onboard_tutorial_step_title);
        String stepButtonTitle[] = getResources().getStringArray(R.array.onboard_tutorial_step_button_title);
        return new OnboardTutorialItem(stepImages.getDrawable(step), stepTitles[step], stepButtonTitle[step]);
    }

    @Override
    public CharSequence generateSkipLinkText() {
        String text = getString(R.string.onboard_welcome_skip);
        SpannableString spanText = new SpannableString(text);
        spanText.setSpan(new UnderlineSpan(), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanText;
    }

    @Override
    public void onSkipOnboardingTutorial() {
        showHomeActivity();
    }

    @Override
    public void onBackPressed() {
        final ViewPager viewPager = tutorialContainer.findViewById(R.id.viewPager);

        if (viewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    @Override
    public void showTutorialNextStep() {
        final ViewPager viewPager = tutorialContainer.findViewById(R.id.viewPager);
        if (viewPager != null) {
           viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
        }
    }

    @Override
    public void onCompleteTutorial() {
        SharedPreferencesUtil.saveShowOnboardingTutorial(false);
    }

    @Override
    public int getStepTagKey() {
        return R.integer.step_tag_key;
    }
}
