package com.android.wcf.onboard;

import android.graphics.drawable.Drawable;

public class OnboardTutorialItem {
    Drawable image;
    String title;
    String buttonTitle;

    public static final String WELCOME_STEP_TAG = "welcome_step";
    public static final String LAST_STEP_TAG = "last_step";
    public static final String INTERMEDIATE_STEP_TAG = "intermediate_step";

    public OnboardTutorialItem(Drawable image, String title, String buttonTitle) {
        this.image = image;
        this.title = title;
        this.buttonTitle = buttonTitle;
    }
}
