package com.traveljar.memories.currentjourney;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.flotingmenulibrary.FloatingActionsMenu;
import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.JourneyDataSource;
import com.traveljar.memories.SQLitedatabase.MemoriesDataSource;
import com.traveljar.memories.currentjourney.adapters.TimeLineAdapter;
import com.traveljar.memories.models.Journey;
import com.traveljar.memories.models.Memories;
import com.traveljar.memories.services.MakeServerRequestsService;
import com.traveljar.memories.utility.SessionManager;
import com.traveljar.memories.utility.TJPreferences;

import java.util.List;

public class TimelineFragment extends Fragment {

    private static final String TAG = "<TimelineFragment>";
    public TimeLineAdapter mAdapter;
    private ListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FloatingActionsMenu mFab;
    //private FrameLayout baseActivityContentOverlay;
    private View rootView;
    private List<Memories> memoriesList;
    private RelativeLayout mLayout;
    private Journey journey;

    private int visibleItemPosition;

    private static TimelineFragment instance;

    /*public TimelineFragment() {
        instance = this;
    }*/

    /*public static TimelineFragment getInstance() {
        return instance == null ? new TimelineFragment() : instance;
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.timeline_list, container, false);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Intent intent = new Intent(getActivity(), MakeServerRequestsService.class);
        getActivity().startService(intent);

        Log.d(TAG, "timeline fragment context is " + getActivity() + " " + getActivity().isDestroyed());

        mLayout = (RelativeLayout) rootView.findViewById(R.id.timeline_layout);

        SessionManager session = new SessionManager(getActivity());
        session.checkLogin(getActivity());

        //String j_id = TJPreferences.getActiveJourneyId(getActivity());
        journey = JourneyDataSource.getJourneyById(getActivity(), TJPreferences.getActiveJourneyId(getActivity()));
        Log.d(TAG, "Yes user is logged in.....");
        Log.d(TAG, "j_id = " + journey.getIdOnServer());
        Log.d(TAG, "user_id = " + TJPreferences.getUserId(getActivity()));

        mListView = (ListView) rootView.findViewById(R.id.timelineList);

        //loadMemoriesList();

        // Pull to refersh tmline
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.timeline_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                loadMemoriesList();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        // FAB ============================================
        // Configure floating action button
         mFab = (FloatingActionsMenu) rootView.findViewById(R.id.multiple_actions_down);
        Log.d(TAG, "user active" + journey.isUserActive());
        if(!journey.isUserActive()){
            mFab.setVisibility(View.GONE);
        }

        mFab.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {

            @Override
            public void onMenuExpanded() {
                Log.d(TAG, "FAB expanded");
                /*baseActivityContentOverlay.setBackgroundColor(getResources().getColor(
                        R.color.black_semi_transparent));*/
                getActivity().findViewById(R.id.content_activity_overlay).setBackgroundColor(getResources().getColor(R.color.black_semi_transparent));
                getActivity().findViewById(R.id.content_toolbar_overlay).setBackgroundColor(getResources().getColor(R.color.black_semi_transparent));
            }

            @Override
            public void onMenuCollapsed() {
                Log.d(TAG, "FAB collapsed");
                //baseActivityContentOverlay.setBackgroundColor(getResources().getColor(R.color.transparent));
                getActivity().findViewById(R.id.content_activity_overlay).setBackgroundColor(getResources().getColor(R.color.transparent));
                getActivity().findViewById(R.id.content_toolbar_overlay).setBackgroundColor(getResources().getColor(R.color.transparent));

            }
        });

        // Remove the overlay if clicked anywhere other than FAB buttons
        getActivity().findViewById(R.id.content_activity_overlay).setOnTouchListener(new View.OnTouchListener() {
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

        // Remove the overlay if clicked anywhere other than FAB buttons
        ((CurrentJourneyBaseActivity) getActivity()).findViewById(R.id.content_toolbar_overlay).setOnTouchListener(new View.OnTouchListener() {
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

    // Method overridden so that if a new activity is called and Fab menu is opened, it will be closed
    @Override
    public void onPause() {
        visibleItemPosition = mListView.getFirstVisiblePosition();
        if (mFab.isExpanded()) {
            mFab.collapse();
        }
        super.onPause();
    }

    public void loadMemoriesList() {
        memoriesList = MemoriesDataSource.getAllMemoriesList(getActivity(), TJPreferences.getActiveJourneyId(getActivity()));
        Log.d(TAG, "no of memories = " + memoriesList.size());
        if (memoriesList.size() > 0) {
            mListView.setVisibility(View.VISIBLE);
            mLayout.setBackgroundColor(getResources().getColor(R.color.white));
            if (mAdapter == null) {
                Log.d(TAG, "mAdapter is null");
                mAdapter = new TimeLineAdapter(getActivity(), memoriesList);
                mListView.setAdapter(mAdapter);
            } else {
                Log.d(TAG, "mAdapter is not null " + mListView.getVisibility() + View.VISIBLE);
                mAdapter.setMemoriesList(memoriesList);
                mAdapter.notifyDataSetChanged();
                mListView.setAdapter(mAdapter);
                mListView.setSelection(visibleItemPosition);
            }
        } else {
            Log.d(TAG, "no of memories < 0");
            mListView.setVisibility(View.GONE);
            mLayout.setBackgroundResource(R.drawable.img_no_timeline_item);
        }
    }

    // Onresume of the fragment fetch all the memories from the database and
    // if adapter is null, create new else notifyDataSetChanged()
    @Override
    public void onResume() {
        loadMemoriesList();
        Log.d(TAG, "on resume() method called from timeline");
        super.onResume();
    }
}


