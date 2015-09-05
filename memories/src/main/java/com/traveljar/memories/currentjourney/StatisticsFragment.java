package com.traveljar.memories.currentjourney;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.AudioDataSource;
import com.traveljar.memories.SQLitedatabase.JourneyDataSource;
import com.traveljar.memories.SQLitedatabase.MoodDataSource;
import com.traveljar.memories.SQLitedatabase.NoteDataSource;
import com.traveljar.memories.SQLitedatabase.PictureDataSource;
import com.traveljar.memories.SQLitedatabase.VideoDataSource;
import com.traveljar.memories.currentjourney.adapters.StatisticsAdapter;
import com.traveljar.memories.utility.TJPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsFragment extends Fragment {

    private static final String TAG = "<StatisticsFragment>";
    private View rootView;
    GridView gridView;
    List<Map<String, String>> statistics;
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

        gridView = (GridView) rootView.findViewById(R.id.statistics_grid);
        populateStatistics();

        StatisticsAdapter adapter = new StatisticsAdapter(getActivity(), statistics);
        gridView.setAdapter(adapter);


/*        // Get all views from XML
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
        Log.d(TAG, "5");*/
    }

    private void populateStatistics(){
        statistics = new ArrayList<>();
        String jId = TJPreferences.getActiveJourneyId(getActivity());
        statistics.add(getStatMap(JourneyDataSource.getJourneyById(getActivity(), jId).getBuddies().size(), "Buddies",
                R.drawable.ic_info_black_24dp));
        statistics.add(getStatMap(0, "Km travelled", R.drawable.ic_info_black_24dp));
        statistics.add(getStatMap(PictureDataSource.getPicCountOfJourney(getActivity(), jId), "Pictures",
                R.drawable.ic_info_black_24dp));
      /*  statistics.add(getStatMap(NoteDataSource.getNoteCountOfJourney(getActivity(), jId), "Notes",
                R.drawable.ic_info_black_24dp));*/
        statistics.add(getStatMap(VideoDataSource.getVideoCountOfJourney(getActivity(), jId), "Videos",
                R.drawable.ic_info_black_24dp));
       /* statistics.add(getStatMap(AudioDataSource.getAudioCountOfJourney(getActivity(), jId), "Audios",
                R.drawable.ic_info_black_24dp));*/
        statistics.add(getStatMap(MoodDataSource.getMoodCountOfJourney(getActivity(), jId), "Moods",
                R.drawable.ic_info_black_24dp));
        statistics.add(getStatMap(MoodDataSource.getMoodCountOfJourney(getActivity(), jId), "Checkins",
                R.drawable.ic_info_black_24dp));
    }

    private Map<String, String> getStatMap(int number, String title, int resourceId){
        Map<String, String> stat  = new HashMap<>();
        stat.put("count", String.valueOf(number));
        stat.put("title", title);
        stat.put("resource_id", String.valueOf(resourceId));
        return stat;
    }

/*    @Override
    public void onResume(){
        Log.d(TAG, "on resume method called");
        updateStats();
        super.onResume();
    }*/

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
