package com.example.memories;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.memories.activejourney.ActivejourneyList;
import com.example.memories.gallery.GalleryBaseActivity;
import com.example.memories.pastjourney.PastJourneyList;
import com.example.memories.profile.ProfileActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by abhi on 28/05/15.
 */
public class SideMenuDrawer extends Fragment {

    private static final String TAG = "<BaseActivity>";
    private static final int REQUEST_CODE_UPDATE_PROFILE = 2;
    private ArrayList<Map<String, String>> featuresList;
    private ListView featuresListView;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.side_menu_drawer, null);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Sliding Side Menu Drawer code
        featuresListView = (ListView) rootView.findViewById(R.id.sidemenu_features_list);
        featuresList = new ArrayList<Map<String, String>>();
        Integer[] categoryIconArray = {R.drawable.timeline, R.drawable.map32, R.drawable.stack21,
                R.drawable.cookies};
        String[] hashMapKeys = {"icon", "title"};

        Integer len = getResources().getStringArray(R.array.sidemenu_features_list).length;
        for (int i = 0; i < len; i++) {
            HashMap<String, String> setting = new HashMap<String, String>();
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

    }
}
