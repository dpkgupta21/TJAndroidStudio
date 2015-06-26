package com.traveljar.memories.services;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.traveljar.memories.SQLitedatabase.AudioDataSource;
import com.traveljar.memories.SQLitedatabase.CheckinDataSource;
import com.traveljar.memories.SQLitedatabase.LikeDataSource;
import com.traveljar.memories.SQLitedatabase.MoodDataSource;
import com.traveljar.memories.SQLitedatabase.NoteDataSource;
import com.traveljar.memories.models.Audio;
import com.traveljar.memories.models.CheckIn;
import com.traveljar.memories.models.Journey;
import com.traveljar.memories.models.Like;
import com.traveljar.memories.models.Mood;
import com.traveljar.memories.models.Note;
import com.traveljar.memories.models.Picture;
import com.traveljar.memories.models.Video;
import com.traveljar.memories.utility.Constants;
import com.traveljar.memories.utility.HelpMe;
import com.traveljar.memories.utility.PictureUtilities;
import com.traveljar.memories.utility.TJPreferences;
import com.traveljar.memories.utility.VideoUtil;
import com.traveljar.memories.volley.AppController;
import com.traveljar.memories.volley.CustomJsonRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class PullJourney implements VideoUtil.OnFinishDownloadListener, PictureUtilities.OnFinishDownloadListener{

    private static final String TAG = "PullMemoriesService";
    private static int count = 0;
    private static boolean isService = false;
    private Journey journey;
    private Context context;
    private OnTaskFinishListener mListener;
    private static PullJourney instance;

    public static PullJourney getInstance(){
        return instance == null ? new PullJourney() : instance;
    }

    public PullJourney(){
        instance = this;
    }

    public PullJourney(Journey journey, Context context, OnTaskFinishListener mListener){
        this.journey = journey;
        this.context = context;
        this.mListener = mListener;
        instance = this;
    }

    public void fetchJourneys() {
        isService = true;
        Log.d(TAG, "fetch journeys");
        String fetchJourneysUrl = Constants.URL_JOURNEYS_FETCH + journey.getIdOnServer() + "?api_key=" + TJPreferences.getApiKey(context);
        Log.d(TAG, "url to fetch journey" + fetchJourneysUrl);
        CustomJsonRequest fetchJourneysRequest = new CustomJsonRequest(Request.Method.GET, fetchJourneysUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(TAG, "=====" + response.getJSONObject("journey"));
                            JSONObject jsonObject = response.getJSONObject("journey");
                            JSONArray memoriesList;
                            memoriesList = jsonObject.getJSONArray("memories");
                            if (memoriesList != null) {
                                Log.d(TAG, "there are no memories");
                                saveMemories(jsonObject.getJSONArray("memories"), journey.getIdOnServer());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        isFinished();

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
        String createdBy;
        String memoryId;
        String fileUrl;
        String thumbnailUrl;
        String description;
        long audioDuration;
        long id;

        Double latitude;
        Double longitude;

        Long createdAt;
        Long updatedAt;

        try {

            for (i = 0; i < memoriesArray.length(); i++) {
                JSONObject object = memoriesArray.getJSONObject(i);
                Iterator<?> keys = object.keys();

                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    Log.d(TAG, "key is " + key);
                    memory = (JSONObject) object.get(key);
                    createdAt = Long.parseLong(memory.getString("created_at"));
                    updatedAt = Long.parseLong(memory.getString("updated_at"));

                    if (key.equals("picture") && object.get(key) instanceof JSONObject) {

                        createdBy = memory.getString("user_id");
                        memoryId = memory.getString("id");
                        description = memory.getString("description").equals("null") ? "" : memory.getString("description");

                        fileUrl = memory.getJSONObject("picture_file").getJSONObject("original").getString("url");
                        thumbnailUrl = memory.getJSONObject("picture_file").getJSONObject("medium").getString("url");

                        latitude = memory.getString("latitude").equals("null") ? 0.0d : Double.parseDouble(memory.getString("latitude"));
                        longitude = memory.getString("longitude").equals("null") ? 0.0d : Double.parseDouble(memory.getString("longitude"));

                        Picture newPic = new Picture(memoryId, journeyId, HelpMe.PICTURE_TYPE, description, "jpg",
                                100, fileUrl, null, createdBy, createdAt, updatedAt, null, thumbnailUrl, latitude, longitude);
                        parseAndSaveLikes(memory.getJSONArray("likes"), null, HelpMe.PICTURE_TYPE, journeyId, memoryId);
                        PictureUtilities.getInstance().setOnFinishDownloadListener(this);
                        PictureUtilities.getInstance().createNewPicFromServer(context, newPic, thumbnailUrl);
                        count++;
                        Log.d(TAG, "picture parsed and saved successfully");

                    } else if (key.equals("note") && object.get(key) instanceof JSONObject) {

                        createdBy = memory.getString("user_id");
                        memoryId = memory.getString("id");
                        String content = memory.getString("note");
                        String caption = null;//memory.getJSONObject("memory").getString("caption");

                        latitude = memory.getString("latitude").equals("null") ? 0.0d : Double.parseDouble(memory.getString("latitude"));
                        longitude = memory.getString("longitude").equals("null") ? 0.0d : Double.parseDouble(memory.getString("longitude"));

                        Note newNote = new Note(memoryId, journeyId, HelpMe.NOTE_TYPE, caption, content, createdBy,
                                createdAt, updatedAt, null, latitude, longitude);

                        id = NoteDataSource.createNote(newNote, context);
                        parseAndSaveLikes(memory.getJSONArray("likes"), String.valueOf(id), HelpMe.NOTE_TYPE, journeyId, memoryId);

                        Log.d(TAG, "note parsed and saved successfully");

                    } else if (key.equals("video") && object.get(key) instanceof JSONObject) {

                        createdBy = memory.getString("user_id");
                        memoryId = memory.getString("id");
                        fileUrl = memory.getJSONObject("video_file").getString("url");
                        thumbnailUrl = memory.getJSONObject("video_file").getString("thumb");
                        description = memory.getString("description").equals("null") ? "" : memory.getString("description");

                        latitude = memory.getString("latitude").equals("null") ? 0.0d : Double.parseDouble(memory.getString("latitude"));
                        longitude = memory.getString("longitude").equals("null") ? 0.0d : Double.parseDouble(memory.getString("longitude"));

                        Video newVideo = new Video(memoryId, journeyId, HelpMe.VIDEO_TYPE, description,
                                "png", 1223, null, fileUrl, createdBy, createdAt, updatedAt, null, thumbnailUrl, latitude, longitude);

                        parseAndSaveLikes(memory.getJSONArray("likes"), null, HelpMe.VIDEO_TYPE, journeyId, memoryId);
                        VideoUtil.getInstance().setOnFinishDownloadListener(this);
                        VideoUtil.getInstance().createNewVideoFromServer(context, newVideo, thumbnailUrl);
                        count++;
                        Log.d(TAG, "video parsed and saved successfully" + thumbnailUrl);

                    } else if (key.equals("checkin") && object.get(key) instanceof JSONObject) {

                        createdBy = memory.getString("user_id");
                        memoryId = memory.getString("id");

                        latitude = memory.getString("latitude").equals("null") ? 0.0d : Double.parseDouble(memory.getString("latitude"));
                        longitude = memory.getString("longitude").equals("null") ? 0.0d : Double.parseDouble(memory.getString("longitude"));

                        String placeName = memory.getString("place_name");
                        String note = memory.getString("note");
                        String buddys = memory.getString("buddies");
                        List<String> buddyIds = buddys == null ? null : Arrays.asList(buddys.split(","));
                        CheckIn newCheckIn = new CheckIn(memoryId, journeyId, HelpMe.CHECKIN_TYPE, note, latitude, longitude, placeName, null, buddyIds, createdBy,
                                createdAt, updatedAt, null);
                        id = CheckinDataSource.createCheckIn(newCheckIn, context);
                        parseAndSaveLikes(memory.getJSONArray("likes"), String.valueOf(id), HelpMe.CHECKIN_TYPE, journeyId, memoryId);

                        Log.d(TAG, "checkin parsed and saved successfully");

                    } else if (key.equals("mood") && object.get(key) instanceof JSONObject) {

                        createdBy = memory.getString("user_id");
                        memoryId = memory.getString("id");
                        String mood = memory.getString("mood");
                        String reason = memory.getString("reason");
                        String buddy = memory.getString("buddies");
                        List<String> buddyId = buddy == null ? null : Arrays.asList(buddy.split(","));

                        latitude = memory.getString("latitude").equals("null") ? 0.0d : Double.parseDouble(memory.getString("latitude"));
                        longitude = memory.getString("longitude").equals("null") ? 0.0d : Double.parseDouble(memory.getString("longitude"));

                        Mood newMood = new Mood(memoryId, journeyId, HelpMe.MOOD_TYPE, buddyId, mood, reason,
                                createdBy, createdAt, updatedAt, null, latitude, longitude);
                        id = MoodDataSource.createMood(newMood, context);
                        parseAndSaveLikes(memory.getJSONArray("likes"), String.valueOf(id), HelpMe.MOOD_TYPE, journeyId, memoryId);

                        Log.d(TAG, "mood parsed and saved successfully");

                    } else if (key.equals("audio") && object.get(key) instanceof JSONObject) {

                        createdBy = memory.getString("user_id");
                        memoryId = memory.getString("id");
                        fileUrl = memory.getJSONObject("audio_file").getString("url");
                        //audioDuration = memory.getString("duration") == "null" ? 0 : Long.parseLong(memory.getString("duration"));
                        //Long size = Long.parseLong(memory.getJSONObject("memory").getJSONObject("audio_file").getString("size"));

                        latitude = memory.getString("latitude").equals("null") ? 0.0d : Double.parseDouble(memory.getString("latitude"));
                        longitude = memory.getString("longitude").equals("null") ? 0.0d : Double.parseDouble(memory.getString("longitude"));
                        Audio newAudio = new Audio(memoryId, journeyId, HelpMe.AUDIO_TYPE, "3gp", 1122,
                                fileUrl, null, createdBy, createdAt, updatedAt, null, 0, latitude, longitude);
                        id = AudioDataSource.createAudio(newAudio, context);
                        parseAndSaveLikes(memory.getJSONArray("likes"), String.valueOf(id), HelpMe.AUDIO_TYPE, journeyId, memoryId);

                        Log.d(TAG, "audio parsed and saved successfully");

                    }
                }
            }
        } catch (Exception ex) {
            Log.d(TAG, "exception in parsing memories " + ex);
        }
    }

    public void parseAndSaveLikes(JSONArray jsonArray, String memoryId, String memType, String journeyId, String memServerId){
        String idOnServer;
        String userId;
        JSONObject jsonObject;
        long createdAt;
        long updatedAt;
        int size = jsonArray.length();
        for(int i = 0; i < size; i++){
            try {
                jsonObject = jsonArray.getJSONObject(i);
                idOnServer = jsonObject.getString("id");
                userId = jsonObject.getString("user_id");
                createdAt = Long.parseLong(jsonObject.getString("created_at"));
                updatedAt = Long.parseLong(jsonObject.getString("updated_at"));
                Like like = new Like(null, idOnServer, journeyId, memoryId, userId, memType, true, memServerId, createdAt, updatedAt);
                LikeDataSource.createLike(like, context);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onFinishDownload(String serverId, String memType, String localId) {
        Log.d(TAG, "updating memory local id for mem type " + memType);
        LikeDataSource.updateMemoryLocalId(serverId, memType, localId, context);
        isFinished();
    }

    public interface OnTaskFinishListener {
        void onFinishTask(Journey journey);
    }

    public void isFinished() {
        if (isService) {
            count--;
            Log.d(TAG, "not finished" + count);
            if (count < 0) {
                Log.d(TAG, "isfinished called" + count);
                count = 0;
                isService = false;
                mListener.onFinishTask(journey);
            }
        }
    }
}
