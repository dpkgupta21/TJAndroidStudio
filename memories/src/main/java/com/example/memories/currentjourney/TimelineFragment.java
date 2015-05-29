package com.example.memories.currentjourney;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.example.flotingmenulibrary.FloatingActionsMenu;
import com.example.memories.R;
import com.example.memories.SQLitedatabase.MemoriesDataSource;
import com.example.memories.currentjourney.adapters.TimeLineAdapter;
import com.example.memories.models.Memories;
import com.example.memories.utility.SessionManager;
import com.example.memories.utility.TJPreferences;

import java.util.List;

public class TimelineFragment extends Fragment{

    private static final String TAG = "<TimelineFragment>";
    public static TimeLineAdapter mAdapter;
    private SessionManager session;
    private String j_id;
    private ListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<Memories> memoriesList;


    private FloatingActionsMenu mFab;
    private FrameLayout baseActivityContentOverlay;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.timeline_list, container, false);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(TAG, "5");

        /**
         * Call getActivity() function whenever you want to check user login getActivity() will
         * redirect user to LoginActivity is he is not logged in
         * */

        session = new SessionManager(getActivity());
        session.checkLogin(getActivity());
        j_id = TJPreferences.getActiveJourneyId(getActivity());
        Log.d(TAG, "Yes user is logged in.....");
        Log.d(TAG, "j_id = " + j_id);
        Log.d(TAG, "user_id = " + TJPreferences.getUserId(getActivity()));

        mListView = (ListView) rootView.findViewById(R.id.timelineList);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.timeline_swipe_refresh_layout);

        memoriesList = MemoriesDataSource.getAllMemoriesList(getActivity(),
                TJPreferences.getActiveJourneyId(getActivity()));
        mAdapter = new TimeLineAdapter(getActivity(), memoriesList);

        Log.d(TAG, "Time line activity started" + TJPreferences.getActiveJourneyId(getActivity()));

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
                memoriesList = MemoriesDataSource.getAllMemoriesList(getActivity(),
                        TJPreferences.getActiveJourneyId(getActivity()));
                mAdapter = new TimeLineAdapter(getActivity(), memoriesList);
                mListView.setAdapter(mAdapter);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        j_id = TJPreferences.getActiveJourneyId(getActivity());

        // FAB ============================================
        // Configure floating action button
        mFab = (FloatingActionsMenu) rootView.findViewById(R.id.multiple_actions_down);
        baseActivityContentOverlay = (FrameLayout) rootView.findViewById(R.id.content_activity_overlay);

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

/*    public void onFABClick(View v) {
        // TODO Auto-generated method stub
        Intent i;
        if (mFab.isExpanded()) {
            mFab.collapse();
        }
        switch (v.getId()) {
            case R.id.button_mood:
                Log.d(TAG, "set a mood clicked");
                i = new Intent(getActivity(), MoodCapture.class);
                startActivity(i);
                break;
            case R.id.button_checkin:
                Log.d(TAG, "checkin clicked");
                i = new Intent(getActivity(), CheckInPlacesList.class);
                startActivity(i);
                break;
            case R.id.button_photo:
                Log.d(TAG, "photo clicked");
                i = new Intent(getActivity(), PictureCapture.class);
                startActivity(i);
                break;
            case R.id.button_note:
                i = new Intent(getActivity(), CreateNotes.class);
                startActivity(i);
                Log.d(TAG, "note clicked");
                break;
            case R.id.button_video:
                Log.d(TAG, "video clicked");
                i = new Intent(getActivity(), VideoCapture.class);
                startActivity(i);
                break;
            case R.id.button_audio:
                Log.d(TAG, "audio clicked");
                i = new Intent(getActivity(), AudioCapture.class);
                startActivity(i);
                break;
        }*/

    //}
/*
    @Override
    public void onClick(View v) {
        onFABClick(v);
    }*/
}
