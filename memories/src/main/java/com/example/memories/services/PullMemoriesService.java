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
import com.example.memories.SQLitedatabase.MoodDataSource;
import com.example.memories.SQLitedatabase.NoteDataSource;
import com.example.memories.models.Audio;
import com.example.memories.models.CheckIn;
import com.example.memories.models.Journey;
import com.example.memories.models.Mood;
import com.example.memories.models.Note;
import com.example.memories.models.Picture;
import com.example.memories.models.Video;
import com.example.memories.utility.Constants;
import com.example.memories.utility.HelpMe;
import com.example.memories.utility.PictureUtilities;
import com.example.memories.utility.TJPreferences;
import com.example.memories.utility.VideoUtil;
import com.example.memories.volley.AppController;
import com.example.memories.volley.CustomJsonRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ankit on 22/5/15.
 */
public class PullMemoriesService extends IntentService {
    private static final String TAG = "PullMemoriesService";
    private ResultReceiver mReceiver;
    private int REQUEST_CODE;
    private Journey journey;

    public PullMemoriesService() {
        super("PullMemoriesService");
    }

    public PullMemoriesService(String name) {
        super(name);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "on start command");
        mReceiver = intent.getParcelableExtra("RECEIVER");
        REQUEST_CODE = intent.getIntExtra("REQUEST_CODE", 0);
        super.onStartCommand(intent, startId, startId);
        Log.d(TAG, "on start command");
        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        Log.d(TAG, "on Handle Intent");
        fetchJourneys();
    }

    private void fetchJourneys() {
        Log.d(TAG, "fetch journeys");
        String fetchJourneysUrl = Constants.URL_TJ_DOMAIN + "api/v1/users/journeys?api_key=" + TJPreferences.getApiKey(this) + "&user_id=" + TJPreferences.getUserId(this);
        CustomJsonRequest fetchJourneysRequest = new CustomJsonRequest(Request.Method.GET, fetchJourneysUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(TAG, "=====" + response.getJSONArray("users"));
//                            /JSONArray jsonArray = response.getJSONArray("journeys");
                            JSONObject jsonObject;
                            int length = response.getJSONArray("users").length();
                            String idOnServer;
                            String name;
                            String tagLine;
                            String createdBy;
                            String laps;
                            List<String> lapsList;
                            String buddies;
                            List<String> buddiesList;

                            for (int i = 0; i < length; i++) {
                                jsonObject = response.getJSONArray("journeys").getJSONObject(i);
                                idOnServer = jsonObject.getString("id");
                                name = jsonObject.getString("name");
                                tagLine = jsonObject.getString("tag_line");
                                createdBy = jsonObject.getString("created_by_id");
                                buddies = jsonObject.getJSONArray("buddy_ids").toString();
                                buddies.replace("[", "");
                                buddies.replace("]", "");
                                buddiesList = Arrays.asList(buddies.split(","));
                                laps = jsonObject.getJSONArray("journey_lap_ids").toString();
                                laps.replace("[", "");
                                laps.replace("]", "");
                                lapsList = Arrays.asList(laps.split(","));
                                journey = new Journey(idOnServer, name, tagLine, "friends",
                                        createdBy, lapsList, buddiesList, Constants.JOURNEY_STATUS_ACTIVE);
                                saveMemories(jsonObject.getJSONArray("memories"), idOnServer);
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
        AppController.getInstance().getRequestQueue().add(fetchJourneysRequest);
    }

    private void saveMemories(JSONArray memoriesArray, String journeyId) {
        Log.d(TAG, "saving memories " + memoriesArray);
        int i;
        JSONObject memory;
        int memoryType;
        String createdBy;
        String memoryId;
        String fileUrl;
        String thumbnailUrl;
        String description;
        String memType;
        String likedBy;
        String fileSize;
        Picture pic;


        Long createdAt = HelpMe.getCurrentTime();
        Long updatedAt = HelpMe.getCurrentTime();

        try {

            for (i = 0; i < memoriesArray.length(); i++) {

                /*memory = memoriesArray.getJSONObject(i);

                memoryType = Integer.parseInt(memory.getString("type"));
                createdBy = memory.getJSONObject("user").getString("user_id");
                memoryId = memory.getJSONObject("memory").getString("id");*/
                JSONObject object = memoriesArray.getJSONObject(i);
                Iterator<?> keys = object.keys();

                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    Log.d(TAG, "key is " + key);

                    if (key.equals("picture") && object.get(key) instanceof JSONObject) {

                        memory = (JSONObject) object.get(key);
                        createdBy = memory.getJSONObject("user").getString("user_id");
                        memoryId = memory.getJSONObject("memory").getString("id");

                        fileUrl = memory.getJSONObject("memory").getJSONObject("picture_file").getString("url");
                        thumbnailUrl = memory.getJSONObject("memory").getJSONObject("picture_file").getJSONObject("medium").getString("url");
                        Picture newPic = new Picture(memoryId, journeyId, HelpMe.PICTURE_TYPE, null, "jpg",
                                100, fileUrl, null, createdBy, createdAt, updatedAt, null, thumbnailUrl);
                        PictureUtilities.createNewPicFromServer(PullMemoriesService.this, newPic, thumbnailUrl);
                    } else if (key.equals("note") && object.get(key) instanceof JSONObject) {
                        memory = (JSONObject) object.get(key);
                        createdBy = memory.getJSONObject("user").getString("user_id");
                        memoryId = memory.getJSONObject("memory").getString("id");
                        String content = memory.getJSONObject("memory").getString("note");
                        String caption = null;//memory.getJSONObject("memory").getString("caption");
                        Note newNote = new Note(memoryId, journeyId, HelpMe.NOTE_TYPE, caption, content, createdBy,
                                createdAt, updatedAt, null);

                        NoteDataSource.createNote(newNote, PullMemoriesService.this);
                    } else if (key.equals("video") && object.get(key) instanceof JSONObject) {
                        memory = (JSONObject) object.get(key);
                        createdBy = memory.getJSONObject("user").getString("user_id");
                        memoryId = memory.getJSONObject("memory").getString("id");
                        fileUrl = memory.getJSONObject("memory").getJSONObject("video_file").getString("url");
                        thumbnailUrl = memory.getJSONObject("memory").getJSONObject("video_file").getString("thumb");
                        description = memory.getJSONObject("memory").getJSONObject("video_file").getString("url");
                        Video newVideo = new Video(null, journeyId, HelpMe.VIDEO_TYPE, description,
                                "png", 1223, null, fileUrl, createdBy, createdAt, updatedAt, null, thumbnailUrl);
                        VideoUtil.createNewVideoFromServer(PullMemoriesService.this, newVideo, thumbnailUrl);

                    } else if (key.equals("checkin") && object.get(key) instanceof JSONObject) {
                        memory = (JSONObject) object.get(key);
                        createdBy = memory.getJSONObject("user").getString("user_id");
                        memoryId = memory.getJSONObject("memory").getString("id");
                        Log.d(TAG, "parsing checkin");
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

                    } else if (key.equals("mood") && object.get(key) instanceof JSONObject) {
                        memory = (JSONObject) object.get(key);
                        createdBy = memory.getJSONObject("user").getString("user_id");
                        memoryId = memory.getJSONObject("memory").getString("id");
                        Log.d(TAG, "parsing mood");
                        String mood = memory.getJSONObject("memory").getString("mood");
                        String reason = memory.getJSONObject("memory").getString("reason");
                        String buddy = memory.getJSONObject("memory").getString("buddies");
                        List<String> buddyId = buddy == null ? null : Arrays.asList(buddy.split(","));
                        Mood newMood = new Mood(memoryId, journeyId, HelpMe.MOOD_TYPE, buddyId, mood, reason,
                                createdBy, createdAt, updatedAt, null);
                        MoodDataSource.createMood(newMood, PullMemoriesService.this);

                    } else if (key.equals("audio") && object.get(key) instanceof JSONObject) {
                        memory = (JSONObject) object.get(key);
                        createdBy = memory.getJSONObject("user").getString("user_id");
                        memoryId = memory.getJSONObject("memory").getString("id");
                        Log.d(TAG, "parsing audio");
                        fileUrl = memory.getJSONObject("memory").getJSONObject("audio_file").getString("url");
                        //Long size = Long.parseLong(memory.getJSONObject("memory").getJSONObject("audio_file").getString("size"));

                        Audio newAudio = new Audio(memoryId, journeyId, HelpMe.AUDIO_TYPE, "3gp", 1122,
                                fileUrl, null, createdBy, createdAt, updatedAt, null);
                        AudioDataSource.createAudio(newAudio, PullMemoriesService.this);

                    }
                }
            }
        } catch (Exception ex) {
            Log.d(TAG, "exception in parsing memories " + ex);
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "pull memories service onDestroy()");
        Bundle bundle = new Bundle();
        mReceiver.send(REQUEST_CODE, bundle);
    }
}