package com.traveljar.memories.gallery;

import android.annotation.TargetApi;
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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.PictureDataSource;
import com.traveljar.memories.gallery.adapters.ImageGalleryAdapter;
import com.traveljar.memories.models.Picture;

import java.util.List;

public class GalleryAlbumsFragment extends Fragment{

    private static final String TAG = "<GalleryAlbumsFragment>";
    private static GridView mGridView;
    private View rootView;
    private List<Picture> mAlbumsList;
    private ImageGalleryAdapter mAdapter;
    private LinearLayout mLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.gallery_albums, container, false);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(TAG, "entered gallery albums fragment!!");

        mGridView = (GridView) rootView.findViewById(R.id.images_grid_view);

        mAlbumsList = PictureDataSource.getAllPictures(getActivity());
        mAdapter = new ImageGalleryAdapter(getActivity(), mAlbumsList);

        mLayout = (LinearLayout)rootView.findViewById(R.id.gallery_photos_layout);

        if (mAlbumsList.size() > 0) {
            // long press selection of the pictures
            mGridView.setAdapter(mAdapter);
            mGridView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
            mGridView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                private Integer noOfItemsSelected = 0;

                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id,
                                                      boolean checked) {
                    Log.d(TAG, "Inside OnItemCheckedStateChanged+at position" + position);
                    if (checked) {
                        mAlbumsList.get(position).setChecked(true);
                        View checkedView = mGridView.getChildAt(position
                                - mGridView.getFirstVisiblePosition());
                        ImageGalleryAdapter.ViewHolder holder = (ImageGalleryAdapter.ViewHolder) checkedView
                                .getTag();
                        holder.overlayImgView.setVisibility(View.VISIBLE);
                        noOfItemsSelected++;
                    } else {
                        mAlbumsList.get(position).setChecked(false);
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
                    for (int i = 0; i < mAlbumsList.size(); i++) {
                        mAlbumsList.get(i).setChecked(false);
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
            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                }
            });
        }else {
            mLayout.setBackgroundResource(R.drawable.img_no_pic);
        }
    }
}
