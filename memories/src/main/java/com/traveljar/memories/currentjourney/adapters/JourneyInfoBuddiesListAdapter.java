package com.traveljar.memories.currentjourney.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.ContactDataSource;
import com.traveljar.memories.SQLitedatabase.JourneyDataSource;
import com.traveljar.memories.models.Contact;
import com.traveljar.memories.utility.HelpMe;
import com.traveljar.memories.utility.JourneyUtil;
import com.traveljar.memories.utility.TJPreferences;

import java.util.List;

public class JourneyInfoBuddiesListAdapter extends RecyclerView.Adapter<JourneyInfoBuddiesListAdapter.ViewHolder> implements JourneyUtil.OnExitJourneyListener {
    private static final String TAG = "<JInfoBudListAdapter>";
    private List<Contact> mDataset;
    private Context context;
    private ProgressDialog mDialog;

    // Provide a suitable constructor (depends on the kind of dataset)
    public JourneyInfoBuddiesListAdapter(List<Contact> myDataset, Context context) {
        mDataset = myDataset;
        this.context = context;
        mDialog = new ProgressDialog(context);
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

    public void updateList(List<Contact> dataSet) {
        mDataset = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public JourneyInfoBuddiesListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "view group is " + parent + " " + mDataset.size());
        parent.getLayoutParams().height = convertDpToPixels(mDataset.size() * 90);

        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.current_journey_info_buddies_list_item,
                parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Contact contactsItem = mDataset.get(position);
        final String name = contactsItem.getPhoneBookName() == null ? contactsItem.getProfileName() : contactsItem.getPhoneBookName();
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
        if (HelpMe.isAdmin(context)) {
            holder.removeBuddyIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(context)
                            .setTitle("Remove Friend")
                            .setMessage("Are you sure you want to remove your friend from this journey")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    mDialog.show();
                                    JourneyUtil.getInstance().setExitJourneyListener(JourneyInfoBuddiesListAdapter.this);
                                    JourneyUtil.getInstance().exitJourney(context, mDataset.get(position).getIdOnServer());
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            });
        } else {
            holder.removeBuddyIcon.setVisibility(View.GONE);
        }
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
        public ImageView removeBuddyIcon;

        public ViewHolder(View v) {
            super(v);
            buddyName = (TextView) v.findViewById(R.id.cur_journey_buddies_name);
            buddyStatus = (TextView) v.findViewById(R.id.cur_journey_buddies_status);
            buddyPicImageView = (ImageView) v.findViewById(R.id.cur_journey_buddies_image);
            removeBuddyIcon = (ImageView) v.findViewById(R.id.journey_info_buddies_remove);
        }
    }

    @Override
    public void onExitJourney(int resultCode, String userId) {
        if (resultCode == 0) {
            mDialog.dismiss();
            JourneyDataSource.removeContactFromJourney(context, userId, TJPreferences.getActiveJourneyId(context));
            /*MemoriesDataSource.deleteAllMemoriesCreatedByUser(context, userId);
            MemoriesDataSource.removeUserFromMemories(context, userId);*/
            //mDataset = ContactDataSource.getContactsFromJourney(context, TJPreferences.getActiveJourneyId(context));
            mDataset = ContactDataSource.getAllContactsFromJourney(context, TJPreferences.getActiveJourneyId(context));
            this.notifyDataSetChanged();
        } else {
            mDialog.dismiss();
            Toast.makeText(context, "some error occured please try again later", Toast.LENGTH_SHORT).show();
        }
    }

    private int convertDpToPixels(int dp) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
        return (int) px;
    }

}