package com.traveljar.memories.services;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.traveljar.memories.SQLitedatabase.AudioDataSource;
import com.traveljar.memories.SQLitedatabase.ContactDataSource;
import com.traveljar.memories.SQLitedatabase.JourneyDataSource;
import com.traveljar.memories.SQLitedatabase.LapsDataSource;
import com.traveljar.memories.SQLitedatabase.LikeDataSource;
import com.traveljar.memories.SQLitedatabase.MoodDataSource;
import com.traveljar.memories.SQLitedatabase.NoteDataSource;
import com.traveljar.memories.SQLitedatabase.PlaceDataSource;
import com.traveljar.memories.eventbus.CheckInDownloadEvent;
import com.traveljar.memories.eventbus.PictureDownloadEvent;
import com.traveljar.memories.eventbus.VideoDownloadEvent;
import com.traveljar.memories.models.Audio;
import com.traveljar.memories.models.CheckIn;
import com.traveljar.memories.models.Journey;
import com.traveljar.memories.models.Laps;
import com.traveljar.memories.models.Like;
import com.traveljar.memories.models.Mood;
import com.traveljar.memories.models.Note;
import com.traveljar.memories.models.Picture;
import com.traveljar.memories.models.Place;
import com.traveljar.memories.models.Video;
import com.traveljar.memories.utility.CheckinUtil;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import de.greenrobot.event.EventBus;

public class PullMemoriesService {
    private static final String TAG = "PullMemoriesService";
    private static int REQUEST_CODE;
    private static int count = 0;
    private static boolean isService = false;
    private static OnTaskFinishListener mListener;
    private Journey journey;
    private Context mContext;

    //    private static final int DOWNLOAD_PICTURE_EVENT_CODE = 0;
//    private static final int DOWNLOAD_VIDEO_EVENT_CODE = 1;
    private static final int VIDEO_DOWNLOAD_REQUESTER_CODE = 0;
    private static final int PICTURE_DOWNLOAD_REQUESTER_CODE = 0;
    private static final int CHECKIN_DOWNLOAD_REQUESTER_CODE = 0;


    public PullMemoriesService(Context context, OnTaskFinishListener listener, int REQUEST_CODE) {
        mContext = context;
        mListener = listener;
        this.REQUEST_CODE = REQUEST_CODE;
    }

    public void fetchJourneys() {
        registerEvent();
        isService = true;
        Log.d(TAG, "fetch journeys");
        String fetchJourneysUrl = Constants.URL_JOURNEYS_FETCH + "?api_key=" + TJPreferences.getApiKey(mContext) + "&user_id=" + TJPreferences.getUserId(mContext);
        Log.d(TAG, "url to fetch journeys" + fetchJourneysUrl);
        CustomJsonRequest fetchJourneysRequest = new CustomJsonRequest(Request.Method.GET, fetchJourneysUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(TAG, "=====" + response.getJSONArray("journeys"));
                            JSONObject jsonObject;
                            JSONArray journeyJSONArray = response.getJSONArray("journeys");
                            int length = journeyJSONArray.length();
                            Log.d(TAG, "total journes length = " + length);
                            String idOnServer;
                            String name;
                            String tagLine;
                            String createdBy;
                            long createdAt;
                            long updatedAt;
                            JSONArray laps;
                            List<String> lapsList;
                            List<String> buddyList;
                            JSONArray memoriesList;
                            String journeyStatus;
                            boolean isUserActive;


                            for (int i = 0; i < length; i++) {
                                jsonObject = journeyJSONArray.getJSONObject(i);
                                idOnServer = jsonObject.getString("id");
                                name = jsonObject.getString("name");
                                tagLine = jsonObject.getString("tag_line");
                                createdBy = jsonObject.getString("created_by_id");
                                createdAt = Long.parseLong(jsonObject.getString("created_at"));
                                updatedAt = Long.parseLong(jsonObject.getString("updated_at"));
                                isUserActive = Boolean.parseBoolean(jsonObject.getString("active"));


                                JSONArray jsonArray = jsonObject.getJSONArray("buddy_ids");
                                buddyList = new ArrayList<>();
                                for (int j = 0; j < jsonArray.length(); j++) {
                                    buddyList.add(jsonArray.getString(j));
                                }

                                buddyList.add(createdBy);
                                buddyList.remove(TJPreferences.getUserId(mContext));

                                laps = jsonObject.getJSONArray("journey_laps");
                                parseLaps(laps, idOnServer);
                                journeyStatus = jsonObject.getString("completed_at").equals("null") ? Constants.JOURNEY_STATUS_ACTIVE : Constants.JOURNEY_STATUS_FINISHED;

                                journey = new Journey(idOnServer, name, tagLine, "friends",
                                        createdBy, null, buddyList, journeyStatus, createdAt, updatedAt, 0, isUserActive);
                                JourneyDataSource.createJourney(journey, mContext);

                                buddyList.add(TJPreferences.getUserId(mContext));
                                ArrayList<String> nonExistingContacts = (ArrayList<String>) ContactDataSource.getNonExistingContacts(mContext, buddyList);
                                if (nonExistingContacts != null && !nonExistingContacts.isEmpty()) {
                                    Log.d(TAG, "some buddies need to be fetched from server hence fetching from server" + buddyList.size());
//                                    registerEvent();
                                    new PullBuddies(mContext.getApplicationContext(), nonExistingContacts, 0).fetchBuddies();
                                }

                                memoriesList = jsonObject.getJSONArray("memories");
                                if (memoriesList != null) {
                                    Log.d(TAG, "there are no memories");
                                    saveMemories(jsonObject.getJSONArray("memories"), idOnServer);
                                }

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
        fetchJourneysRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (HelpMe.isNetworkAvailable(mContext)) {
            AppController.getInstance().getRequestQueue().add(fetchJourneysRequest);
        } else {
            unRegisterEvent();
            Toast.makeText(mContext, "Network unavailable please turn on your data", Toast.LENGTH_SHORT).show();
        }
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
        String imagePath;
        String localDataPath = null;

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

                        imagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() +
                                "/pic_" + createdBy + "_" + journeyId + "_" + createdAt + ".jpg";
                        if((new File(imagePath).exists())){
                            localDataPath = imagePath;
                        }

                        Picture newPic = new Picture(memoryId, journeyId, HelpMe.PICTURE_TYPE, description, "jpg",
                                100, fileUrl, localDataPath, createdBy, createdAt, updatedAt, null, thumbnailUrl, latitude, longitude);
                        parseAndSaveLikes(memory.getJSONArray("likes"), null, HelpMe.PICTURE_TYPE, journeyId, memoryId);
                        //PictureUtilities.getInstance().setOnFinishDownloadListener(this);
                        count++;
                        PictureUtilities.getInstance().createNewPicFromServer(mContext, newPic, thumbnailUrl, PICTURE_DOWNLOAD_REQUESTER_CODE);
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

                        id = NoteDataSource.createNote(newNote, mContext);
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
                        count++;
                        VideoUtil.getInstance().createNewVideoFromServer(mContext, newVideo, thumbnailUrl, VIDEO_DOWNLOAD_REQUESTER_CODE);
                        //parseAndSaveLikes(memory.getJSONArray("likes"), String.valueOf(id), HelpMe.CHECKIN_TYPE, journeyId);

                    } else if (key.equals("checkin") && object.get(key) instanceof JSONObject) {
                        createdBy = memory.getString("user_id");
                        memoryId = memory.getString("id");

                        latitude = memory.getString("latitude").equals("null") ? 0.0d : Double.parseDouble(memory.getString("latitude"));
                        longitude = memory.getString("longitude").equals("null") ? 0.0d : Double.parseDouble(memory.getString("longitude"));

                        String placeName = memory.getString("place_name");
                        String note = memory.getString("note");
                        String buddys = memory.getString("buddies");
                        List<String> buddyIds = buddys == null ? null : Arrays.asList(buddys.split(","));

                        fileUrl = memory.getJSONObject("picture_file").getJSONObject("original").getString("url");
                        thumbnailUrl = memory.getJSONObject("picture_file").getJSONObject("medium").getString("url");

                        CheckIn newCheckIn = new CheckIn(memoryId, journeyId, HelpMe.CHECKIN_TYPE, note, latitude, longitude, placeName,
                                null, fileUrl, thumbnailUrl, buddyIds, createdBy, createdAt, updatedAt);

                        parseAndSaveLikes(memory.getJSONArray("likes"), null, HelpMe.CHECKIN_TYPE, journeyId, memoryId);
                        count++;
                        CheckinUtil.createNewCheckInFromServer(mContext, newCheckIn, CHECKIN_DOWNLOAD_REQUESTER_CODE);

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
                        id = MoodDataSource.createMood(newMood, mContext);
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
                        id = AudioDataSource.createAudio(newAudio, mContext);
                        parseAndSaveLikes(memory.getJSONArray("likes"), String.valueOf(id), HelpMe.AUDIO_TYPE, journeyId, memoryId);

                        Log.d(TAG, "audio parsed and saved successfully");

                    }
                }
            }
        } catch (Exception ex) {
            Log.d(TAG, "exception in parsing memories " + ex);
        }
    }

    public void parseAndSaveLikes(JSONArray jsonArray, String memoryId, String memType, String journeyId, String memServerId) {
        String idOnServer;
        String userId;
        JSONObject jsonObject;
        long createdAt;
        long updatedAt;
        int size = jsonArray.length();
        for (int i = 0; i < size; i++) {
            try {
                jsonObject = jsonArray.getJSONObject(i);
                idOnServer = jsonObject.getString("id");
                userId = jsonObject.getString("user_id");
                createdAt = Long.parseLong(jsonObject.getString("created_at"));
                updatedAt = Long.parseLong(jsonObject.getString("updated_at"));
                Like like = new Like(null, idOnServer, journeyId, memoryId, userId, memType, true, memServerId, createdAt, updatedAt);
                LikeDataSource.createLike(like, mContext);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public static void isFinished() {
        Log.d(TAG, "not finished" + count);
        if (isService) {
            count--;
            if (count < 0) {
                Log.d(TAG, "isfinished called" + count);
                count = 0;
                isService = false;
                mListener.onFinishTask(REQUEST_CODE);
            }
        }
    }

    private void parseLaps(JSONArray journeyLaps, String journeyId) {
        Laps laps;
        Place source;
        Place destination;
        JSONObject lapObject;
        JSONObject sourceObject = null;
        JSONObject destinationObject;
        Double latitude;
        Double longitude;
        long sourceId;
        long destinationId;
        int noLaps = journeyLaps.length();
        for (int i = 0; i < noLaps; i++) {
            try {
                lapObject = (JSONObject) journeyLaps.get(i);
                sourceObject = lapObject.getJSONObject("source");
                destinationObject = lapObject.getJSONObject("destination");

                //Parsing source place
                latitude = sourceObject.getString("latitude").equals("null") ? 0.0 : Double.parseDouble(sourceObject.getString("latitude"));
                longitude = sourceObject.getString("longitude").equals("null") ? 0.0 : Double.parseDouble(sourceObject.getString("longitude"));

                source = new Place(null, sourceObject.getString("id"), sourceObject.getString("country"), sourceObject.getString("state"),
                        sourceObject.getString("city"), Long.parseLong(sourceObject.getString("created_at")), latitude, longitude);
                sourceId = PlaceDataSource.createPlace(source, mContext);

                // Parsing destination Place
                latitude = destinationObject.getString("latitude").equals("null") ? 0.0 : Double.parseDouble(destinationObject.getString("latitude"));
                longitude = destinationObject.getString("longitude").equals("null") ? 0.0 : Double.parseDouble(destinationObject.getString("longitude"));

                destination = new Place(null, destinationObject.getString("id"), destinationObject.getString("country"), destinationObject.getString("state"),
                        destinationObject.getString("city"), Long.parseLong(destinationObject.getString("created_at")), latitude, longitude);
                destinationId = PlaceDataSource.createPlace(destination, mContext);

                laps = new Laps(null, lapObject.getString("id"), journeyId, String.valueOf(sourceId), String.valueOf(destinationId),
                        HelpMe.getConveyanceModeCode(lapObject.getString("travel_mode")), Long.parseLong(lapObject.getString("start_date")));
                LapsDataSource.createLap(laps, mContext);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
    }

    public interface OnTaskFinishListener {
        void onFinishTask(int REQUEST_CODES);
    }

    private void registerEvent() {
        EventBus.getDefault().register(this);
    }

    private void unRegisterEvent() {
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(PictureDownloadEvent event) {
        Log.d(TAG, "picture downloaded successfully");
        if (event.getCallerCode() == PICTURE_DOWNLOAD_REQUESTER_CODE && event.isSuccess()) {
            LikeDataSource.updateMemoryLocalId(event.getPicture().getIdOnServer(), event.getPicture().getMemType(),
                    event.getPicture().getId(), mContext);
        }
    }

    public void onEvent(VideoDownloadEvent event) {
        Log.d(TAG, "video downloaded successfully");
        if (event.getCallerCode() == VIDEO_DOWNLOAD_REQUESTER_CODE && event.isSuccess()) {
            LikeDataSource.updateMemoryLocalId(event.getVideo().getIdOnServer(), event.getVideo().getMemType(), event.getVideo().getId(),
                    mContext);
        }
    }

    public void onEvent(CheckInDownloadEvent event) {
        Log.d(TAG, "checkin downloaded successfully");
        if (event.getCallerCode() == CHECKIN_DOWNLOAD_REQUESTER_CODE && event.isSuccess()) {
            LikeDataSource.updateMemoryLocalId(event.getCheckIn().getIdOnServer(), event.getCheckIn().getMemType(), event.getCheckIn().getId(),
                    mContext);
        }
    }
}