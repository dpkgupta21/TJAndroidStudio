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
    private static ResultReceiver mReceiver;
    private static int REQUEST_CODE;
    private Journey journey;
    private static int count = 0;

    public PullMemoriesService() {
        super("PullMemoriesService");
    }

    public PullMemoriesService(String name) {
        super(name);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, startId, startId);
        Log.d(TAG, "on start command");
        mReceiver = intent.getParcelableExtra("RECEIVER");
        REQUEST_CODE = intent.getIntExtra("REQUEST_CODE", 0);
        Log.d(TAG, "on start command");
        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        fetchJourneys();
        Log.d(TAG, "on Handle Intent");
    }

    private void fetchJourneys() {//user id 26 QTn0lwDmgWTc4K5R4Xlz7g
        Log.d(TAG, "fetch journeys");
        String fetchJourneysUrl = Constants.URL_TJ_DOMAIN + "api/v1/journeys?api_key=" + TJPreferences.getApiKey(this) + "&user_id=" + TJPreferences.getUserId(this);
        Log.d(TAG, "url to fetch journeys" + fetchJourneysUrl);
        CustomJsonRequest fetchJourneysRequest = new CustomJsonRequest(Request.Method.GET, fetchJourneysUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(TAG, "=====" + response.getJSONArray("journeys"));
//                            /JSONArray jsonArray = response.getJSONArray("journeys");
                            JSONObject jsonObject;
                            JSONArray journeyJSONArray = response.getJSONArray("journeys");
                            int length = journeyJSONArray.length();
                            String idOnServer;
                            String name;
                            String tagLine;
                            String createdBy;
                            String laps;
                            List<String> lapsList;
                            String buddies;
                            List<String> buddiesList;

                            for (int i = 0; i < length; i++) {
                                jsonObject = journeyJSONArray.getJSONObject(i);
                                idOnServer = jsonObject.getString("id");
                                name = jsonObject.getString("name");
                                tagLine = jsonObject.getString("tag_line");
                                createdBy = jsonObject.getString("created_by_id");
                                buddies = jsonObject.getJSONArray("buddy_ids").toString();
                                Log.d(TAG, "buddies list saved in database are " + buddies);
                                buddies = buddies.replace("[", "");
                                buddies = buddies.replace("]", "");
                                buddiesList = Arrays.asList(buddies.split(","));
                                Log.d(TAG, "buddies list saved in database are " + buddiesList + " blah blah " + buddies);
                                laps = jsonObject.getJSONArray("journey_lap_ids").toString();
                                laps.replace("[",  "");
                                laps.replace("]", "");
                                lapsList = Arrays.asList(laps.split(","));

                                journey = new Journey(idOnServer, name, tagLine, "friends",
                                        createdBy, lapsList, buddiesList, Constants.JOURNEY_STATUS_ACTIVE);
                                JourneyDataSource.createJourney(journey, PullMemoriesService.this);
                                Log.d(TAG, "journey parsed and saved successfully in the database");
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
                    memory = (JSONObject) object.get(key);

                    if (key.equals("picture") && object.get(key) instanceof JSONObject) {

                        createdBy = memory.getString("user_id");
                        memoryId = memory.getString("id");

                        fileUrl = memory.getJSONObject("picture_file").getJSONObject("original").getString("url");
                        thumbnailUrl = memory.getJSONObject("picture_file").getJSONObject("medium").getString("url");
                        Picture newPic = new Picture(memoryId, journeyId, HelpMe.PICTURE_TYPE, null, "jpg",
                                100, fileUrl, null, createdBy, createdAt, updatedAt, null, thumbnailUrl);
                        PictureUtilities.createNewPicFromServer(PullMemoriesService.this, newPic, thumbnailUrl);
                        count++;
                        Log.d(TAG, "picture parsed and saved successfully");

                    } else if (key.equals("note") && object.get(key) instanceof JSONObject) {

                        createdBy = memory.getString("user_id");
                        memoryId = memory.getString("id");
                        String content = memory.getString("note");
                        String caption = null;//memory.getJSONObject("memory").getString("caption");
                        Note newNote = new Note(memoryId, journeyId, HelpMe.NOTE_TYPE, caption, content, createdBy,
                                createdAt, updatedAt, null);

                        NoteDataSource.createNote(newNote, PullMemoriesService.this);

                        Log.d(TAG, "note parsed and saved successfully");

                    } else if (key.equals("video") && object.get(key) instanceof JSONObject) {

                        createdBy = memory.getString("user_id");
                        memoryId = memory.getString("id");
                        fileUrl = memory.getJSONObject("video_file").getString("url");
                        thumbnailUrl = memory.getJSONObject("video_file").getString("thumb");
                        description = memory.getString("description");
                        Video newVideo = new Video(memoryId, journeyId, HelpMe.VIDEO_TYPE, description,
                                "png", 1223, null, fileUrl, createdBy, createdAt, updatedAt, null, thumbnailUrl);
                        VideoUtil.createNewVideoFromServer(PullMemoriesService.this, newVideo, thumbnailUrl);
                        count++;
                        Log.d(TAG, "video parsed and saved successfully" + thumbnailUrl);

                    } else if (key.equals("checkin") && object.get(key) instanceof JSONObject) {

                        createdBy = memory.getString("user_id");
                        memoryId = memory.getString("id");
                        Log.d(TAG, "parsing checkin");
                        String lat = memory.getString("latitude");
                        String lon = memory.getString("longitude");

                        Double latitude = (lat == "null" ? 0.0 : Double.parseDouble(lat));
                        Log.d(TAG, "latitude is " + latitude);
                        Double longitude = (lon == "null" ? 0.0 : Double.parseDouble(lon));
                        String placeName = memory.getString("place_name");
                        String note = memory.getString("note");
                        String buddys = memory.getString("buddies");
                        List<String> buddyIds = buddys == null ? null : Arrays.asList(buddys.split(","));
                        CheckIn newCheckIn = new CheckIn(memoryId, journeyId, HelpMe.CHECKIN_TYPE, note, latitude, longitude, placeName, null, buddyIds, createdBy,
                                createdAt, updatedAt, null);
                        CheckinDataSource.createCheckIn(newCheckIn, PullMemoriesService.this);

                        Log.d(TAG, "checkin parsed and saved successfully");

                    } else if (key.equals("mood") && object.get(key) instanceof JSONObject) {

                        createdBy = memory.getString("user_id");
                        memoryId = memory.getString("id");
                        Log.d(TAG, "parsing mood");
                        String mood = memory.getString("mood");
                        String reason = memory.getString("reason");
                        String buddy = memory.getString("buddies");
                        List<String> buddyId = buddy == null ? null : Arrays.asList(buddy.split(","));
                        Mood newMood = new Mood(memoryId, journeyId, HelpMe.MOOD_TYPE, buddyId, mood, reason,
                                createdBy, createdAt, updatedAt, null);
                        MoodDataSource.createMood(newMood, PullMemoriesService.this);

                        Log.d(TAG, "mood parsed and saved successfully");

                    } else if (key.equals("audio") && object.get(key) instanceof JSONObject) {

                        createdBy = memory.getString("user_id");
                        memoryId = memory.getString("id");
                        fileUrl = memory.getJSONObject("audio_file").getString("url");
                        //Long size = Long.parseLong(memory.getJSONObject("memory").getJSONObject("audio_file").getString("size"));

                        Audio newAudio = new Audio(memoryId, journeyId, HelpMe.AUDIO_TYPE, "3gp", 1122,
                                fileUrl, null, createdBy, createdAt, updatedAt, null);
                        AudioDataSource.createAudio(newAudio, PullMemoriesService.this);

                        Log.d(TAG, "audio parsed and saved successfully");

                    }
                }
            }
        } catch (Exception ex) {
            Log.d(TAG, "exception in parsing memories " + ex);
        }
    }

    public static void isFinished(){
        count--;
        Log.d(TAG, "not finished" + count);
        if(count == 0) {
            Log.d(TAG, "isfimiehd cal;ed" + count);
            count = 0;
            Bundle bundle = new Bundle();
            mReceiver.send(REQUEST_CODE, bundle);
        }
    }
}