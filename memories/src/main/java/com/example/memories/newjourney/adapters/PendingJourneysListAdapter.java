package com.example.memories.newjourney.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.memories.R;
import com.example.memories.SQLitedatabase.AudioDataSource;
import com.example.memories.SQLitedatabase.CheckinDataSource;
import com.example.memories.SQLitedatabase.ContactDataSource;
import com.example.memories.SQLitedatabase.MoodDataSource;
import com.example.memories.SQLitedatabase.MySQLiteHelper;
import com.example.memories.SQLitedatabase.NoteDataSource;
import com.example.memories.models.Audio;
import com.example.memories.models.CheckIn;
import com.example.memories.models.Contact;
import com.example.memories.models.Journey;
import com.example.memories.models.Mood;
import com.example.memories.models.Note;
import com.example.memories.models.Picture;
import com.example.memories.newjourney.NewJourney;
import com.example.memories.services.PullBuddiesService;
import com.example.memories.timeline.Timeline;
import com.example.memories.utility.AudioUtil;
import com.example.memories.utility.Constants;
import com.example.memories.utility.HelpMe;
import com.example.memories.utility.PictureUtilities;
import com.example.memories.utility.TJPreferences;
import com.example.memories.volley.AppController;
import com.example.memories.volley.CustomJsonRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ankit on 19/5/15.
 */
public class PendingJourneysListAdapter extends BaseAdapter {

    private static final String TAG = "PENDING_JOURNEY_ADAPTER";
    static Context mContext;
    List<Journey> mJourneyList;
    public static int requestCount = 0;
    public static boolean allRequestsFinished;

    public PendingJourneysListAdapter(Context context, List<Journey> journeyList){
        mContext = context;
        mJourneyList = journeyList;
    }

    @Override
    public int getCount() {
        return mJourneyList.size();
    }

    @Override
    public Object getItem(int position) {
        return mJourneyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.pending_request_list_item, null);
        }
        final Journey journey = mJourneyList.get(position);
//        Contact createdBy = ContactDataSource.getContactById(mContext, journey.getCreatedBy());
//        ((TextView) convertView.findViewById(R.id.journeyCreatedByTxt)).setText(createdBy.getName());
        ((TextView) convertView.findViewById(R.id.journeyCreatedByTxt)).setText("Ankit Aggarwal");
        ((TextView) convertView.findViewById(R.id.journeyNameTxt)).setText(journey.getName());
        //((TextView) convertView.findViewById(R.id.journeyCreatedByTxt)).setText(createdBy.getName());

        convertView.findViewById(R.id.joinJourneyBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TJPreferences.setActiveJourneyId(mContext, journey.getIdOnServer());

                // Fetch all those contacts which are not in the contacts list of current user but are on the journey
                ArrayList<String> buddyList = (ArrayList<String>) ContactDataSource.getNonExistingContacts(mContext, journey.getBuddies());
                Log.d(TAG, "Total contacts " + buddyList.toString());
                if (buddyList.size() > 0) {
                    Intent intent = new Intent(mContext, PullBuddiesService.class);
                    intent.putStringArrayListExtra("BUDDY_IDS", buddyList);
                    mContext.startService(intent);
                }

                //Fetch all the memories
                String fetchMemoriesUrl = "https://www.traveljar.in/api/v1/journeys/" + journey.getIdOnServer() + "/memories?api_key=" + TJPreferences.getApiKey(mContext);
                CustomJsonRequest fetchMemoriesRequest = new CustomJsonRequest(Request.Method.GET, fetchMemoriesUrl, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Log.d(TAG, "=====" + response.getJSONArray("memories"));
                                    JSONArray responseArray = response.getJSONArray("memories");
                                    int i;
                                    JSONObject memory;
                                    String memoryType;
                                    String createdBy;
                                    String memoryId;
                                    String journeyId = journey.getIdOnServer();
                                    String fileUrl;
                                    String thumbnailUrl;
                                    String memType;
                                    String likedBy;
                                    String fileSize;
                                    Picture pic;


                                    Long createdAt = HelpMe.getCurrentTime();
                                    Long updatedAt = HelpMe.getCurrentTime();

                                    for(i = 0; i < responseArray.length(); i++){

                                        memory = responseArray.getJSONObject(i);
                                        memoryType = memory.getString("type");
                                        createdBy = memory.getJSONObject("user").getString("id");
                                        memoryId = memory.getJSONObject("memory").getString("id");

                                        if(memoryType.equals("Picture")){
                                            requestCount++;
                                            Log.d(TAG, "request count " + requestCount);
                                            fileUrl = memory.getJSONObject("memory").getJSONObject("picture_file").getString("url");
                                            thumbnailUrl = memory.getJSONObject("memory").getJSONObject("picture_file").getJSONObject("medium").getString("url");
                                            Picture newPic = new Picture(memoryId, journeyId, HelpMe.PICTURE_TYPE, null, "jpg",
                                                    100, fileUrl, null, createdBy, createdAt, updatedAt, null, null);
                                            PictureUtilities.createNewPicFromServer(mContext, newPic, thumbnailUrl);
                                        }else if(memoryType.equals("Note")){
                                            String content = memory.getJSONObject("memory").getString("note");
                                            String caption = null;//memory.getJSONObject("memory").getString("caption");
                                            Note newNote = new Note(memoryId, journeyId, HelpMe.NOTE_TYPE, caption, content, createdBy,
                                                    createdAt, updatedAt, null);

                                            NoteDataSource.createNote(newNote, mContext);

                                        }else if(memoryType.equals("Video")){
                                            /*memType = HelpMe.VIDEO_TYPE;
                                            fileUrl = memory.getJSONObject("memory").getJSONObject("video_file").getString("url");*/


                                        }else if(memoryType.equals("Checkin")){
                                            Log.d(TAG, "memory type is checkin");
                                            String lat = memory.getJSONObject("memory").getString("latitude");
                                            String lon = memory.getJSONObject("memory").getString("longitude");

                                            Double latitude = (lat == "null" ? 0.0 : Double.parseDouble(lat));
                                            Log.d(TAG, "latitude is " + latitude);
                                            Double longitude = (lon == "null" ? 0.0 : Double.parseDouble(lon));
                                            String placeName = memory.getJSONObject("memory").getString("place_name");
                                            String note = memory.getJSONObject("memory").getString("note");
                                            String buddys = memory.getJSONObject("memory").getString("buddies");
                                            List<String> buddyIds = buddys == null ? null : Arrays.asList(buddys.split(","));
                                            CheckIn newCheckIn = new CheckIn(memoryId, journeyId, HelpMe.CHECKIN_TYPE, note, latitude, longitude, placeName, null, buddyIds, createdBy,
                                                    createdAt, updatedAt);
                                            CheckinDataSource.createCheckIn(newCheckIn, mContext);

                                        }else if(memoryType.equals("Mood")){
                                            String mood = memory.getJSONObject("memory").getString("mood");
                                            String reason = memory.getJSONObject("memory").getString("reason");
                                            String buddys = memory.getJSONObject("memory").getString("buddies");
                                            List<String> buddyIds = buddys == null ? null : Arrays.asList(buddys.split(","));
                                            Mood newMood = new Mood(memoryId, journeyId, HelpMe.MOOD_TYPE, buddyIds, mood, reason,
                                                    createdBy, createdAt, updatedAt, null);
                                            MoodDataSource.createMood(newMood, mContext);

                                        }else if(memoryType.equals("Audio")){

                                            fileUrl = memory.getJSONObject("memory").getJSONObject("audio_file").getString("url");
                                            //Long size = Long.parseLong(memory.getJSONObject("memory").getJSONObject("audio_file").getString("size"));

                                            Audio newAudio = new Audio(memoryId, journeyId, HelpMe.AUDIO_TYPE, "3gp", 1122,
                                                    fileUrl, null, createdBy, createdAt, updatedAt, null);
                                            AudioDataSource.createAudio(newAudio, mContext);
                                        }
                                    }
                                    allRequestsFinished = true;
                                    PendingJourneysListAdapter.areAllRequestsFinished();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "That din't work!");
                    }
                });
                AppController.getInstance().getRequestQueue().add(fetchMemoriesRequest);
                /*Intent intent = new Intent(mContext, Timeline.class);
                mContext.startActivity(intent);*/
            }
        });
        return convertView;
    }

    public static void areAllRequestsFinished(){
        if(allRequestsFinished && requestCount == 0){
            Intent intent = new Intent(mContext, Timeline.class);
            mContext.startActivity(intent);
        }
    }

}
