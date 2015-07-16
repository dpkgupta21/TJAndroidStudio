package com.traveljar.memories.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by abhi on 24/06/15.
 */

public class MyTextViewCustom extends TextView {

    public MyTextViewCustom(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyTextViewCustom(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTextViewCustom(Context context) {
        super(context);
        init();
    }

    private void init() {
//        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Regular.ttf");
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/OmnesReg.ttf");
        setTypeface(tf);
    }

}


