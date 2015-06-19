package com.traveljar.memories.gallery.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.PictureDataSource;
import com.traveljar.memories.models.Picture;
import com.traveljar.memories.picture.DownloadPicture;
import com.traveljar.memories.utility.HelpMe;

import java.io.FileNotFoundException;
import java.util.List;

public class ImageCarouselAdapter extends PagerAdapter implements DownloadPicture.OnPictureDownloadListener{
    private static final String TAG = "ImageCarouselAdapter";
    private Activity _activity;
    private List<Picture> mPictureList;
    private LayoutInflater inflater;
    private ProgressDialog pDialog;

    // constructor
    public ImageCarouselAdapter(Activity activity, List<Picture> pictureList) {
        _activity = activity;
        mPictureList = pictureList;
        pDialog = new ProgressDialog(_activity);
        pDialog.setMessage("Downloading image please wait");
    }

    @Override
    public int getCount() {
        return mPictureList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
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

        if (mPictureList.get(position).getDataLocalURL() != null) {
            try {
                imgDisplay.setImageBitmap(HelpMe.decodeSampledBitmapFromPath(_activity, mPictureList
                                .get(position).getDataLocalURL(), displayMetrics.widthPixels,
                        displayMetrics.heightPixels));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            pDialog.show();
            Log.d(TAG, "downloading picture for position" + position);
            new DownloadPicture(mPictureList.get(position), this, imgDisplay).startDownloadingPic();
        }

        // close button click event
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _activity.finish();
            }
        });
        container.addView(viewLayout);
        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }

    @Override
    public void onDownloadPicture(Picture picture, ImageView imgView) {
        PictureDataSource.updatePicLocalPath(_activity, picture.getDataLocalURL(), picture.getId());
        Log.d(TAG, "picture downloaded successfully now displaying it");
        pDialog.dismiss();
        DisplayMetrics displayMetrics = _activity.getResources().getDisplayMetrics();
        try {
            imgView.setImageBitmap(HelpMe.decodeSampledBitmapFromPath(_activity, picture.getDataLocalURL(), displayMetrics.widthPixels,
                    displayMetrics.heightPixels));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}