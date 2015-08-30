package com.traveljar.memories.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.AudioDataSource;
import com.traveljar.memories.SQLitedatabase.ContactDataSource;
import com.traveljar.memories.SQLitedatabase.JourneyDataSource;
import com.traveljar.memories.SQLitedatabase.LikeDataSource;
import com.traveljar.memories.SQLitedatabase.MemoriesDataSource;
import com.traveljar.memories.SQLitedatabase.MoodDataSource;
import com.traveljar.memories.SQLitedatabase.NoteDataSource;
import com.traveljar.memories.activejourney.ActivejourneyList;
import com.traveljar.memories.currentjourney.DownloadTimeCapsuleAsyncTask;
import com.traveljar.memories.models.Audio;
import com.traveljar.memories.models.CheckIn;
import com.traveljar.memories.models.Contact;
import com.traveljar.memories.models.Journey;
import com.traveljar.memories.models.Like;
import com.traveljar.memories.models.Memories;
import com.traveljar.memories.models.Mood;
import com.traveljar.memories.models.Note;
import com.traveljar.memories.models.Picture;
import com.traveljar.memories.models.Timecapsule;
import com.traveljar.memories.models.Video;
import com.traveljar.memories.services.PullJourney;
import com.traveljar.memories.utility.CheckinUtil;
import com.traveljar.memories.utility.Constants;
import com.traveljar.memories.utility.ContactsUtil;
import com.traveljar.memories.utility.HelpMe;
import com.traveljar.memories.utility.PictureUtilities;
import com.traveljar.memories.utility.TJPreferences;
import com.traveljar.memories.utility.VideoUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GcmIntentService extends IntentService implements PullJourney.OnTaskFinishListener{
    public static final int NOTIFICATION_ID = 1;
    public static final String TAG = "<GcmIntentService>";
    private NotificationManager mNotificationManager;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) { // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that
			 * GCM will be extended in the future with new message types, just
			 * ignore any message types you're not interested in, or that you
			 * don't recognize.
			 */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                //sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                //sendNotification("Deleted messages on server: " + extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                Log.d(TAG, "something recieved =" + extras.toString());

                // Check if it is a valid GCM message
                if ((extras).containsKey("type")) {
                    parseGcmMessage(extras);
                } else {
                    Log.d(TAG, "dodged a number verification exception");
                }
                
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void parseGcmMessage(Bundle bundle) {
        Log.d(TAG, " parseGcmMessage called " + bundle);
        String type = bundle.get("type").toString();

        // buddy_ids are recieved in format [5,6,7]
        String userIds = bundle.get("user_ids").toString();
        Log.d(TAG, "user ids from gcm are" + userIds);
        userIds = userIds.replace("[", "");
        userIds = userIds.replace("]", "");

        List<String> userIdList = new ArrayList<>(Arrays.asList(userIds.split(",")));

        Log.d(TAG, "1.3" + userIdList.toString());

        final String journeyId;
        String jName;
        String tagline;
        String createdBy;
        String memId;
        long createdAt;
        long updatedAt;
        long completedAt;
        String memoryType;
        Memories memories;
        String message;
        Journey journey;

        String userId;

        Log.d(TAG, "1.1");
        // COde to verify correct receipient
        // Check for user id
        // If userId is not present in the list, ignore this message
        Log.d(TAG, "====" + (!userIdList.contains(TJPreferences.getUserId(getBaseContext()))));
        if (!userIdList.contains(TJPreferences.getUserId(getBaseContext()))) {
            Log.d(TAG, "gcm notification ignored ");
            return;
        } else {
            Log.d(TAG, "gcm notification passesd ");
        }

        Log.d(TAG, "1.4");
        switch (Integer.parseInt(type)) {
            case HelpMe.TYPE_CREATE_MEMORY:
                String memType = bundle.get("memory_type").toString();
                String data = bundle.get("details").toString();
                journeyId = bundle.getString("journey_id");
                try {
                    Log.d(TAG, "type = create , so createMemory called");
                    createMemory(journeyId, Integer.parseInt(memType), new JSONObject(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                journey = JourneyDataSource.getJourneyById(this, journeyId);
                message = "A new memory has been added to the journey " + journey.getName();
                showNotification(message, ActivejourneyList.class);
                break;

            case HelpMe.TYPE_CREATE_JOURNEY:
                Log.d(TAG, "type = create journey");

                journeyId = bundle.get("id").toString();
                createdBy = bundle.get("created_by").toString();
                jName = bundle.get("name").toString();
                tagline = bundle.get("tag_line").toString();
                createdAt = Long.parseLong(bundle.getString("created_at"));
                updatedAt = Long.parseLong(bundle.getString("updated_at"));
                completedAt = 0;
                boolean isUserActive = true;

                Log.d(TAG, "bundle buddy ids are " + bundle.get("buddy_ids"));
                String buddyIds = (String) bundle.get("buddy_ids");
                buddyIds = buddyIds.replace("[", "");
                buddyIds = buddyIds.replace("]", "");
                List<String> buddyIdsList = new ArrayList<String>(Arrays.asList(buddyIds.split(",")));
                buddyIdsList.add(createdBy);
                buddyIdsList.remove(TJPreferences.getUserId(getBaseContext()));

                Journey jItem = new Journey(journeyId, jName, tagline, "Friends", createdBy, null, buddyIdsList,
                        Constants.JOURNEY_STATUS_ACTIVE, createdAt, updatedAt, completedAt, isUserActive);
                new PullJourney(jItem, this, this).fetchJourneys();
                message = "You have been added to a new journey by your friend";
                showNotification(message, ActivejourneyList.class);

                break;

            case HelpMe.TYPE_DELETE_MEMORY:
                memId = bundle.getString("memory_id");
                memType = bundle.getString("memory_type");
                MemoriesDataSource.deleteMemoryWithServerId(this, memType, memId);
                /*if(CurrentJourneyBaseActivity.isActivityVisible()){
                    Thread thread = new Thread(){
                            @Override
                            public void run() {
                                try {
                                    synchronized (this) {
                                        wait(5000);
                                        CurrentJourneyBaseActivity.getInstance().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                CurrentJourneyBaseActivity.getInstance().refreshTimelineList();
                                            }
                                        });

                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        thread.start();
                    }*/
                break;

            case HelpMe.TYPE_LIKE_MEMORY:
                userId = bundle.get("user_id").toString();
                journeyId = bundle.get("j_id").toString();
                memId = bundle.get("id").toString();
                memoryType = bundle.get("memory_type").toString();
                createdAt = Long.parseLong(bundle.get("created_at").toString());
                updatedAt = Long.parseLong(bundle.get("updated_at").toString());

                memories = MemoriesDataSource.getMemoryFromTypeAndId(this, memId, memoryType);

                if(memories != null) {
                    Like like = new Like(null, null, journeyId, memories.getId(), userId, memoryType, true, memId, createdAt, updatedAt);
                    LikeDataSource.createLike(like, this);
                }
                break;

            case HelpMe.TYPE_UNLIKE_MEMORY:
                memId = bundle.get("id").toString();
                memoryType = bundle.get("memory_type").toString();
                String user_id = bundle.get("user_id").toString();

                memories = MemoriesDataSource.getMemoryFromTypeAndId(this, memId, memoryType);
                if(memories != null) {
                    LikeDataSource.deleteLike(this, memories.getId(), user_id, memories.getMemType());
                }
                break;

            case HelpMe.TYPE_ADD_BUDDY:
                String buddyId = bundle.getString("added_buddy_id");
                journeyId = bundle.getString("journey_id");
                ContactsUtil.fetchContact(this, buddyId);
                JourneyDataSource.addContactToJourney(this, buddyId, journeyId);
                journey = JourneyDataSource.getJourneyById(this, journeyId);
                message = "A friend has been removed from the journey " + journey.getName();
                showNotification(message, ActivejourneyList.class);
                break;

            case HelpMe.TYPE_REMOVE_BUDDY:
                buddyId = bundle.getString("removed_buddy_id");
                journeyId = bundle.getString("journey_id");
                if(buddyId.equals(TJPreferences.getUserId(this))){
                    JourneyDataSource.updateUserActiveStatus(this, journeyId, false);
                    /*if(CurrentJourneyBaseActivity.isActivityVisible()){
                        CurrentJourneyBaseActivity.getInstance().refreshTimelineFragment();
                    }*/
                }
                journey = JourneyDataSource.getJourneyById(this, journeyId);
                message = "A new friend has been added to the journey " + journey.getName();
                showNotification(message, ActivejourneyList.class);

                break;

            case HelpMe.TYPE_PROFILE_UPDATE:
                JSONObject obj;
                try {
                    obj = new JSONObject(bundle.getString("user"));
                    Log.d(TAG, "contact fetched with server id " + obj);
                    userId = obj.getString("id");
                    Contact contact = ContactDataSource.getContactById(this, userId);
                    contact.setProfileName(obj.getString("name"));
                    contact.setStatus(obj.getString("status"));
                    contact.setPhoneNo(obj.getString("phone"));
                    boolean profilePicUpdated = Boolean.parseBoolean(obj.getString("is_profile_pic_change"));
                    if(profilePicUpdated){
                        String picUrl = obj.getString("profile_picture");
                        String profilePicPath = Constants.TRAVELJAR_FOLDER_BUDDY_PROFILES + contact.getIdOnServer() + ".jpeg";
                        if(ContactsUtil.fetchProfilePicture(this, picUrl, profilePicPath)){
                            contact.setPicLocalUrl(profilePicPath);
                            contact.setPicServerUrl(picUrl);
                        }
                    }
                    Log.d(TAG, "contact fetched ->" + contact);
                    ContactDataSource.updateContact(this, contact);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                break;
            case HelpMe.TYPE_END_JOURNEY:
                journeyId = bundle.getString("journey_id");
                JourneyDataSource.updateJourneyStatus(this, journeyId, Constants.JOURNEY_STATUS_FINISHED);
                /*if(CurrentJourneyBaseActivity.isActivityVisible()){
                    CurrentJourneyBaseActivity.getInstance().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CurrentJourneyBaseActivity.getInstance().endJourney(journeyId);
                        }
                    });
                }*/
                if(ActivejourneyList.isActivityVisible()){
                    ActivejourneyList.getInstance().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ActivejourneyList.getInstance().refreshJourneysList();
                        }
                    });
                }
                message = "Your journey has been marked finished by the admin. We'll get back to you with a video of the same very soon";
                showNotification(message, ActivejourneyList.class);
                break;

            case HelpMe.TYPE_TIMECAPSULE_CREATE:
                journeyId = bundle.getString("journey_id");
                String timecapsuleURL = bundle.getString("timecap_video");
                String idOnServer = "123";
                long dummyText = Long.parseLong("111");

                Timecapsule newTimecapsule = new Timecapsule(idOnServer, journeyId, "", timecapsuleURL,
                        "", "", "mp4", dummyText, "abhi", dummyText, dummyText);

                DownloadTimeCapsuleAsyncTask asyncTask = new DownloadTimeCapsuleAsyncTask(newTimecapsule, this);
                asyncTask.execute();

            default:
                break;
        }
    }

    private void createMemory(String jId, int memType, JSONObject data)
            throws NumberFormatException, JSONException {
        Log.d(TAG, "createMemory called");
        String idOnServer = data.getString("memory_id");
        String createdBy = data.getString("created_by");
        long createdAt = Long.parseLong(data.getString("created_at"));
        long updatedAt = Long.parseLong(data.getString("updated_at"));
        String dataUrl;
        long size;
        String extension;
        String caption;
        String content;
        String mood;
        List<String> buddyIds;
        String reason;
        Double latitude = data.getString("latitude").equals("null") ? 0.0d : Double.parseDouble(data.getString("latitude"));
        Double longitude = data.getString("longitude").equals("null") ? 0.0d : Double.parseDouble(data.getString("longitude"));
        long audioDuration;

        switch (memType) {
            case HelpMe.SERVER_PICTURE_TYPE:
                Log.d(TAG, "its picture type with idOnServer = " + idOnServer);
                dataUrl = data.getString("data_url");
                String thumb = data.getString("thumb");
                size = Long.parseLong(data.getString("size"));
                extension = data.getString("extension");
                caption = data.getString("caption").equals("null") ? null : data.getString("caption");

                Log.d(TAG, "caption is " + caption);

                Picture newPic = new Picture(idOnServer, jId, HelpMe.PICTURE_TYPE, caption, extension,
                        size, dataUrl, null, createdBy,
                        createdAt, updatedAt, null, null, latitude, longitude);
                // No need to catch the event hence send -1
                PictureUtilities.getInstance().createNewPicFromServer(this, newPic, thumb, -1);
                break;

            case HelpMe.SERVER_AUDIO_TYPE:
                Log.d(TAG, "its audio type with idOnServer = " + idOnServer);
                dataUrl = data.getString("data_url");
                size = Long.parseLong(data.getString("size"));
                extension = data.getString("extention");
                audioDuration = data.getString("duration").equals("null") ? 0 : Long.parseLong(data.getString("duration"));

                Audio newAudio = new Audio(idOnServer, jId, HelpMe.AUDIO_TYPE, extension, size,
                        dataUrl, null, createdBy, createdAt, updatedAt, null, audioDuration, latitude, longitude);

                AudioDataSource.createAudio(newAudio, this);
                break;

            case HelpMe.SERVER_VIDEO_TYPE:
                Log.d(TAG, "its video type with idOnServer = " + idOnServer);
                dataUrl = data.getString("data_url");
                String localThumbUrl = data.getString("thumbnail");
                size = Long.parseLong(data.getString("size"));
                extension = data.getString("extension");
                caption = data.getString("caption").equals("null") ? null : data.getString("caption");

                Video newVideo = new Video(idOnServer, jId, HelpMe.VIDEO_TYPE, caption, extension,
                        size, dataUrl, null, createdBy, createdAt, updatedAt, null, null, latitude, longitude);
                //Downloading video and save to database
                // No need to catch the event hence send -1
                VideoUtil.getInstance().createNewVideoFromServer(this, newVideo, localThumbUrl, -1);
                break;

            case HelpMe.SERVER_NOTE_TYPE:
                Log.d(TAG, "its note type with idOnServer = " + idOnServer);
                content = data.getString("content");
                caption = data.getString("caption");

                Note newNote = new Note(idOnServer, jId, HelpMe.NOTE_TYPE, caption, content, createdBy,
                        createdAt, updatedAt, null, latitude, longitude);

                NoteDataSource.createNote(newNote, this);
                break;

            case HelpMe.SERVER_MOOD_TYPE:
                Log.d(TAG, "its mood type with idOnServer = " + idOnServer);
                mood = data.getString("mood");
                reason = data.getString("reason");

                buddyIds = new ArrayList<>();
                String array[] = data.getString("buddies").split(",");
                for (String s : array) {
                    buddyIds.add(s);
                }

                Mood newMood = new Mood(idOnServer, jId, HelpMe.MOOD_TYPE, buddyIds, mood, reason,
                        createdBy, createdAt, updatedAt, null, latitude, longitude);

                MoodDataSource.createMood(newMood, this);
                break;

            case HelpMe.SERVER_CHECKIN_TYPE:

                String buddies = data.getString("buddies");
                buddies = buddies.replace("[", "");
                buddies = buddies.replace("]", "");
                List<String> buddyList = Arrays.asList(buddies.split(","));
                String place_name = data.getString("place_name");
                caption = data.getString("caption").equals("null") ? null : data.getString("caption");
                thumb = data.getString("thumb");
                dataUrl = data.getString("data_url");

                CheckIn newCheckIn = new CheckIn(idOnServer, jId, HelpMe.CHECKIN_TYPE, caption, latitude, longitude,
                        place_name, null, dataUrl, thumb, buddyList, createdBy, createdAt, updatedAt);

                CheckinUtil.createNewCheckInFromServer(this, newCheckIn, -1);
                //CheckinDataSource.createCheckIn(newCheckIn, this);
                break;

            default:
                break;
        }
    }

    @Override
    public void onFinishTask(Journey journey) {
        JourneyDataSource.createJourney(journey, this);
        if (ActivejourneyList.isActivityVisible()) {
            ActivejourneyList.getInstance().refreshJourneysList();
        }
    }

    private void showNotification(String message, Class _activity){
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                _activity), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher).setContentTitle("GCM Notification")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message)).setContentText(message);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

}