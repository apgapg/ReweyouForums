package com.linkedin.android.spyglass;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ListView;


/**
 * Created by master on 21/6/17.
 */

public class FixHeightListView extends ListView {
    public FixHeightListView(Context context) {
        super(context);
    }

    public FixHeightListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixHeightListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d("abc", "onMeasure: vhvjhvyvy");
        super.onMeasure(widthMeasureSpec, (200));
    }
}
