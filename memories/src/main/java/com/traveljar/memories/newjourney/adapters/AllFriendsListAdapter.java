package com.traveljar.memories.newjourney.adapters;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.traveljar.memories.R;
import com.traveljar.memories.models.Contact;

import java.util.ArrayList;
import java.util.List;

public class AllFriendsListAdapter extends ArrayAdapter<Contact> implements Filterable {

    protected static final String TAG = "AllFriendsListAdapter";
    private List<Contact> originalList;
    private Activity context;
    private List<Contact> list;

    public AllFriendsListAdapter(Activity context, List<Contact> list) {
        super(context, R.layout.new_journey_traveljar_contact_list_item, list);
        this.context = context;
        this.list = list;
        this.originalList = list;
    }

    public void updateList(List<Contact> contactList) {
        originalList = contactList;
        list = contactList;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Log.d(TAG, originalList.get(position).getName());
        final ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.new_journey_traveljar_contact_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.all_contacts_contact_name);
            viewHolder.phone_no = (TextView) convertView.findViewById(R.id.all_contacts_contact_phone);
            viewHolder.img = (ImageView) convertView.findViewById(R.id.all_contacts_contact_image);
            viewHolder.checkbox = (CheckBox) convertView.findViewById(R.id.all_contacts_checkbox);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
            Log.d(TAG, "convert view is NOT null" + list.size() + "position = " + position);
        }

        viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Contact element = list.get(position);
                element.setSelected(isChecked);
            }
        });
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.checkbox.setChecked(!viewHolder.checkbox.isChecked());
                Contact element = list.get(position);
                element.setSelected(viewHolder.checkbox.isChecked());
            }
        });

        viewHolder.name.setText(list.get(position).getName());

        if (list.get(position).getPhone_no() == null || list.get(position).getPhone_no() == "") {
            viewHolder.phone_no.setText(list.get(position).getPrimaryEmail());
        } else {
            viewHolder.phone_no.setText(list.get(position).getPhone_no());
        }

        if (list.get(position).getPicLocalUrl() != null) {
            viewHolder.img.setImageBitmap(BitmapFactory.decodeFile(list.get(position).getPicLocalUrl()));
        }

        viewHolder.checkbox.setChecked(list.get(position).isSelected());
        return convertView;
    }

    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                list = (List<Contact>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                Log.d(TAG, "filtering add contacts list!");

                FilterResults results = new FilterResults();
                List<Contact> FilteredArrayNames = new ArrayList<Contact>();

                // perform your search here using the searchConstraint String.
                constraint = constraint.toString().toLowerCase();
                Log.d(TAG, originalList.size() + "!");
                for (int i = 0; i < originalList.size(); i++) {
                    Contact contactName = originalList.get(i);
                    Log.d(TAG, "1.3");
                    if (contactName.getName().toLowerCase().startsWith(constraint.toString())) {
                        FilteredArrayNames.add(contactName);
                    }
                }

                results.count = FilteredArrayNames.size();
                results.values = FilteredArrayNames;

                Log.d(TAG, "1.7");
                return results;
            }
        };

        return filter;
    }

    static class ViewHolder {
        protected TextView name;
        protected TextView phone_no;
        protected ImageView img;
        protected CheckBox checkbox;
    }

}