package com.example.memories.gallery.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.example.memories.R;
import com.example.memories.models.Picture;
import com.example.memories.utility.HelpMe;
import com.example.memories.utility.PictureUtilities;
import com.example.memories.volley.AppController;

import java.io.FileNotFoundException;
import java.util.List;

public class ImageCarouselAdapter extends PagerAdapter {
    private Activity _activity;
    private List<Picture> mPictureList;
    private LayoutInflater inflater;

    // constructor
    public ImageCarouselAdapter(Activity activity, List<Picture> pictureList) {
        _activity = activity;
        mPictureList = pictureList;
    }

    @Override
    public int getCount() {
        return mPictureList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imgDisplay;
        ImageView btnClose;
        TextView imgTitle;
        inflater = (LayoutInflater) _activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.gallery_photos_detail_item, container, false);

        imgDisplay = (ImageView) viewLayout.findViewById(R.id.detailImage);
        btnClose = (ImageView) viewLayout.findViewById(R.id.closeIcon);
        imgTitle = (TextView) viewLayout.findViewById(R.id.imageTitleTxt);

        imgTitle.setText(mPictureList.get(position).getCaption());

        DisplayMetrics displayMetrics = _activity.getResources().getDisplayMetrics();

        if(mPictureList.get(position).getDataLocalURL() != null){
            try {
                imgDisplay.setImageBitmap(HelpMe.decodeSampledBitmapFromPath(_activity, mPictureList
                                .get(position).getDataLocalURL(), displayMetrics.widthPixels,
                        displayMetrics.heightPixels));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else{
            PictureUtilities.downloadPicFromURL(_activity, mPictureList.get(position), imgDisplay);
        }

        // close button click event
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _activity.finish();
            }
        });
        ((ViewPager) container).addView(viewLayout);
        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }

    private int imageWidthPixel() {
        DisplayMetrics displayMetrics = _activity.getResources().getDisplayMetrics();
        int width = (int) (displayMetrics.widthPixels - 30 / displayMetrics.density) / 3;
        return width;
    }

}