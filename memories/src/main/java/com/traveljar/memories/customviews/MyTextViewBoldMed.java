package com.traveljar.memories.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by abhi on 21/06/15.
 */
public class MyTextViewBoldMed extends TextView {

    public MyTextViewBoldMed(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyTextViewBoldMed(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTextViewBoldMed(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Regular.ttf");
        setTypeface(tf);
    }

}
