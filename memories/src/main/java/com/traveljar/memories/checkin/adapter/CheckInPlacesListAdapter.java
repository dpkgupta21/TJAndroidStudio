package com.traveljar.memories.checkin.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Cache.Entry;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.traveljar.memories.R;
import com.traveljar.memories.checkin.CheckInPlacesList;
import com.traveljar.memories.utility.Constants;
import com.traveljar.memories.volley.AppController;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class CheckInPlacesListAdapter extends BaseAdapter implements Filterable {
    private static final String TAG = "[TagFileAdapter]";
    private final Activity context;
    private List<CheckInPlacesList.Place> mOriginalList;
    private List<CheckInPlacesList.Place> mFilteredList;
    private ViewHolder holder;

    public CheckInPlacesListAdapter(Activity context, List<CheckInPlacesList.Place> tagList) {
        this.context = context;
        this.mOriginalList = tagList;
        this.mFilteredList = tagList;
    }

    @Override
    public int getCount() {
        return mFilteredList.size();
    }

    @Override
    public Object getItem(int position) {
        return mFilteredList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void updateList(List<CheckInPlacesList.Place> list){
        mOriginalList = list;
        mFilteredList = list;
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
        CheckInPlacesList.Place  place = mFilteredList.get(position);
        holder.name.setText(place.getName());
        holder.address.setText(place.getAddress());
        holder.count.setText(place.getCheckInCount());
        makeImageRequest(place.getThumbUrl());

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

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                //If there's nothing to filter on, return the original data for your list
                if (charSequence == null || charSequence.length() == 0) {
                    filterResults.values = mOriginalList;
                    filterResults.count = mOriginalList.size();
                } else {
                    ArrayList<CheckInPlacesList.Place> resultList = new ArrayList<>();
                    for (CheckInPlacesList.Place place: mOriginalList) {
                        if(place.getName().toLowerCase().startsWith(charSequence.toString().toLowerCase()))
                            resultList.add(place);
                    }
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (ArrayList) filterResults.values;
//                notifyDataSetChanged();
                if (filterResults.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }

}