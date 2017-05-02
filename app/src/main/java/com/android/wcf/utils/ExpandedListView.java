package com.android.wcf.utils;

/**
 * Created by Malik Khoja on 9/1/2016.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ListView;

import static com.android.wcf.BuildConfig.DEBUG;


public class ExpandedListView extends ListView {

    boolean expanded = false;

    public ExpandedListView(Context context) {
        super(context);
    }

    public ExpandedListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandedListView(Context context, AttributeSet attrs,
                            int defStyle) {
        super(context, attrs, defStyle);
    }

    public boolean isExpanded() {
        return expanded;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // HACK! TAKE THAT ANDROID!
        try {
            if (isExpanded()) {
                int expandSpec = MeasureSpec.makeMeasureSpec(
                        Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
                super.onMeasure(widthMeasureSpec, expandSpec);
                ViewGroup.LayoutParams params = getLayoutParams();
                params.height = getMeasuredHeight();
            } else {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        } catch (Exception e) {
            Debug.error(DEBUG, "ExpandedListView", "Exception" + e.getMessage());
        }
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}