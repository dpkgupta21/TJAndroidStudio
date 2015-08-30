package com.traveljar.memories.gallery.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.PictureDataSource;
import com.traveljar.memories.models.Memories;
import com.traveljar.memories.models.Picture;
import com.traveljar.memories.picture.DownloadPicture;

import java.io.File;
import java.util.List;

public class ImageCarouselAdapter extends PagerAdapter implements DownloadPicture.OnPictureDownloadListener{
    private static final String TAG = "ImageCarouselAdapter";
    private Activity _activity;
    private List<Memories> mPictureList;
    private LayoutInflater inflater;
    private ProgressDialog pDialog;
    //Used to store the view of button for which download is called so that on successful completion it can be set invisible
    private View downloadBtnView;
    private View image;

    // constructor
    public ImageCarouselAdapter(Activity activity, List<Memories> pictureList) {
        _activity = activity;
        mPictureList = pictureList;
        pDialog = new ProgressDialog(_activity);
        pDialog.setMessage("Downloading image please wait");
        pDialog.setCanceledOnTouchOutside(false);
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
    public Object instantiateItem(ViewGroup container, final int position) {

        final ImageView imgDisplay;
        ImageView btnClose;
        TextView imgTitle;
        final Button downloadBtn;
        inflater = (LayoutInflater) _activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View viewLayout = inflater.inflate(R.layout.gallery_photos_detail_item, container, false);

        imgDisplay = (ImageView) viewLayout.findViewById(R.id.detailImage);
        btnClose = (ImageView) viewLayout.findViewById(R.id.closeIcon);
        imgTitle = (TextView) viewLayout.findViewById(R.id.imageTitleTxt);
        downloadBtn = (Button) viewLayout.findViewById(R.id.download_audio_btn);

        final Picture pic = (Picture)mPictureList.get(position);
        Log.d(TAG, "position " + position + " url " + pic.getDataLocalURL() + pic);
        if(pic.getDataLocalURL() == null){
            downloadBtn.setVisibility(View.VISIBLE);
        }
        imgTitle.setText(pic.getCaption());

        DisplayMetrics displayMetrics = _activity.getResources().getDisplayMetrics();

        // If original image is available show it else show the thumbnail
        String picPath;
        if(pic.getDataLocalURL() != null){
            picPath = pic.getDataLocalURL();
        }else {
            picPath = pic.getPicThumbnailPath();
        }

        Glide.with(_activity).load(Uri.fromFile(new File(picPath))).asBitmap().into(imgDisplay);

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadBtnView = downloadBtn;
                image = imgDisplay;
                pDialog.show();
                Log.d(TAG, "downloading picture for position" + position);
                new DownloadPicture(pic, ImageCarouselAdapter.this).startDownloadingPic();
            }
        });

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
    public void onDownloadPicture(Picture picture, boolean result) {
        if(result) {
            downloadBtnView.setVisibility(View.GONE);
            Glide.with(_activity).load(Uri.fromFile(new File(picture.getDataLocalURL()))).asBitmap().into((ImageView)image);
            PictureDataSource.updatePicLocalPath(_activity, picture.getDataLocalURL(), picture.getId());
            pDialog.dismiss();
            Log.d(TAG, " " + PictureDataSource.getPictureById(_activity, picture.getId()));
            Toast.makeText(_activity, "Image Successfully downloaded and saved on your device", Toast.LENGTH_SHORT).show();
        }else {
            pDialog.dismiss();
            Toast.makeText(_activity, "Unable to download image now please try later", Toast.LENGTH_SHORT).show();
        }
    }
}