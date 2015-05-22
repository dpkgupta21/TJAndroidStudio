package com.example.memories.newjourney.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.memories.R;
import com.example.memories.SQLitedatabase.ContactDataSource;
import com.example.memories.models.Journey;
import com.example.memories.services.PullBuddiesService;
import com.example.memories.utility.TJPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ankit on 19/5/15.
 */
public class PendingJourneysListAdapter extends BaseAdapter {

    private static final String TAG = "PENDING_JOURNEY_ADAPTER";
    static Context mContext;
    List<Journey> mJourneyList;

    public PendingJourneysListAdapter(Context context, List<Journey> journeyList){
        mContext = context;
        mJourneyList = journeyList;
    }

    @Override
    public int getCount() {
        return mJourneyList.size();
    }

    @Override
    public Object getItem(int position) {
        return mJourneyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.pending_request_list_item, null);
        }
        final Journey journey = mJourneyList.get(position);
//        Contact createdBy = ContactDataSource.getContactById(mContext, journey.getCreatedBy());
//        ((TextView) convertView.findViewById(R.id.journeyCreatedByTxt)).setText(createdBy.getName());
        ((TextView) convertView.findViewById(R.id.journeyCreatedByTxt)).setText("Ankit Aggarwal");
        ((TextView) convertView.findViewById(R.id.journeyNameTxt)).setText(journey.getName());
        //((TextView) convertView.findViewById(R.id.journeyCreatedByTxt)).setText(createdBy.getName());

        convertView.findViewById(R.id.joinJourneyBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TJPreferences.setActiveJourneyId(mContext, journey.getIdOnServer());

                // Fetch all those contacts which are not in the contacts list of current user but are on the journey
                ArrayList<String> buddyList = (ArrayList<String>) ContactDataSource.getNonExistingContacts(mContext, journey.getBuddies());
                Log.d(TAG, "Total contacts " + buddyList.toString());
                if (buddyList.size() > 0) {
                    Intent intent = new Intent(mContext, PullBuddiesService.class);
                    intent.putStringArrayListExtra("BUDDY_IDS", buddyList);
                    mContext.startService(intent);
                }

                //Fetch all the memories
                /*Intent intent = new Intent(mContext, Timeline.class);
                mContext.startActivity(intent);*/
            }
        });
        return convertView;
    }

}
