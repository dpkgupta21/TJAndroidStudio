package com.example.memories.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.memories.SQLitedatabase.AudioDataSource;
import com.example.memories.SQLitedatabase.CheckinDataSource;
import com.example.memories.SQLitedatabase.JourneyDataSource;
import com.example.memories.SQLitedatabase.MoodDataSource;
import com.example.memories.SQLitedatabase.NoteDataSource;
import com.example.memories.models.Audio;
import com.example.memories.models.CheckIn;
import com.example.memories.models.Journey;
import com.example.memories.models.Mood;
import com.example.memories.models.Note;
import com.example.memories.models.Picture;
import com.example.memories.utility.HelpMe;
import com.example.memories.utility.PictureUtilities;
import com.example.memories.utility.TJPreferences;
import com.example.memories.volley.AppController;
import com.example.memories.volley.CustomJsonRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * Created by ankit on 22/5/15.
 */
public class PullMemoriesService extends IntentService {
    private ResultReceiver mReceiver;
    private int REQUEST_CODE;
    private Journey journey;
    private String journeyId;
    private static final String TAG = "<PullMemoriesService>";

    public PullMemoriesService() {
        super("PullMemoriesService");
    }

    public PullMemoriesService(String name) {
        super(name);
    }

    public int onStartCommand(Intent intent, int flags, int startId){
        mReceiver = intent.getParcelableExtra("RECEIVER");
        REQUEST_CODE = intent.getIntExtra("REQUEST_CODE", 0);
        journeyId = intent.getStringExtra("JOURNEY_ID");
        super.onStartCommand(intent, startId, startId);
        Log.d(TAG, "on start command");
        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        Log.d(TAG, "on Handle Intent");
        Bundle bundle = new Bundle();
        mReceiver.send(REQUEST_CODE, bundle);
    }

    private void fetchMemories(){
        journey = JourneyDataSource.getJourneyById(this, journeyId);
        String fetchMemoriesUrl = "https://www.traveljar.in/api/v1/journeys/" + journey.getIdOnServer() + "/memories?api_key=" + TJPreferences.getApiKey(this);
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
                                    fileUrl = memory.getJSONObject("memory").getJSONObject("picture_file").getString("url");
                                    thumbnailUrl = memory.getJSONObject("memory").getJSONObject("picture_file").getJSONObject("medium").getString("url");
                                    Picture newPic = new Picture(memoryId, journeyId, HelpMe.PICTURE_TYPE, null, "jpg",
                                            100, fileUrl, null, createdBy, createdAt, updatedAt, null, null);
                                    PictureUtilities.createNewPicFromServer(PullMemoriesService.this, newPic, thumbnailUrl);
                                }else if(memoryType.equals("Note")){
                                    String content = memory.getJSONObject("memory").getString("note");
                                    String caption = null;//memory.getJSONObject("memory").getString("caption");
                                    Note newNote = new Note(memoryId, journeyId, HelpMe.NOTE_TYPE, caption, content, createdBy,
                                            createdAt, updatedAt, null);

                                    NoteDataSource.createNote(newNote, PullMemoriesService.this);

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
                                    CheckinDataSource.createCheckIn(newCheckIn, PullMemoriesService.this);

                                }else if(memoryType.equals("Mood")){
                                    String mood = memory.getJSONObject("memory").getString("mood");
                                    String reason = memory.getJSONObject("memory").getString("reason");
                                    String buddys = memory.getJSONObject("memory").getString("buddies");
                                    List<String> buddyIds = buddys == null ? null : Arrays.asList(buddys.split(","));
                                    Mood newMood = new Mood(memoryId, journeyId, HelpMe.MOOD_TYPE, buddyIds, mood, reason,
                                            createdBy, createdAt, updatedAt, null);
                                    MoodDataSource.createMood(newMood, PullMemoriesService.this);

                                }else if(memoryType.equals("Audio")){

                                    fileUrl = memory.getJSONObject("memory").getJSONObject("audio_file").getString("url");
                                    //Long size = Long.parseLong(memory.getJSONObject("memory").getJSONObject("audio_file").getString("size"));

                                    Audio newAudio = new Audio(memoryId, journeyId, HelpMe.AUDIO_TYPE, "3gp", 1122,
                                            fileUrl, null, createdBy, createdAt, updatedAt, null);
                                    AudioDataSource.createAudio(newAudio, PullMemoriesService.this);
                                }
                            }
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
    }

    @Override
    public void onDestroy(){
        Log.d(TAG, "pull memories service onDestroy()");
        Bundle bundle = new Bundle();
        mReceiver.send(REQUEST_CODE, bundle);
    }
}
