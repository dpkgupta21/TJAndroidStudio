package com.example.memories.gallery;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.example.memories.R;
import com.example.memories.SQLitedatabase.PictureDataSource;
import com.example.memories.gallery.adapters.ImageCarouselAdapter;
import com.example.memories.models.Picture;

import java.util.List;

public class GalleryPhotoDetail extends Activity {

    List<Picture> mPicturesList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_photos_detail);

        int clickedPosition = getIntent().getIntExtra("CLICKED_POSITION", 0);

        ViewPager viewPager = (ViewPager) findViewById(R.id.imagePager);
        mPicturesList = PictureDataSource.getAllPictures(this);

        ImageCarouselAdapter adapter = new ImageCarouselAdapter(this,
                mPicturesList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(clickedPosition);
    }
}
