package com.example.memories.currentjourney;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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

public class TimelineFragment extends Fragment {

    private static final String TAG = "<TimelineFragment>";
    public static TimeLineAdapter mAdapter;
    private ListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FloatingActionsMenu mFab;
    private FrameLayout baseActivityContentOverlay;
    private View rootView;
    private List<Memories> memoriesList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.timeline_list, container, false);
        Log.d(TAG, "onactivitycreated() method called from timeline");
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(TAG, "onactivitycreated() method called from timeline");
        /**
         * Call getActivity() function whenever you want to check user login getActivity() will
         * redirect user to LoginActivity is he is not logged in
         * */

        SessionManager session = new SessionManager(getActivity());
        session.checkLogin(getActivity());

        String j_id = TJPreferences.getActiveJourneyId(getActivity());
        Log.d(TAG, "Yes user is logged in.....");
        Log.d(TAG, "j_id = " + j_id);
        Log.d(TAG, "user_id = " + TJPreferences.getUserId(getActivity()));

        mListView = (ListView) rootView.findViewById(R.id.timelineList);
        memoriesList = MemoriesDataSource.getAllMemoriesList(getActivity(),
                TJPreferences.getActiveJourneyId(getActivity()));
        mAdapter = new TimeLineAdapter(getActivity(), memoriesList);
        mListView.setAdapter(mAdapter);

        // Swipe to refersh tmline
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.timeline_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                memoriesList = MemoriesDataSource.getAllMemoriesList(getActivity(),
                        TJPreferences.getActiveJourneyId(getActivity()));
                mAdapter.updateList(memoriesList);
                mAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

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
                baseActivityContentOverlay.setBackgroundColor(getResources().getColor(R.color.transparent));
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

        // Weather api testing

    }

    // Method overridden so that if a new activity is called and Fab menu is opened, it will be closed
    @Override
    public void onPause() {
        if (mFab.isExpanded()) {
            mFab.collapse();
        }
        super.onPause();
    }

    // Onresume of the fragment fetch all the memories from the database and
    // if adapter is null, create new else notifyDataSetChanged()
    public void onResume() {
        Log.d(TAG, "on resume() method called from timeline");
        memoriesList = MemoriesDataSource.getAllMemoriesList(getActivity(), TJPreferences.getActiveJourneyId(getActivity()));
        if (mAdapter == null) {
            mAdapter = new TimeLineAdapter(getActivity(), memoriesList);
            mListView.setAdapter(mAdapter);
        } else {
            mAdapter.setMemoriesList(memoriesList);
            mAdapter.notifyDataSetChanged();
        }
        super.onResume();
    }
}
