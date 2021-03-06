package com.traveljar.memories.newjourney.adapters;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.traveljar.memories.R;
import com.traveljar.memories.models.Contact;

import java.util.ArrayList;
import java.util.List;

public class FriendsAutoCompleteAdapter extends ArrayAdapter<Contact> implements Filterable {

    protected static final String TAG = null;
    private List<Contact> originalList;
    private Activity context;
    private List<Contact> list;

    public FriendsAutoCompleteAdapter(Activity context, List<Contact> list) {
        super(context, R.layout.multi_autocomplete_contact_item, list);
        this.context = context;
        this.list = list;
        originalList = list;
        Log.d(TAG, "list is " + list);
    }

    public void updateList(List<Contact> contactList){
        this.list = contactList;
        originalList = contactList;
    }

    public Contact getFilteredContactAtPosition(int position){
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        Contact contact = list.get(position);
        if (convertView == null) {
            Log.d(TAG, "convert view iss null" + list.size() + "position = " + position);
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.multi_autocomplete_contact_item, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.contact_name);
            viewHolder.contact_info = (TextView)view.findViewById(R.id.contact_info);
            viewHolder.img = (ImageView)view.findViewById(R.id.profile_img);

            view.setTag(viewHolder);
        } else {
            Log.d(TAG, "convert view is NOT null" + list.size() + "position = " + position);
            view = convertView;
        }

        ViewHolder holder = (ViewHolder) view.getTag();
        String name = contact.getPhoneBookName() == null ? contact.getProfileName() : contact.getPhoneBookName();
        holder.name.setText(name);
        if (contact.getPhoneNo() == null || contact.getPhoneNo().equals("")) {
            holder.contact_info.setText(contact.getPrimaryEmail());
        } else {
            holder.contact_info.setText(contact.getPhoneNo());
        }
        if (contact.getPicLocalUrl() != null) {
            holder.img.setImageBitmap(BitmapFactory.decodeFile(contact.getPicLocalUrl()));
        }

        return view;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                //If there's nothing to filter on, return the original data for your list
                if (charSequence == null || charSequence.length() == 0) {
                    filterResults.values = originalList;
                    filterResults.count = originalList.size();
                } else {
                    ArrayList<Contact> resultList = new ArrayList<>();
                    for (Contact contact : originalList) {
                        String name = contact.getPhoneBookName() == null ? contact.getProfileName() : contact.getPhoneBookName();
                        if (name.toLowerCase().startsWith(charSequence.toString()))
                            resultList.add(contact);
                    }
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                list = (ArrayList) filterResults.values;
                if (filterResults.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }

    static class ViewHolder {
        protected TextView name;
        protected TextView contact_info;
        protected ImageView img;
    }

}