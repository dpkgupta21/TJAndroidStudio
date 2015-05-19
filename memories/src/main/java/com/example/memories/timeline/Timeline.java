package com.example.memories.timeline;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.memories.BaseActivity;
import com.example.memories.R;
import com.example.memories.SQLitedatabase.MemoriesDataSource;
import com.example.memories.models.Memories;
import com.example.memories.retrofit.StringConverter;
import com.example.memories.retrofit.TravelJarServices;
import com.example.memories.timeline.adapters.TimeLineAdapter;
import com.example.memories.utility.Constants;
import com.example.memories.utility.SessionManager;
import com.example.memories.utility.TJPreferences;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;

public class Timeline extends BaseActivity {

    private static final String TAG = "<Timeline>";
    public static TimeLineAdapter mAdapter;
    private SessionManager session;
    private String j_id;
    private ListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<Memories> memoriesList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline_list);

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

        RestAdapter restAdapter = new RestAdapter.Builder().setConverter(new StringConverter())
                .setEndpoint(Constants.TRAVELJAR_API_BASE_URL).build();
        TravelJarServices myService = restAdapter.create(TravelJarServices.class);

        myService.addPlace(TJPreferences.getApiKey(this), "Delhi",
                new Callback<String>() {
                    @Override
                    public void success(String str, retrofit.client.Response response) {
                        Log.d(TAG, "image uploaded successfully " + str);
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        Log.d(TAG, "error in uploading picture" + retrofitError);
                        retrofitError.printStackTrace();
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

    }
}
