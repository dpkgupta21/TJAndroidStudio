package com.example.memories;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.memories.activejourney.ActivejourneyList;
import com.example.memories.gallery.GalleryBaseActivity;
import com.example.memories.pastjourney.PastJourneyList;
import com.example.memories.profile.ProfileActivity;
import com.example.memories.settings.Settings;
import com.example.memories.utility.TJPreferences;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by abhi on 28/05/15.
 */
public class SideMenuDrawer extends Fragment {

    private static final String TAG = "<BaseActivity>";
    private static final int REQUEST_CODE_UPDATE_PROFILE = 2;
    ImageView mProfileImg;
    TextView mUserName;
    TextView mUserStatus;
    private View rootView;
    private ImageButton settingsButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.side_menu_drawer, null);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mProfileImg = (ImageView) rootView.findViewById(R.id.sidemenu_profile_pic);
        mUserName = (TextView) rootView.findViewById(R.id.sidemenu_username);
        mUserStatus = (TextView) rootView.findViewById(R.id.sidemenu_status);

        settingsButton = (ImageButton)rootView.findViewById(R.id.sidemenu_settings);

        // Sliding Side Menu Drawer code
        ListView featuresListView = (ListView) rootView.findViewById(R.id.sidemenu_features_list);
        ArrayList<Map<String, String>> featuresList = new ArrayList<>();
        Integer[] categoryIconArray = {R.drawable.timeline, R.drawable.ic_past_journeys, R.drawable.ic_gallery,
                R.drawable.ic_profile};
        String[] hashMapKeys = {"icon", "title"};

        Integer len = getResources().getStringArray(R.array.sidemenu_features_list).length;
        for (int i = 0; i < len; i++) {
            HashMap<String, String> setting = new HashMap<>();
            setting.put("icon", "" + categoryIconArray[i]);
            setting.put("title", getResources().getStringArray(R.array.sidemenu_features_list)[i]);
            featuresList.add(setting);
        }

        SimpleAdapter adapter = new SimpleAdapter(getActivity(), featuresList,
                R.layout.side_menu_drawer_item, hashMapKeys, new int[]{R.id.side_menu_cloud_icon,
                R.id.side_menu_cloud_title});
        featuresListView.setAdapter(adapter);

        featuresListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // selected item
                Intent i;
                switch (position) {
                    case 0:
                        i = new Intent(getActivity(), ActivejourneyList.class);
                        startActivity(i);
                        break;
                    case 1:
                        i = new Intent(getActivity(), PastJourneyList.class);
                        startActivity(i);
                        break;
                    case 2:
                        i = new Intent(getActivity(), GalleryBaseActivity.class);
                        startActivity(i);
                        break;
                    case 3:
                        i = new Intent(getActivity(), ProfileActivity.class);
                        startActivityForResult(i, REQUEST_CODE_UPDATE_PROFILE);
                        break;

                    default:
                        break;
                }
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Settings.class);
                startActivity(intent);

            }
        });

    }

    @Override
    public void onResume() {
        Log.d(TAG, "fragment on resume method called");
        try {
            mProfileImg.setImageBitmap(BitmapFactory.decodeStream(new FileInputStream(TJPreferences.getProfileImgPath(getActivity())), null, new BitmapFactory.Options()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ;
        mUserName.setText(TJPreferences.getUserName(getActivity()));
        mUserStatus.setText(TJPreferences.getUserStatus(getActivity()));
        super.onResume();
    }
}
