package com.traveljar.memories;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.traveljar.memories.activejourney.ActivejourneyList;
import com.traveljar.memories.gallery.GalleryBaseActivity;
import com.traveljar.memories.pastjourney.PastJourneyList;
import com.traveljar.memories.profile.ProfileActivity;
import com.traveljar.memories.settings.Settings;
import com.traveljar.memories.utility.TJPreferences;

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

    private DrawerLayout mDrawerLayout;

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

        mDrawerLayout = (DrawerLayout)getActivity().findViewById(R.id.drawerLayout);

        // Sliding Side Menu Drawer code
        ListView featuresListView = (ListView) rootView.findViewById(R.id.sidemenu_features_list);
        ArrayList<Map<String, String>> featuresList = new ArrayList<>();
        Integer[] categoryIconArray = {R.drawable.timeline, R.drawable.ic_past_journeys, R.drawable.ic_gallery,
                R.drawable.add70};
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
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        i = new Intent(getActivity(), ActivejourneyList.class);
                        startActivity(i);
                        break;
                    case 1:
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        i = new Intent(getActivity(), PastJourneyList.class);
                        startActivity(i);
                        break;
                    case 2:
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        i = new Intent(getActivity(), GalleryBaseActivity.class);
                        startActivity(i);
                        break;
                    case 3:
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        i = new Intent(getActivity(), ProfileActivity.class);
                        startActivity(i);
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

        mProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
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
