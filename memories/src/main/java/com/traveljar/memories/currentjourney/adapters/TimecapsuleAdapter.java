package com.traveljar.memories.currentjourney.adapters;

/**
 * Created by abhi on 26/06/15.
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.traveljar.memories.R;
import com.traveljar.memories.models.Timecapsule;
import com.traveljar.memories.utility.HelpMe;

import java.io.File;
import java.util.ArrayList;

public class TimecapsuleAdapter extends RecyclerView.Adapter<TimecapsuleAdapter.ViewHolder> {
    private ArrayList<Timecapsule> mDataset;
    private Context context;

    // Provide a suitable constructor (depends on the kind of dataset)
    public TimecapsuleAdapter(ArrayList<Timecapsule> myDataset, Context context) {
        mDataset = myDataset;
        this.context = context;
    }

    public void add(int position, Timecapsule item) {
        mDataset.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(Timecapsule item) {
        int position = mDataset.indexOf(item);
        mDataset.remove(position);
        notifyItemRemoved(position);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.current_journey_timecapsule_list_item,
                parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Timecapsule timecapsuleItem = mDataset.get(position);
        final String bigDate = HelpMe.getDate(timecapsuleItem.getCreatedAt(), HelpMe.DATE_ONLY);
        final String day = HelpMe.getDate(timecapsuleItem.getCreatedAt(), HelpMe.ONLY_DAY);

        holder.txtNoteDate.setText(bigDate);
        holder.txtNoteDay.setText(day);
        Glide.with(context).load(Uri.fromFile(new File(timecapsuleItem.getVideoLocalURL()))).into(holder.timeCapsuleCoverPic);
        holder.timeCapsuleCoverPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String videoUrl = "http://videosite/myvideo.mp4";
                Intent playVideo = new Intent(Intent.ACTION_VIEW);
                playVideo.setDataAndType(Uri.parse(timecapsuleItem.getVideoServerURL()), "video/mp4");
                context.startActivity(playVideo);

            /*    Intent mediaIntent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(new File(timecapsuleItem.getVideoLocalURL())));
                mediaIntent.setDataAndType(Uri.fromFile(new File(timecapsuleItem.getVideoLocalURL())), "video*//*");
                context.startActivity(mediaIntent);*/
            }
        });

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
        public TextView txtNoteDate;
        public TextView txtNoteDay;
        public ImageView timeCapsuleCoverPic;

        public ViewHolder(View v) {
            super(v);
            txtNoteDate = (TextView) v.findViewById(R.id.cur_journey_timecapsule_item_big_date);
            txtNoteDay = (TextView) v.findViewById(R.id.cur_journey_timecapsule_item_day);
            timeCapsuleCoverPic = (ImageView) v.findViewById(R.id.timecapsule_cover_pic);
        }
    }

}