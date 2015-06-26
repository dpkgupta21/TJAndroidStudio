package com.traveljar.memories.currentjourney;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.TimecapsuleDataSource;
import com.traveljar.memories.currentjourney.adapters.TimecapsuleAdapter;
import com.traveljar.memories.models.Timecapsule;
import com.traveljar.memories.utility.TJPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abhi on 26/06/15.
 */
public class TimecapsuleFragment extends Fragment {

    private static final String TAG = "<TimecapsuleFragment>";
    private View rootView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.current_journey_timecapsule_list, container, false);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(TAG, "enetred gallery photos fragment!!");

        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.current_journey_timecapsule_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        RelativeLayout mLayout = (RelativeLayout) rootView.findViewById(R.id.layout);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(rootView.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        List<Timecapsule> mTimecapsuleList = TimecapsuleDataSource.getAllTimecapsule(
                getActivity(), TJPreferences.getActiveJourneyId(getActivity()));

        // specify an adapter (see also next example)
        TimecapsuleAdapter mAdapter = new TimecapsuleAdapter((ArrayList) mTimecapsuleList);

    }
}
