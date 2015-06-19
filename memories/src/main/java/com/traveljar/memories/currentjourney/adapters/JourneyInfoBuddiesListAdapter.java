package com.traveljar.memories.currentjourney.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.traveljar.memories.R;
import com.traveljar.memories.models.Contact;

import java.util.List;

public class JourneyInfoBuddiesListAdapter extends RecyclerView.Adapter<JourneyInfoBuddiesListAdapter.ViewHolder> {
    private static final String TAG = "<JInfoBudListAdapter>";
    private List<Contact> mDataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    public JourneyInfoBuddiesListAdapter(List<Contact> myDataset) {
        mDataset = myDataset;
    }

    public void add(int position, Contact item) {
        mDataset.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(Contact item) {
        int position = mDataset.indexOf(item);
        mDataset.remove(position);
        notifyItemRemoved(position);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public JourneyInfoBuddiesListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.current_journey_info_buddies_list_item,
                parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Contact contactsItem = mDataset.get(position);
        final String name = contactsItem.getName();
        final String status = contactsItem.getStatus();
        final String picLocalURL = contactsItem.getPicLocalUrl();

        Log.d(TAG, "info are : " + name + "---" + status + "---" + picLocalURL);

        if (picLocalURL != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(picLocalURL, new BitmapFactory.Options());
            holder.buddyPicImageView.setImageBitmap(bitmap);
        } else {
            holder.buddyPicImageView.setImageResource(R.drawable.gumnaam_profile_image);
        }

        holder.buddyName.setText(name);
        holder.buddyStatus.setText(status);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView buddyName;
        public TextView buddyStatus;
        public ImageView buddyPicImageView;

        public ViewHolder(View v) {
            super(v);
            buddyName = (TextView) v.findViewById(R.id.cur_journey_buddies_name);
            buddyStatus = (TextView) v.findViewById(R.id.cur_journey_buddies_status);
            buddyPicImageView = (ImageView) v.findViewById(R.id.cur_journey_buddies_image);
        }
    }

}