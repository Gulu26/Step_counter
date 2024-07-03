package com.mikhaellopez.circularprogressbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class CircularProgressBar extends View {
    public CircularProgressBar(Context context) {
        this(context, null);
    }

    public CircularProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setProgressWithAnimation(float toFloat) {

    }
}
