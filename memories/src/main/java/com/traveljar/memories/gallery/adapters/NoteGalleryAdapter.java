package com.traveljar.memories.gallery.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.traveljar.memories.R;
import com.traveljar.memories.models.Memories;
import com.traveljar.memories.models.Note;
import com.traveljar.memories.utility.HelpMe;

import java.util.ArrayList;

public class NoteGalleryAdapter extends RecyclerView.Adapter<NoteGalleryAdapter.ViewHolder> {
    private ArrayList<Memories> mDataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    public NoteGalleryAdapter(ArrayList<Memories> myDataset) {
        mDataset = myDataset;
    }

    public void add(int position, Memories item) {
        mDataset.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(Memories item) {
        int position = mDataset.indexOf(item);
        mDataset.remove(position);
        notifyItemRemoved(position);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public NoteGalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_notes_item,
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
        final Note NotesItem = (Note) mDataset.get(position);
        final String name = NotesItem.getContent();
        final String bigDate = HelpMe.getDate(NotesItem.getCreatedAt(), HelpMe.DATE_ONLY);
        final String day = HelpMe.getDate(NotesItem.getCreatedAt(), HelpMe.ONLY_DAY);

        holder.txtNote.setText(name);
        holder.txtNoteDate.setText(bigDate);
        holder.txtNoteDay.setText(day);
        holder.txtNote.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

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
        public TextView txtNote;
        public TextView txtNoteDate;
        public TextView txtNoteDay;

        public ViewHolder(View v) {
            super(v);
            txtNote = (TextView) v.findViewById(R.id.galleryNoteItemtext);
            txtNoteDate = (TextView) v.findViewById(R.id.galleryNoteItemBigDate);
            txtNoteDay = (TextView) v.findViewById(R.id.galleryNoteItemDay);
        }
    }

}