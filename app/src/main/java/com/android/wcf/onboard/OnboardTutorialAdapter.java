package com.android.wcf.onboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.wcf.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class OnboardTutorialAdapter extends PagerAdapter implements OnboardTutorialAdapterMvp.View {

    OnboardTutorialAdapterMvp.Host host;
    OnboardTutorialAdapterMvp.Presenter presenter;

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.skip_tutorial_link:
                    presenter.onSkipOnboardingTutorial();
                    break;
                case R.id.tutorial_step_button:
                    if (OnboardTutorialItem.LAST_STEP_TAG.equalsIgnoreCase((String) view.getTag(host.getStepTagKey()))) {
                        presenter.onCompleteTutorial();
                    }
                    else {
                        presenter.showTutorialNextStep();
                    }
                    break;
            }
        }
    };


    public OnboardTutorialAdapter(OnboardTutorialAdapterMvp.Host host) {
        super();
        this.host = host;
        presenter = new OnboardTutorialAdapterPresenter(this, host);
    }

    public OnboardTutorialAdapterMvp.Presenter getPresenter() {
        return presenter;
    }

    @Override
    public void setData(List<OnboardTutorialItem> tutorialItems) {
        presenter.updateOnboardTutorialData(tutorialItems);
    }

        @Override
    public int getCount() {
        return presenter.getItemCount();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        OnboardTutorialItem stepData = presenter.getStepData(position);
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.view_onboarding_step, container, false);

        TextView headline = view.findViewById(R.id.headline);
        TextView byline1 = view.findViewById(R.id.byline1);
        TextView byline2 = view.findViewById(R.id.byline2);
        ImageView pageImage = view.findViewById(R.id.page_image);
        Button stepButton = view.findViewById(R.id.tutorial_step_button);
        TextView skipLink = view.findViewById(R.id.skip_tutorial_link);

        byline1.setVisibility(View.GONE);
        byline2.setVisibility(View.GONE);

        headline.setText(stepData.title);
        pageImage.setImageDrawable(stepData.image);
        stepButton.setText(stepData.buttonTitle);
        if (position == presenter.getItemCount() -1) {
            stepButton.setTag(host.getStepTagKey(), OnboardTutorialItem.LAST_STEP_TAG);
            skipLink.setVisibility(View.GONE);
        }
        else {
            stepButton.setTag(host.getStepTagKey(), OnboardTutorialItem.INTERMEDIATE_STEP_TAG);
        }
        skipLink.setText(host.generateSkipLinkText());

        stepButton.setOnClickListener(onClickListener);
        skipLink.setOnClickListener(onClickListener);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
