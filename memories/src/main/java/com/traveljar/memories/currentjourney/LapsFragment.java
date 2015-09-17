package com.traveljar.memories.currentjourney;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.LapsDataSource;
import com.traveljar.memories.currentjourney.adapters.LapsAdapter;
import com.traveljar.memories.models.Lap;
import com.traveljar.memories.models.Laps;
import com.traveljar.memories.newjourney.AddLap;
import com.traveljar.memories.utility.TJPreferences;

import java.util.List;

public class LapsFragment extends Fragment {

    private static final String TAG = "<LapsFragment>";
    private View rootView;
    private ListView lvLaps;
    private List<Laps> lapsList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.current_journey_lapslist, container, false);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        lvLaps = (ListView) rootView.findViewById(R.id.current_laps_list);

        FloatingActionButton btnFab = (FloatingActionButton) rootView.findViewById(R.id.btn_add_lap);
        btnFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), AddLap.class);
                i.putExtra("isJourneyCreated", true);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
    }

    @Override
    public void onResume() {
        String jId = TJPreferences.getActiveJourneyId(getActivity());
        lapsList = LapsDataSource.getLapsFromJourneyWithPlace(getActivity(), jId);
        LapsAdapter adapter = new LapsAdapter(getActivity(), lapsList);
        lvLaps.setAdapter(adapter);
        super.onResume();
    }
}
