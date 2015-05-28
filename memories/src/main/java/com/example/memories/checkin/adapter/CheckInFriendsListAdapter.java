package com.example.memories.checkin.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.memories.R;
import com.example.memories.models.Contact;

import java.util.List;

public class CheckInFriendsListAdapter extends BaseAdapter {
    private static final String TAG = "checkinFriendsAdapter";
    Context mContext;
    List<Contact> mContactsList;

    public CheckInFriendsListAdapter(Context context, List<Contact> contactsList) {
        mContext = context;
        mContactsList = contactsList;
    }

    @Override
    public int getCount() {
        return mContactsList.size();
    }

    @Override
    public Object getItem(int position) {
        return mContactsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "checkin friends grid adapter");
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.mood_select_friends_grid_item, parent, false);
        }
        ImageView profileImg = (ImageView) convertView.findViewById(R.id.friendImg);
        ImageView overlayImg = (ImageView) convertView.findViewById(R.id.overlayImg);
        TextView friendName = (TextView) convertView.findViewById(R.id.friendName);

        Contact contact = mContactsList.get(position);

        if (contact.getPicLocalUrl() != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(contact.getPicLocalUrl(), new BitmapFactory.Options());
            profileImg.setImageBitmap(bitmap);
        } else {
            profileImg.setImageResource(R.drawable.ic_profile);
        }

        friendName.setText(mContactsList.get(position).getName());
        if (mContactsList.get(position).isSelected()) {
            overlayImg.setVisibility(View.VISIBLE);
        }
        return convertView;
    }
}