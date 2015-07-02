package com.traveljar.memories.gallery;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.PictureDataSource;
import com.traveljar.memories.gallery.adapters.ImageCarouselAdapter;
import com.traveljar.memories.models.Memories;

import java.util.List;

public class GalleryPhotoDetail extends Activity {

    List<Memories> mPicturesList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_photos_detail);

        int clickedPosition = getIntent().getIntExtra("CLICKED_POSITION", 0);
        String journeyId = getIntent().getStringExtra("JOURNEY_ID");

        ViewPager viewPager = (ViewPager) findViewById(R.id.imagePager);
        mPicturesList = PictureDataSource.getPictureMemoriesFromJourney(this, journeyId);

        ImageCarouselAdapter adapter = new ImageCarouselAdapter(this, mPicturesList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(clickedPosition);
    }
}
