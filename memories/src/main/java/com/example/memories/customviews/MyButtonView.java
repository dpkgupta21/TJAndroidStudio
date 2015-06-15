package com.example.memories.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class MyButtonView extends Button {

    public MyButtonView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyButtonView(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/OmnesMed.ttf");
        setTypeface(tf);
    }

}