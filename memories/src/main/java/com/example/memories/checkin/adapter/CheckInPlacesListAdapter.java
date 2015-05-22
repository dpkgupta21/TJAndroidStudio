package com.example.memories.checkin.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Cache.Entry;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.memories.R;
import com.example.memories.utility.Constants;
import com.example.memories.volley.AppController;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;

public class CheckInPlacesListAdapter extends ArrayAdapter<Map<String, String>> {
    private static final String TAG = "[TagFileAdapter]";
    private final Activity context;
    private ArrayList<Map<String, String>> names;
    private ViewHolder holder;

    public CheckInPlacesListAdapter(Activity context, ArrayList<Map<String, String>> tagList) {
        super(context, R.layout.checkin_places_list_item, tagList);
        Log.d(TAG, "construcor");
        this.context = context;
        this.names = tagList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.checkin_places_list_item, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.name = (TextView) rowView.findViewById(R.id.checkin_place_name);
            viewHolder.address = (TextView) rowView.findViewById(R.id.checkin_place_address);
            viewHolder.count = (TextView) rowView.findViewById(R.id.checkin_place_count);
            viewHolder.thumbnail = (NetworkImageView) rowView
                    .findViewById(R.id.checkin_place_thumbnail);
            rowView.setTag(viewHolder);
        }

        // fill data
        holder = (ViewHolder) rowView.getTag();
        String n = names.get(position).get("name");
        String a = names.get(position).get("address");
        String c = names.get(position).get("count");
        String t = names.get(position).get("thumbnail");
        holder.name.setText(n);
        holder.address.setText(a);
        holder.count.setText(c);
        makeImageRequest(t);

        return rowView;
    }

    private void makeImageRequest(String imgURL) {
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();

        // If you are using NetworkImageView
        holder.thumbnail.setImageUrl(imgURL, imageLoader);

        // Loading image with placeholder and error image
        // imageLoader.get(Constants.URL_IMAGE,
        // ImageLoader.getImageListener(imageView, R.drawable.abhi,
        // R.drawable.hamburger));

        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Entry entry = cache.get(Constants.URL_IMAGE);
        if (entry != null) {
            try {
                String data = new String(entry.data, "UTF-8");
                // handle data, like converting it to xml, json, bitmap etc.,
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            // cached response doesn't exists. Make a network call here
        }
    }

    static class ViewHolder {
        public TextView name;
        public NetworkImageView thumbnail;
        private TextView address;
        private TextView count;
    }

}
