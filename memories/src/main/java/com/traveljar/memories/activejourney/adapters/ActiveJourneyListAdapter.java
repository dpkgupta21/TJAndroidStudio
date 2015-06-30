package com.traveljar.memories.activejourney.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.ContactDataSource;
import com.traveljar.memories.SQLitedatabase.PictureDataSource;
import com.traveljar.memories.currentjourney.CurrentJourneyBaseActivity;
import com.traveljar.memories.customevents.ContactsFetchEvent;
import com.traveljar.memories.models.Journey;
import com.traveljar.memories.models.Lap;
import com.traveljar.memories.models.Picture;
import com.traveljar.memories.services.PullBuddies;
import com.traveljar.memories.utility.HelpMe;
import com.traveljar.memories.utility.TJPreferences;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class ActiveJourneyListAdapter extends RecyclerView.Adapter<ActiveJourneyListAdapter.ViewHolder>  {
    private static final String TAG = "<ActiveJListAdapter>";
    private List<Journey> mDataset;
    private Context mContext;
    private ProgressDialog mDialog;

    // For the request bus receive event to discard the received event which is not meant for this activity
    private static int LISTENER_CODE = 0;

    // Provide a suitable constructor (depends on the kind of dataset)
    public ActiveJourneyListAdapter(List<Journey> myDataset, Context context) {
        mDataset = myDataset;
        mContext = context;
        mDialog = new ProgressDialog(mContext);
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
        final String tagline = journeyItem.getTagLine();
        final String buddyCount = (journeyItem.getBuddies().size() + 1) + " people";
        List<Lap> lapList = journeyItem.getLapsList();
        String place = "";
        if(lapList.size() > 0){
            place = lapList.get(0).getDestinationCityName();
        }
        Log.d(TAG, "place name is " + place);

        Log.d(TAG, "info are : " + name + journeyItem.getIdOnServer());

        holder.journeyName.setText(name);
        holder.journeyTagline.setText(tagline);
        holder.journeyBuddyCount.setText(buddyCount);
        holder.journeyPlace.setText(place);

        Picture coverPic = PictureDataSource.getRandomPicOfJourney(mDataset.get(position).getIdOnServer(), mContext);

        if (coverPic != null) {
            try {
                holder.journeyCoverPic.setImageBitmap(HelpMe.decodeSampledBitmapFromPath(mContext, coverPic.getPicThumbnailPath(), 512, 384));
                AlphaAnimation alpha = new AlphaAnimation(0.5F, 0.5F); // change values as you want
                alpha.setDuration(0); // Make animation instant
                alpha.setFillAfter(true); // Tell it to persist after the animation ends

                // And then on your imageview
                holder.journeyCoverPic.startAnimation(alpha);
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

    public void updateList(List<Journey> updatedList) {
        mDataset = updatedList;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView journeyName;
        public TextView journeyTagline;
        public TextView journeyBuddyCount;
        public TextView journeyPlace;
        public ImageView journeyCoverPic;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            journeyName = (TextView) v.findViewById(R.id.active_journey_list_name);
            journeyTagline = (TextView) v.findViewById(R.id.active_journey_list_tagline);
            journeyBuddyCount = (TextView) v.findViewById(R.id.active_journey_list_buddy_count);
            journeyPlace = (TextView) v.findViewById(R.id.active_journey_list_buddy_place);
            journeyCoverPic = (ImageView) v.findViewById(R.id.active_journey_cover_pic);
        }

        // In Recycler views OnItemCLick is handled here
        @Override
        public void onClick(View v) {
            mDialog.setMessage("Please wait, fetching memories!");
            mDialog.show();
            Log.d(TAG, getAdapterPosition() + "===" + getLayoutPosition());
            Journey journey = mDataset.get(getLayoutPosition());
            TJPreferences.setActiveJourneyId(mContext, journey.getIdOnServer());

            // Fetch all those contacts which are not in the contacts list of current user but are on the journey
            Log.d(TAG, "buddies from journey " + journey.getBuddies());
            if (journey.getBuddies() != null && !journey.getBuddies().isEmpty()) {
                Log.d(TAG, "buddies are = " + journey.getBuddies());
                ArrayList<String> buddyList = (ArrayList<String>) ContactDataSource.getNonExistingContacts(mContext, journey.getBuddies());

                Log.d(TAG, "non existing contacts list is" + buddyList);

                if (buddyList != null && !buddyList.isEmpty()) {
                    Log.d(TAG, "some buddies need to be fetched from server hence fetching from server" + buddyList.size());
                    registerEvent();
                    new PullBuddies(mContext, buddyList, LISTENER_CODE).fetchBuddies();
                } else {
                    Log.d(TAG, "no buddy to be fetched from server hence starting current activity");
                    Intent intent = new Intent(mContext, CurrentJourneyBaseActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    Log.d(TAG, "context " + mContext + " -- " + intent);
                }
            } else {
                Log.d(TAG, "all required contacts are already present in the database");
                mDialog.dismiss();
                Intent intent = new Intent(mContext, CurrentJourneyBaseActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }

        }
    }

    private void registerEvent(){
        EventBus.getDefault().register(this);
    }

    private void unRegisterEvent(){
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(ContactsFetchEvent event) {
        //Discard the event if the event's activity code is not similar to its own activity code
        if (event.getActivityCode() == LISTENER_CODE) {
            mDialog.dismiss();
            if(event.isSuccess()) {
                Intent intent = new Intent(mContext, CurrentJourneyBaseActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.getApplicationContext().startActivity(intent);
            }else {
                Toast.makeText(mContext, "We are unable to fetch your journey write now please try after some time", Toast.LENGTH_SHORT).show();
            }
            unRegisterEvent();
        }
    }
}