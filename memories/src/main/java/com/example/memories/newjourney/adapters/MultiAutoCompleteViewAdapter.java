package com.example.memories.newjourney.adapters;

import android.app.Activity;
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

import java.util.ArrayList;
import java.util.List;

public class MultiAutoCompleteViewAdapter extends ArrayAdapter<Model> implements Filterable {

    protected static final String TAG = null;
    private final List<Model> originalList;
    private final Activity context;
    private List<Model> list;

    public MultiAutoCompleteViewAdapter(Activity context, List<Model> list) {
        super(context, android.R.layout.simple_dropdown_item_1line, list);
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
            view = inflator.inflate(android.R.layout.simple_dropdown_item_1line, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.name = (TextView) view.findViewById(android.R.layout.simple_dropdown_item_1line);

            viewHolder.checkbox = (CheckBox) view.findViewById(R.id.all_contacts_checkbox);

            viewHolder.checkbox
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            Model element = (Model) viewHolder.checkbox.getTag();
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
            holder.phone_no.setText(list.get(position).getEmail());
        } else {
            holder.phone_no.setText(list.get(position).getPhone_no());
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
                list = (List<Model>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                Log.d(TAG, "filtering add contacts list!");

                FilterResults results = new FilterResults();
                List<Model> FilteredArrayNames = new ArrayList<Model>();

                // perform your search here using the searchConstraint String.
                constraint = constraint.toString().toLowerCase();
                Log.d(TAG, originalList.size() + "!");
                for (int i = 0; i < originalList.size(); i++) {
                    Model contactName = originalList.get(i);
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