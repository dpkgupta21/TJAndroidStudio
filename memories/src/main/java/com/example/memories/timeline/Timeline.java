package com.example.memories.timeline;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.flotingmenulibrary.FloatingActionsMenu;
import com.example.memories.BaseActivity;
import com.example.memories.R;
import com.example.memories.SQLitedatabase.MemoriesDataSource;
import com.example.memories.audio.CaptureAudio;
import com.example.memories.checkin.CheckInPlacesList;
import com.example.memories.models.Memories;
import com.example.memories.moods.CaptureMoods;
import com.example.memories.note.CreateNotes;
import com.example.memories.picture.CapturePhotos;
import com.example.memories.timeline.adapters.TimeLineAdapter;
import com.example.memories.utility.SessionManager;
import com.example.memories.utility.TJPreferences;
import com.example.memories.video.CaptureVideo;

import java.util.List;

public class Timeline extends BaseActivity {

    private static final String TAG = "<Timeline>";
    public static TimeLineAdapter mAdapter;
    private SessionManager session;
    private String j_id;
    private ListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<Memories> memoriesList;

    private boolean backPressedToExitOnce = false;
    private Toast toast = null;

    private FloatingActionsMenu mFab;
    private FrameLayout baseActivityContentOverlay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline_list);
        Log.d(TAG, "5");

        /**
         * Call this function whenever you want to check user login This will
         * redirect user to LoginActivity is he is not logged in
         * */

        session = new SessionManager(getApplicationContext());
        session.checkLogin(this);
        j_id = TJPreferences.getActiveJourneyId(this);
        Log.d(TAG, "Yes user is logged in.....");
        Log.d(TAG, "j_id = " + j_id);
        Log.d(TAG, "user_id = " + TJPreferences.getUserId(getBaseContext()));

        mListView = (ListView) findViewById(R.id.timelineList);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.timeline_swipe_refresh_layout);

        memoriesList = MemoriesDataSource.getAllMemoriesList(this,
                TJPreferences.getActiveJourneyId(this));
        mAdapter = new TimeLineAdapter(this, memoriesList);

        Log.d(TAG, "Time line activity started" + TJPreferences.getActiveJourneyId(this));

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // selected item
                Log.d(TAG, "position = " + position + ", and id = " + id + " on view = " + view);
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // TODO Auto-generated method stub
                memoriesList = MemoriesDataSource.getAllMemoriesList(getApplicationContext(),
                        TJPreferences.getActiveJourneyId(getApplicationContext()));
                mAdapter = new TimeLineAdapter(getApplicationContext(), memoriesList);
                mListView.setAdapter(mAdapter);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        j_id = TJPreferences.getActiveJourneyId(this);

        // FAB ============================================
        // Configure floating action button
        mFab = (FloatingActionsMenu) findViewById(R.id.multiple_actions_down);
        baseActivityContentOverlay = (FrameLayout) findViewById(R.id.content_activity_overlay);

        mFab.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {

            @Override
            public void onMenuExpanded() {
                Log.d(TAG, "FAB expanded");
                baseActivityContentOverlay.setBackgroundColor(getResources().getColor(
                        R.color.black_semi_transparent));

            }

            @Override
            public void onMenuCollapsed() {
                Log.d(TAG, "FAB collapsed");
                baseActivityContentOverlay.setBackgroundColor(getResources().getColor(
                        R.color.transparent));

            }
        });

        // Remove the overlay if clicked anywhere other than FAB buttons
        baseActivityContentOverlay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // ignore all touch events
                if (mFab.isExpanded()) {
                    mFab.collapse();
                    return true;
                }
                return false;
            }
        });

    }

    // On clicking on options in FAB button
    // Take them to their respective modules/screens
    public void onFABClick(View v) {
        // TODO Auto-generated method stub
        Intent i;
        if (mFab.isExpanded()) {
            mFab.collapse();
        }
        switch (v.getId()) {
            case R.id.button_mood:
                Log.d(TAG, "set a mood clicked");
                i = new Intent(getApplicationContext(), CaptureMoods.class);
                startActivity(i);
                break;
            case R.id.button_checkin:
                Log.d(TAG, "checkin clicked");
                i = new Intent(getApplicationContext(), CheckInPlacesList.class);
                startActivity(i);
                break;
            case R.id.button_photo:
                Log.d(TAG, "photo clicked");
                i = new Intent(getApplicationContext(), CapturePhotos.class);
                startActivity(i);
                break;
            case R.id.button_note:
                i = new Intent(this, CreateNotes.class);
                startActivity(i);
                Log.d(TAG, "note clicked");
                break;
            case R.id.button_video:
                Log.d(TAG, "video clicked");
                i = new Intent(getApplicationContext(), CaptureVideo.class);
                startActivity(i);
                break;
            case R.id.button_audio:
                Log.d(TAG, "audio clicked");
                i = new Intent(getApplicationContext(), CaptureAudio.class);
                startActivity(i);
                break;
        }

    }

    @Override
    public void onBackPressed() {
        if (backPressedToExitOnce) {
            super.onBackPressed();
        } else {
            this.backPressedToExitOnce = true;
            showToast("Press again to exit");
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    backPressedToExitOnce = false;
                }
            }, 2000);
        }
    }

    private void showToast(String message) {
        if (this.toast == null) {
            // Create toast if found null, it would he the case of first call only
            this.toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);

        } else if (this.toast.getView() == null) {
            // Toast not showing, so create new one
            this.toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);

        } else {
            // Updating toast message is showing
            this.toast.setText(message);
        }

        // Showing toast finally
        this.toast.show();
    }

    @Override
    protected void onPause() {
        if (this.toast != null) {
            this.toast.cancel();
        }
        super.onPause();
    }

}
