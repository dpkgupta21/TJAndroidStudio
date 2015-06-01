package com.example.memories.activejourney.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.memories.R;
import com.example.memories.SQLitedatabase.ContactDataSource;
import com.example.memories.SQLitedatabase.PictureDataSource;
import com.example.memories.currentjourney.CurrentJourneyBaseActivity;
import com.example.memories.models.Journey;
import com.example.memories.models.Picture;
import com.example.memories.services.CustomResultReceiver;
import com.example.memories.services.PullBuddiesService;
import com.example.memories.utility.HelpMe;
import com.example.memories.utility.TJPreferences;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class ActiveJourneyListAdapter extends RecyclerView.Adapter<ActiveJourneyListAdapter.ViewHolder> {
    private static final String TAG = "<ActiveJListAdapter>";
    private List<Journey> mDataset;
    private Context mContext;
    private static final int REQUEST_FETCH_BUDDIES = 1;
    public CustomResultReceiver mReceiver;

    // Provide a suitable constructor (depends on the kind of dataset)
    public ActiveJourneyListAdapter(List<Journey> myDataset, Context context) {
        mDataset = myDataset;
        mContext = context;
    }

    public void add(int position, Journey item) {
        mDataset.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(Journey item) {
        int position = mDataset.indexOf(item);
        mDataset.remove(position);
        notifyItemRemoved(position);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ActiveJourneyListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.active_journey_list_item,
                parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Journey journeyItem = mDataset.get(position);
        final String name = journeyItem.getName();

        Log.d(TAG, "info are : " + name);

        holder.journeyName.setText(name);

        Picture coverPic = PictureDataSource.getRandomPicOfJourney(mDataset.get(position).getIdOnServer(), mContext);

        if (coverPic != null) {
            try {
                holder.journeyCoverPic.setImageBitmap(HelpMe.decodeSampledBitmapFromPath(mContext, coverPic.getPicThumbnailPath(), 512, 384));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void updateList(List<Journey> updatedList){
        mDataset = updatedList;
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView journeyName;
        public ImageView journeyCoverPic;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            journeyName = (TextView) v.findViewById(R.id.active_journey_list_name);
            journeyCoverPic = (ImageView) v.findViewById(R.id.active_journey_cover_pic);
        }

        // In Recycler views OnItemCLick is handled here
        @Override
        public void onClick(View v) {
            Log.d(TAG, getAdapterPosition() + "===" + getLayoutPosition());
            Journey journey = mDataset.get(getLayoutPosition());
            TJPreferences.setActiveJourneyId(mContext, journey.getIdOnServer());

            // Fetch all those contacts which are not in the contacts list of current user but are on the journey
            if (journey.getBuddies() != null && journey.getBuddies().isEmpty()) {
                ArrayList<String> buddyList = (ArrayList<String>) ContactDataSource.getNonExistingContacts(mContext, journey.getBuddies());
                if (!buddyList.isEmpty() && buddyList != null) {
                    Intent intent = new Intent(mContext, PullBuddiesService.class);
                    intent.putExtra("REQUEST_CODE", REQUEST_FETCH_BUDDIES);
                    intent.putExtra("RECEIVER", mReceiver);
                    intent.putStringArrayListExtra("BUDDY_IDS", buddyList);
                    mContext.startService(intent);
                } else {
                    Intent intent = new Intent(mContext, CurrentJourneyBaseActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.getApplicationContext().startActivity(intent);
                }
            } else {
                Log.d(TAG, "all required contacts are already present in the database");
                Intent intent = new Intent(mContext, CurrentJourneyBaseActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.getApplicationContext().startActivity(intent);
            }

        }
    }

}