package com.traveljar.memories.gallery;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.JourneyDataSource;
import com.traveljar.memories.SQLitedatabase.PictureDataSource;
import com.traveljar.memories.gallery.adapters.ImageGalleryAdapter;
import com.traveljar.memories.models.Memories;
import com.traveljar.memories.models.Picture;

import java.util.List;

public class GalleryPhotos extends AppCompatActivity {

    private static final String TAG = "<GalleryPhotos>";
    private static GridView mGridView;
    private List<Memories> mImageList;
    private ImageGalleryAdapter mAdapter;
    private LinearLayout mLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.gallery_photos);

        setUpToolBar();

        final String journeyId = getIntent().getStringExtra("JOURNEY_ID");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(JourneyDataSource.getJourneyById(this, journeyId).getName());
        setSupportActionBar(toolbar);

        Log.d(TAG, "entered gallery photos fragment!!");

        mGridView = (GridView) findViewById(R.id.images_grid_view);

        Log.d(TAG, "entered gallery photos fragment!!");

        mImageList = PictureDataSource.getPictureMemoriesFromJourney(this, journeyId);
        mAdapter = new ImageGalleryAdapter(this, mImageList);

        mLayout = (LinearLayout)findViewById(R.id.gallery_photos_layout);

        if (mImageList.size() > 0) {
            // long press selection of the pictures
            mGridView.setAdapter(mAdapter);
            mGridView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
            mGridView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
                private Integer noOfItemsSelected = 0;

                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id,
                                                      boolean checked) {
                    Log.d(TAG, "Inside OnItemCheckedStateChanged+at position" + position);
                    Picture picture = (Picture)mImageList.get(position);
                    if (checked) {
                        picture.setChecked(true);
                        View checkedView = mGridView.getChildAt(position
                                - mGridView.getFirstVisiblePosition());
                        ImageGalleryAdapter.ViewHolder holder = (ImageGalleryAdapter.ViewHolder) checkedView
                                .getTag();
                        holder.overlayImgView.setVisibility(View.VISIBLE);
                        noOfItemsSelected++;
                    } else {
                        picture.setChecked(false);
                        View checkedView = mGridView.getChildAt(position
                                - mGridView.getFirstVisiblePosition());
                        ImageGalleryAdapter.ViewHolder holder = (ImageGalleryAdapter.ViewHolder) checkedView
                                .getTag();
                        holder.overlayImgView.setVisibility(View.GONE);
                        noOfItemsSelected--;
                    }
                    mode.setTitle(noOfItemsSelected.toString() + " selected");
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {
                        default:
                            return false;
                    }
                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.gallery_photos_cab_items, menu);
                    return true;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    for (int i = 0; i < mImageList.size(); i++) {
                        ((Picture)mImageList.get(i)).setChecked(false);
                    }
                    noOfItemsSelected = 0;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }
            });

            // click on an individual photo
            // Take to the detail page in a swipable gallery
            mGridView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
//					Intent intent = new Intent(getActivity(), PhotoDetail.class);
//					intent.putExtra("PICTURE_ID", mImageList.get(position).getId());
//					startActivity(intent);
                    Intent intent = new Intent(GalleryPhotos.this, GalleryPhotoDetail.class);
                    intent.putExtra("CLICKED_POSITION", position);
                    intent.putExtra("JOURNEY_ID", journeyId);
                    startActivity(intent);
                }
            });
        }else {
            mLayout.setBackgroundResource(R.drawable.img_no_pic);
        }
    }

    private void setUpToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        title.setText("Pictures Detail");
    }

}