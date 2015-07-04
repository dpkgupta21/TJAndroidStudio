package com.traveljar.memories.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class MyTextViewBodyMed14 extends TextView {

    public MyTextViewBodyMed14(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyTextViewBodyMed14(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTextViewBodyMed14(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Medium.ttf");
        setTypeface(tf);
        setTextSize(14);
    }

}