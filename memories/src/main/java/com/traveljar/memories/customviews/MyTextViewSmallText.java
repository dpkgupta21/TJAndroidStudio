package com.traveljar.memories.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by abhi on 21/06/15.
 */
public class MyTextViewSmallText extends TextView {

    public MyTextViewSmallText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyTextViewSmallText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTextViewSmallText(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Regular.ttf");
        setTypeface(tf);
        setTextSize(12);
    }

}