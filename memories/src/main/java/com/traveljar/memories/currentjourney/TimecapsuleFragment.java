package com.traveljar.memories.currentjourney;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.TimecapsuleDataSource;
import com.traveljar.memories.currentjourney.adapters.TimecapsuleAdapter;
import com.traveljar.memories.models.Timecapsule;
import com.traveljar.memories.utility.Constants;
import com.traveljar.memories.utility.TJPreferences;
import com.traveljar.memories.volley.AppController;
import com.traveljar.memories.volley.CustomJsonRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        Log.d(TAG, "enetred gallery timecapsule fragment!!");

        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.current_journey_timecapsule_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        LinearLayout mLayout = (LinearLayout) rootView.findViewById(R.id.layout);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(rootView.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        List<Timecapsule> mTimecapsuleList = TimecapsuleDataSource.getAllTimecapsule(
                getActivity(), TJPreferences.getActiveJourneyId(getActivity()));

        // specify an adapter (see also next example)
        TimecapsuleAdapter mAdapter = new TimecapsuleAdapter((ArrayList<Timecapsule>) mTimecapsuleList, getActivity());
        mRecyclerView.setAdapter(mAdapter);

        Button button = (Button) rootView.findViewById(R.id.generate);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateTimecapsule();
            }
        });

    }


    public void generateTimecapsule() {
        String uploadRequestTag = "TIMECAPSULE_GENERATE";
        Map<String, String> params = new HashMap<>();

        // put the parameters here
        //params.put("note[updated_at]", String.valueOf(note.getUpdatedAt()));

        String url = Constants.URL_TIMECAPSULE_GENERATE + "?api_key=" + TJPreferences.getApiKey(getActivity())
                + "&j_id=" + TJPreferences.getActiveJourneyId(getActivity());
        Log.d(TAG, url);
        CustomJsonRequest uploadRequest = new CustomJsonRequest(Request.Method.GET, url, params,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "response from server = " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "error in generating timecapsule video" + error);
            }
        });
        AppController.getInstance().addToRequestQueue(uploadRequest, uploadRequestTag);
    }
}
