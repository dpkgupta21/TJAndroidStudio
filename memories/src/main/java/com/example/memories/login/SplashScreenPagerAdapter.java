package com.example.memories.login;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.memories.R;

import java.util.List;
import java.util.Map;

public class SplashScreenPagerAdapter extends PagerAdapter {

    private Activity _activity;
    private Map<Integer, String> mPicturesList;
    private List<Integer> pictureIdsList;

    public SplashScreenPagerAdapter(Activity _activity, Map<Integer, String> mPicturesList, List<Integer> pictureIdsList) {
        this._activity = _activity;
        this.mPicturesList = mPicturesList;
        this.pictureIdsList = pictureIdsList;
    }

    @Override
    public int getCount() {
        return pictureIdsList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) _activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.splash_screen_view_pager_item, container, false);

        TextView text = (TextView) view.findViewById(R.id.image_description);
        ImageView img = (ImageView) view.findViewById(R.id.image_item);


        img.setImageResource(pictureIdsList.get(position));
        text.setText(mPicturesList.get(pictureIdsList.get(position)));
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }



}
