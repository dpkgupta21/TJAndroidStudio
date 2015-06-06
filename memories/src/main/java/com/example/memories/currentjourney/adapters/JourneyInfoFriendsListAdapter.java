package com.example.memories.currentjourney.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.memories.R;
import com.example.memories.models.Contact;
import com.example.memories.utility.Constants;
import com.example.memories.utility.HelpMe;

import java.io.FileNotFoundException;
import java.util.List;

public class JourneyInfoFriendsListAdapter extends RecyclerView.Adapter<JourneyInfoFriendsListAdapter.ViewHolder> {
    private static final String TAG = "<JInfoFriendsAdapter>";
    private List<Contact> mDataset;
    private Context mContext;

    // Provide a suitable constructor (depends on the kind of dataset)
    public JourneyInfoFriendsListAdapter(List<Contact> myDataset, Context context) {
        mDataset = myDataset;
        mContext = context;
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
    public JourneyInfoFriendsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.journey_info_friends_list_item,
                parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Contact contactItem = mDataset.get(position);
        final String name = contactItem.getName();
        final String profileLocalURL = contactItem.getPicLocalUrl();

        Log.d(TAG, "info are : " + name);

        holder.contactName.setText(name);

        try {
            if (profileLocalURL != null) {
                holder.contactImage.setImageBitmap(HelpMe.decodeSampledBitmapFromPath(mContext, profileLocalURL, 100, 100));
            } else {
                holder.contactImage.setImageBitmap(HelpMe.decodeSampledBitmapFromPath(mContext, Constants.GUMNAAM_IMAGE_URL, 100, 100));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void updateList(List<Contact> updatedList) {
        mDataset = updatedList;
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView contactName;
        public ImageView contactImage;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            contactName = (TextView) v.findViewById(R.id.journey_info_friends_list_name);
            contactImage = (ImageView) v.findViewById(R.id.journey_info_friends_list_contact_image);
        }

        // In Recycler views OnItemCLick is handled here
        @Override
        public void onClick(View v) {
            Log.d(TAG, getAdapterPosition() + "===" + getLayoutPosition());
            Contact journey = mDataset.get(getLayoutPosition());



        }
    }

}