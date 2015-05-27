package com.example.memories.newjourney.adapters;

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

import com.example.memories.R;
import com.example.memories.models.Contact;

import java.util.ArrayList;
import java.util.List;

public class AllFriendsListAdapter extends ArrayAdapter<Contact> implements Filterable {

    protected static final String TAG = null;
    private final List<Contact> originalList;
    private final Activity context;
    private List<Contact> list;

    public AllFriendsListAdapter(Activity context, List<Contact> list) {
        super(context, R.layout.new_journey_traveljar_contact_list_item, list);
        this.context = context;
        this.list = list;
        this.originalList = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            Log.d(TAG, "convert view iss null" + list.size() + "position = " + position);
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.new_journey_traveljar_contact_list_item, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.all_contacts_contact_name);
            viewHolder.phone_no = (TextView) view.findViewById(R.id.all_contacts_contact_phone);
            viewHolder.img = (ImageView) view.findViewById(R.id.all_contacts_contact_image);
            viewHolder.checkbox = (CheckBox) view.findViewById(R.id.all_contacts_checkbox);

            viewHolder.checkbox
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            Contact element = (Contact) viewHolder.checkbox.getTag();
                            element.setSelected(buttonView.isChecked());

                        }
                    });
            view.setTag(viewHolder);
            viewHolder.checkbox.setTag(list.get(position));
        } else {
            Log.d(TAG, "convert view is NOT null" + list.size() + "position = " + position);
            view = convertView;
            ((ViewHolder) view.getTag()).checkbox.setTag(list.get(position));
        }

        Log.d(TAG, "concvert view awayi" + list.size() + "position = " + position);
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.name.setText(list.get(position).getName());

        if (list.get(position).getPhone_no() == null || list.get(position).getPhone_no() == "") {
            holder.phone_no.setText(list.get(position).getPrimaryEmail());
        } else {
            holder.phone_no.setText(list.get(position).getPhone_no());
        }

        if (list.get(position).getPicLocalUrl() != null) {
            holder.img.setImageBitmap(BitmapFactory.decodeFile(list.get(position).getPicLocalUrl()));
        }

        // Uri imgUri = Uri.parse(list.get(position).getProfilePic());
        // holder.img.setImageURI(imgUri);

        holder.checkbox.setChecked(list.get(position).isSelected());
        return view;
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