package com.traveljar.memories.gallery;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.AudioDataSource;
import com.traveljar.memories.SQLitedatabase.JourneyDataSource;
import com.traveljar.memories.gallery.adapters.AudioGalleryAdapter;
import com.traveljar.memories.models.Memories;

import java.util.List;

public class GalleryAudios extends AppCompatActivity {

    private static final String TAG = "GalleryAudio";
    private List<Memories> mAudioList;
    private AudioGalleryAdapter mAdapter;
    private ListView mListView;
    private LinearLayout mLayout;
    private String journeyId;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_audios);

        journeyId = getIntent().getStringExtra("JOURNEY_ID");
        setUpToolBar();

        mListView = (ListView) findViewById(R.id.gallery_audio_listview);
        mLayout = (LinearLayout)findViewById(R.id.gallery_audio_layout);

        mAudioList = AudioDataSource.getAudioMemoriesForJourney(this, journeyId);
        if(mAudioList.size() == 0){
            mLayout.setBackgroundResource(R.drawable.img_no_audio);
        }else {
            mAdapter = new AudioGalleryAdapter(this, mAudioList);
            mListView.setAdapter(mAdapter);
            mLayout.setBackgroundColor(getResources().getColor(R.color.white));
        }
        Log.d(TAG, "audio list size " + mAudioList.size());

    }

    private void setUpToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        title.setText(JourneyDataSource.getJourneyById(this, journeyId).getName());
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
