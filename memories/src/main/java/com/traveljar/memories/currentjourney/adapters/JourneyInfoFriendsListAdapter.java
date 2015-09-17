package com.traveljar.memories.currentjourney.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.ContactDataSource;
import com.traveljar.memories.SQLitedatabase.ContactJourneyMappingDataSource;
import com.traveljar.memories.SQLitedatabase.JourneyDataSource;
import com.traveljar.memories.models.Contact;
import com.traveljar.memories.utility.Constants;
import com.traveljar.memories.utility.JourneyUtil;
import com.traveljar.memories.utility.TJPreferences;

import java.io.File;
import java.util.List;

public class JourneyInfoFriendsListAdapter extends RecyclerView.Adapter<JourneyInfoFriendsListAdapter.ViewHolder> implements JourneyUtil.OnAddBuddyListener {
    private static final String TAG = "<JInfoFriendsAdapter>";
    private List<Contact> mDataset;
    private Context mContext;
    ProgressDialog mDialog;

    // Provide a suitable constructor (depends on the kind of dataset)
    public JourneyInfoFriendsListAdapter(List<Contact> myDataset, Context context) {
        mDataset = myDataset;
        mContext = context;
        mDialog = new ProgressDialog(context);
        mDialog.setCanceledOnTouchOutside(false);
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

    public void updateContactsList(List<Contact> contactsList) {
        mDataset = contactsList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
        String name = contactItem.getPhoneBookName() == null ? contactItem.getProfileName() : contactItem.getPhoneBookName();
        final String profileLocalURL = contactItem.getPicLocalUrl();

        Log.d(TAG, "info are : " + name);

        holder.contactName.setText(name);

        if (profileLocalURL != null) {
            Glide.with(mContext).load(Uri.fromFile(new File(profileLocalURL))).asBitmap().into(holder.contactImage);
        } else {
            Glide.with(mContext).load(Uri.fromFile(new File(Constants.GUMNAAM_IMAGE_URL))).asBitmap().into(holder.contactImage);
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
            //mDialog.show();
            Log.d(TAG, getAdapterPosition() + "===" + getLayoutPosition());
            Contact contact = mDataset.get(getLayoutPosition());
            JourneyUtil.getInstance().setAddBuddyListener(JourneyInfoFriendsListAdapter.this);
            JourneyUtil.getInstance().addUserToJourney(mContext, contact.getIdOnServer());
        }
    }

    @Override
    public void onAddBuddy(String contactId, int resultCode) {
        if(resultCode == 0) {
            mDialog.dismiss();
            mDataset.remove(ContactDataSource.getContactById(mContext, contactId));
            ContactJourneyMappingDataSource.addMapping(mContext, contactId, TJPreferences.getActiveJourneyId(mContext), true);
            this.notifyDataSetChanged();
            JourneyDataSource.addContactToJourney(mContext, contactId, TJPreferences.getActiveJourneyId(mContext));
        }else {
            Toast.makeText(mContext, "Unable to add contact to the journey please try again", Toast.LENGTH_SHORT).show();
            mDialog.dismiss();
        }
    }
}