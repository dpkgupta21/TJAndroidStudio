package com.traveljar.memories.picture;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.traveljar.memories.R;
import com.traveljar.memories.picture.adapters.PicGalleryGridAdapter;

public class UploadImageFromGallery extends Fragment{
    private static final String TAG = "UploadImageFromGallery";
    private View rootView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.media_choose_from_gallery, container, false);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        GridView mGridView = (GridView)rootView.findViewById(R.id.media_grid_view);

        Log.d(TAG, "inside upload image from gallery");
        String[] projection = {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.TITLE
        };

        // Return only video and image metadata.
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE /*+
                " AND " + MediaStore.Images.Media.DATA + " like ? "*/;

        Uri queryUri = MediaStore.Files.getContentUri("external");

        Cursor cursor = getActivity().getContentResolver().query(queryUri, projection, selection, null, null, null);
        cursor.moveToFirst();
        PicGalleryGridAdapter adapter = new PicGalleryGridAdapter(getActivity(), cursor);
        mGridView.setAdapter(adapter);
    }
}
