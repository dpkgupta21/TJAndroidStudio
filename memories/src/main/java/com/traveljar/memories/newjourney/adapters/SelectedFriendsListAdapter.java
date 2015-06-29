package com.traveljar.memories.newjourney.adapters;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.traveljar.memories.R;
import com.traveljar.memories.models.Contact;

import java.util.List;

public class SelectedFriendsListAdapter extends ArrayAdapter<Contact> {
    private static final String TAG = "[SelectedFriendAdapter]";
    private final Activity context;
    private List<Contact> names;

    public SelectedFriendsListAdapter(Activity context, List<Contact> contactList) {
        super(context, R.layout.new_journey_selected_friends_list_item, contactList);
        Log.d(TAG, "construcor");
        this.context = context;
        this.names = contactList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.new_journey_selected_friends_list_item, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();

            viewHolder.name = (TextView) rowView.findViewById(R.id.add_friends_contact_name);
            viewHolder.contactInfo = (TextView) rowView.findViewById(R.id.add_friends_contact_info);
            viewHolder.image = (ImageView) rowView.findViewById(R.id.add_friends_contact_image);
            viewHolder.deleteIcon = (ImageView) rowView
                    .findViewById(R.id.add_friends_contact_remove_btn);
            viewHolder.deleteIcon.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Log.d(TAG, "item clicked" + v.getTag());
                    // Model element = (Model) viewHolder.checkbox.getTag();
                    // element.setSelected(buttonView.isChecked());
                    names.remove(position);
                    notifyDataSetChanged();
                }
            });
            rowView.setTag(viewHolder);
        }

        // fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();
        String n = names.get(position).getPhoneBookName() == null ? names.get(position).getProfileName() : names.get(position).getPhoneBookName();
        holder.name.setText(n);
        Log.d(TAG, "pic local url for contact = " + names.get(position).getPicLocalUrl());
        if (names.get(position).getPicLocalUrl() != null) {
            holder.image.setImageBitmap(BitmapFactory.decodeFile(names.get(position).getPicLocalUrl()));
        }

        if (names.get(position).getPhoneNo() == null || names.get(position).getPhoneNo() == "") {
            holder.contactInfo.setText(names.get(position).getPrimaryEmail());
        } else {
            holder.contactInfo.setText(names.get(position).getPhoneNo());
        }
        // holder.image.setImageResource(resId);
        return rowView;
    }

    static class ViewHolder {
        public TextView name;
        public ImageView image;
        public ImageView deleteIcon;
        public TextView contactInfo;
    }
}
