package com.example.memories.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.memories.R;
import com.example.memories.SQLitedatabase.AudioDataSource;
import com.example.memories.SQLitedatabase.JourneyDataSource;
import com.example.memories.SQLitedatabase.MoodDataSource;
import com.example.memories.SQLitedatabase.NoteDataSource;
import com.example.memories.models.Audio;
import com.example.memories.models.Journey;
import com.example.memories.models.Mood;
import com.example.memories.models.Note;
import com.example.memories.models.Picture;
import com.example.memories.currentjourney.TimelineFragment;
import com.example.memories.utility.Constants;
import com.example.memories.utility.HelpMe;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmIntentService extends IntentService {
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
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " + extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                Log.d(TAG, "something recieved =" + extras.toString());

                parseGcmMessage(extras);

                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                sendNotification("Received: " + extras.toString());
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                TimelineFragment.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher).setContentTitle("GCM Notification")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg)).setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void parseGcmMessage(Bundle bundle) {
        Log.d(TAG, " parseGcmMessage called");
        String type = bundle.get("type").toString();
        String journeyId;

        switch (Integer.parseInt(type)) {
            case HelpMe.TYPE_CREATE_MEMORY:
                String memType = bundle.get("memory_type").toString();
                String data = bundle.get("details").toString();
                journeyId = bundle.getString("journey_id");
                try {
                    Log.d(TAG, "type = create , so createMemory called");
                    createMemory(journeyId, Integer.parseInt(memType), new JSONObject(data));
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;

            case HelpMe.TYPE_CREATE_JOURNEY:
                Log.d(TAG, "type = create journey");

                journeyId = bundle.get("id").toString();
                Log.d(TAG, "bundle buddy ids are " + bundle.get("buddy_ids"));
                String buddyIds = (String) bundle.get("buddy_ids");
                buddyIds = buddyIds.replace("[", "");
                buddyIds = buddyIds.replace("]", "");


                Journey newJ = new Journey(journeyId, "deafault", "tagline", "Friends", "90", null, Arrays.asList(buddyIds), Constants.JOURNEY_STATUS_PENDING);
                JourneyDataSource.createJourney(newJ, this);

            default:
                break;
        }
    }

    private void createMemory(String jId, int memType, JSONObject data)
            throws NumberFormatException, JSONException {
        Log.d(TAG, "createMemory called");
        String idOnServer = data.getString("id");
        String createdBy = data.getString("created_by");
        Long createdAt = HelpMe.getCurrentTime();
        Long updatedAt = HelpMe.getCurrentTime();
        // Long createdAt = Long.parseLong(data.getString("created_at"));
        // Long updatedAt = Long.parseLong(data.getString("updated_at"));
        String dataUrl;
        long size;
        String extension;
        String caption;
        String content;
        String mood;
        List<String> buddyIds;
        String reason;

        switch (memType) {
            case HelpMe.TYPE_PICTURE:
                Log.d(TAG, "its picture type with idOnServer = " + idOnServer);
                dataUrl = data.getString("data_url");
                size = Long.parseLong(data.getString("size"));
                extension = data.getString("extention");
                caption = "nthing";

                Picture newPic = new Picture(idOnServer, jId, HelpMe.PICTURE_TYPE, caption, extension,
                        size, dataUrl, null, createdBy,
                        createdAt, updatedAt, null, null);

                //PictureUtilities.downloadPicFromURL(this, newPic);
                //PictureDataSource.createPicture(newPic, this);
                break;

            case HelpMe.TYPE_AUDIO:
                Log.d(TAG, "its audio type with idOnServer = " + idOnServer);
                dataUrl = data.getString("data_url");
                size = Long.parseLong(data.getString("size"));
                extension = data.getString("extention");

                Audio newAudio = new Audio(idOnServer, jId, HelpMe.AUDIO_TYPE, extension, size,
                        dataUrl, null, createdBy, createdAt, updatedAt, null);

                AudioDataSource.createAudio(newAudio, this);
                //AudioUtil.downloadAudio(this, newAudio);
                break;

/*            case HelpMe.TYPE_VIDEO:
                Log.d(TAG, "its video type with idOnServer = " + idOnServer);
                dataUrl = data.getString("data_url");
                size = Long.parseLong(data.getString("size"));
                extension = data.getString("extention");
                caption = data.getString("caption");

                Video newVideo = new Video(idOnServer, jId, HelpMe.VIDEO_TYPE, caption, extension,
                        size, dataUrl, null, createdBy, createdAt, updatedAt, null, null);
                //Downloading video and save to database
                VideoUtil.createNewVideoFromServer(this, newVideo);
                break;*/

            case HelpMe.TYPE_NOTE:
                Log.d(TAG, "its note type with idOnServer = " + idOnServer);
                content = data.getString("content");
                caption = data.getString("caption");

                Note newNote = new Note(idOnServer, jId, HelpMe.NOTE_TYPE, caption, content, createdBy,
                        createdAt, updatedAt, null);

                NoteDataSource.createNote(newNote, this);
                break;

            case HelpMe.TYPE_MOOD:
                Log.d(TAG, "its mood type with idOnServer = " + idOnServer);
                mood = data.getString("mood");
                reason = data.getString("reason");

                buddyIds = new ArrayList<String>();
                String array[] = data.getString("buddy_ids").split(",");
                for (String s : array) {
                    buddyIds.add(s);
                }

                Mood newMood = new Mood(idOnServer, jId, HelpMe.MOOD_TYPE, buddyIds, mood, reason,
                        createdBy, createdAt, updatedAt, null);

                MoodDataSource.createMood(newMood, this);
                break;

            default:
                break;
        }
    }
}