package com.example.memories.gallery;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.example.memories.R;
import com.example.memories.SQLitedatabase.PictureDataSource;
import com.example.memories.gallery.adapters.ImageGalleryAdapter;
import com.example.memories.models.Picture;

import java.util.List;

public class GalleryPhotosFragment extends Fragment {

    private static final String TAG = "<GalleryPhotosFragment>";
    private static GridView mGridView;
    private View rootView;
    private ActionBar actionBar;
    private List<Picture> mImageList;
    private ImageGalleryAdapter mAdapter;
    private LinearLayout mLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.gallery_photos, container, false);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(TAG, "enetred gallery photos fragment!!");

        mGridView = (GridView) rootView.findViewById(R.id.images_grid_view);

        mImageList = PictureDataSource.getAllPictures(getActivity());
        mAdapter = new ImageGalleryAdapter(getActivity(), mImageList);

        mLayout = (LinearLayout)rootView.findViewById(R.id.layout);

        if (mImageList.size() > 0) {
            // long press selection of the pictures
            mGridView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
            mGridView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
                private Integer noOfItemsSelected = 0;

                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id,
                                                      boolean checked) {
                    Log.d(TAG, "Inside OnItemCheckedStateChanged+at position" + position);
                    if (checked) {
                        mImageList.get(position).setChecked(true);
                        View checkedView = mGridView.getChildAt(position
                                - mGridView.getFirstVisiblePosition());
                        ImageGalleryAdapter.ViewHolder holder = (ImageGalleryAdapter.ViewHolder) checkedView
                                .getTag();
                        holder.overlayImgView.setVisibility(View.VISIBLE);
                        noOfItemsSelected++;
                    } else {
                        mImageList.get(position).setChecked(false);
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
                        mImageList.get(i).setChecked(false);
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
                    Intent intent = new Intent(getActivity(), GalleryPhotoDetail.class);
                    intent.putExtra("CLICKED_POSITION", position);
                    startActivity(intent);
                }
            });
        }else {
            //mLayout.setBackgroundResource(R.drawable.img_no_video);
        }
    }
}