package com.traveljar.memories.gallery;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
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
import com.traveljar.memories.SQLitedatabase.VideoDataSource;
import com.traveljar.memories.gallery.adapters.ImageGalleryAdapter;
import com.traveljar.memories.gallery.adapters.VideoGalleryAdapter;
import com.traveljar.memories.models.Memories;
import com.traveljar.memories.models.Video;
import com.traveljar.memories.video.DownloadVideoAsyncTask;

import java.io.File;
import java.util.List;

public class GalleryVideos extends AppCompatActivity implements DownloadVideoAsyncTask.OnVideoDownloadListener {

    private static final String TAG = "<GalleryPhotos>";
    private static GridView mGridView;
    private List<Memories> mVideoList;
    private VideoGalleryAdapter mAdapter;

    private LinearLayout mLayout;
    private ProgressDialog mProgressDialog;
    private String journeyId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_videos);

        journeyId = getIntent().getStringExtra("JOURNEY_ID");
        setUpToolBar();

        mGridView = (GridView) findViewById(R.id.videos_grid_view);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Downloading Video please wait");

        mVideoList = VideoDataSource.getAllVideoMemories(this, journeyId);
        mAdapter = new VideoGalleryAdapter(this, mVideoList);

        mLayout = (LinearLayout) findViewById(R.id.gallery_video_layout);

        if (mVideoList.size() > 0) {
            mGridView.setAdapter(mAdapter);
            // long press selection of the pictures
            mGridView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
            mGridView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
                private Integer noOfItemsSelected = 0;

                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id,
                                                      boolean checked) {
                    Video video = (Video)mVideoList.get(position);
                    Log.d(TAG, "Inside OnItemCheckedStateChanged+at position" + position);
                    if (checked) {
                        video.setChecked(true);
                        View checkedView = mGridView.getChildAt(position
                                - mGridView.getFirstVisiblePosition());
                        ImageGalleryAdapter.ViewHolder holder = (ImageGalleryAdapter.ViewHolder) checkedView
                                .getTag();
                        holder.overlayImgView.setVisibility(View.VISIBLE);
                        noOfItemsSelected++;
                    } else {
                        video.setChecked(false);
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
                    for (int i = 0; i < mVideoList.size(); i++) {
                        ((Video)mVideoList.get(i)).setChecked(false);
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
                    Video video = (Video)mVideoList.get(position);
                    if (video.getDataLocalURL() != null) {
                        Intent mediaIntent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(new File(video.getDataLocalURL())));
                        mediaIntent.setDataAndType(Uri.fromFile(new File(video.getDataLocalURL())), "video/*");
                        startActivity(mediaIntent);
                    } else {
                        mProgressDialog.show();
                        new DownloadVideoAsyncTask(GalleryVideos.this, video).execute();
                    }
                }
            });
        } else {
            mLayout.setBackgroundResource(R.drawable.img_no_video);
        }
    }

    @Override
    public void onVideoDownload(String videoLocalUrl, Video video) {
        mProgressDialog.dismiss();
        VideoDataSource.updateVideoLocalUrl(this, video.getId(), video.getDataLocalURL());
        Intent mediaIntent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(new File(video.getDataLocalURL())));
        mediaIntent.setDataAndType(Uri.fromFile(new File(video.getDataLocalURL())), "video/*");
        startActivity(mediaIntent);
    }

    private void setUpToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        title.setText(JourneyDataSource.getJourneyById(this, journeyId).getName());
        toolbar.setNavigationIcon(R.drawable.ic_next);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}