package com.traveljar.memories.currentjourney;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.traveljar.memories.R;

/**
 * Created by abhi on 26/06/15.
 */
public class TimecapsuleFragment extends Fragment {

    private static final String TAG = "<TimecapsuleFragment>";
    private View rootView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.current_journey_statistics, container, false);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }
}
