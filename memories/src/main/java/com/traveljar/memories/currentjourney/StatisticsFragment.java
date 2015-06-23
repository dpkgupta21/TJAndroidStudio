package com.traveljar.memories.currentjourney;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.AudioDataSource;
import com.traveljar.memories.SQLitedatabase.JourneyDataSource;
import com.traveljar.memories.SQLitedatabase.MoodDataSource;
import com.traveljar.memories.SQLitedatabase.NoteDataSource;
import com.traveljar.memories.SQLitedatabase.PictureDataSource;
import com.traveljar.memories.SQLitedatabase.VideoDataSource;
import com.traveljar.memories.utility.TJPreferences;


/**
 * Created by abhi on 28/05/15.
 */
public class StatisticsFragment extends Fragment {

    private static final String TAG = "<StatisticsFragment>";
    private View rootView;
    Button mButton;
    private TextView buddyCountTxtView;
    private TextView picCountTxtView;
    private TextView noteCountTxtView;
    private TextView videoCountTxtView;
    private TextView audioCountTxtView;
    private TextView moodCountTxtView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.current_journey_statistics, container, false);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get all views from XML
        buddyCountTxtView = (TextView) rootView.findViewById(R.id.current_journey_stats_buddies_number);
        picCountTxtView = (TextView) rootView.findViewById(R.id.current_journey_stats_photos_number);
        noteCountTxtView = (TextView) rootView.findViewById(R.id.current_journey_stats_notes_number);
        videoCountTxtView = (TextView) rootView.findViewById(R.id.current_journey_stats_videos_number);
        audioCountTxtView = (TextView) rootView.findViewById(R.id.current_journey_stats_audios_number);
        moodCountTxtView = (TextView) rootView.findViewById(R.id.current_journey_stats_moods_number);

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

    @Override
    public void onResume(){
        Log.d(TAG, "on resume method called");
        updateStats();
        super.onResume();
    }

    // It collects counts of different memories and stats and updated on UI
    private void updateStats(){
        // Fetch count for all statistics
        String jId = TJPreferences.getActiveJourneyId(getActivity());
        int buddyCount = JourneyDataSource.getJourneyById(getActivity(), jId).getBuddies().size();
        int picCount = PictureDataSource.getPicCountOfJourney(getActivity(), jId);
        int noteCount = NoteDataSource.getNoteCountOfJourney(getActivity(), jId);
        int videoCount = VideoDataSource.getVideoCountOfJourney(getActivity(), jId);
        int audioCount = AudioDataSource.getAudioCountOfJourney(getActivity(), jId);
        int moodCount = MoodDataSource.getMoodCountOfJourney(getActivity(), jId);

        // Set all stats accordingly
        buddyCountTxtView.setText(String.valueOf(buddyCount+1));
        picCountTxtView.setText(String.valueOf(picCount));
        noteCountTxtView.setText(String.valueOf(noteCount));
        videoCountTxtView.setText(String.valueOf(videoCount));
        audioCountTxtView.setText(String.valueOf(audioCount));
        moodCountTxtView.setText(String.valueOf(moodCount));

    }
}
