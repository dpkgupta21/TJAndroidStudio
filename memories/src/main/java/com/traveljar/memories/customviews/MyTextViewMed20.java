package com.traveljar.memories.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by abhi on 21/06/15.
 */
public class MyTextViewMed20 extends TextView {

    public MyTextViewMed20(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyTextViewMed20(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTextViewMed20(Context context) {
        super(context);
        init();
    }

    private void init() {
//        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Regular.ttf");
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/OmnesMed.ttf");
        setTypeface(tf);
        setTextSize(20);
    }

}
