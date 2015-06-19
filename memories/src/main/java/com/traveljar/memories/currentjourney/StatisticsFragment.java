package com.traveljar.memories.currentjourney;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.traveljar.memories.R;


/**
 * Created by abhi on 28/05/15.
 */
public class StatisticsFragment extends Fragment {

    private static final String TAG = "<StatisticsFragment>";
    private View rootView;
    Button mButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.current_journey_statistics, container, false);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mButton = (Button) rootView.findViewById(R.id.viewTimeCapsule);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TimecapsulePlayer.class);
                getActivity().startActivity(intent);
            }
        });
        Log.d(TAG, "5");
    }
}
