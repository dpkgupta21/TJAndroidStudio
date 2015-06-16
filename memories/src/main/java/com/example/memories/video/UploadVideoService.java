package com.example.memories.video;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.example.memories.SQLitedatabase.VideoDataSource;
import com.example.memories.models.Video;
import com.example.memories.utility.Constants;
import com.example.memories.utility.TJPreferences;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by ankit on 17/6/15.
 */
public class UploadVideoService extends IntentService{
    private Video video;

    private static final String TAG = "UploadVideoService";

    public UploadVideoService() {
        super("UploadVideoService");
    }

    public UploadVideoService(String name) {
        super(name);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, startId, startId);
        video = intent.getParcelableExtra("VIDEO");
        Log.d(TAG, "upload video service has started for picture" + video.getId());
        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        Log.d(TAG, "on Handle Intent");
        uploadVideo();
    }

    private void uploadVideo(){
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        entityBuilder.addPart("video[video_file]", new FileBody(new File(video.getDataLocalURL())));
        entityBuilder.addTextBody("video[user_id]", video.getCreatedBy());
        entityBuilder.addTextBody("api_key", TJPreferences.getApiKey(this));
        entityBuilder.addTextBody("video[latitude]", String.valueOf(video.getLatitude()));
        entityBuilder.addTextBody("video[longitude]", String.valueOf(video.getLongitude()));

        String url = Constants.URL_MEMORY_UPLOAD + TJPreferences.getActiveJourneyId(this) + "/videos";
        HttpPost updateProfileRequest = new HttpPost(url);
        updateProfileRequest.setEntity(entityBuilder.build());
        HttpResponse response;
        try {
            response = new DefaultHttpClient().execute(updateProfileRequest);
            JSONObject object = new JSONObject(EntityUtils.toString(response.getEntity()));
            Log.d(TAG, "response on uploading video" + object);
            parseAndUpdateVideo(object);
        } catch (Exception e) {
            Log.d(TAG, "error in uploading video" + e.getMessage());
        }
    }

    private void parseAndUpdateVideo(JSONObject object){
        try {
            Log.d(TAG, "onPostExecute()" + object);
            String serverId = object.getJSONObject("video").getString("id");
            String serverUrl = object.getJSONObject("video")
                    .getJSONObject("video_file").getString("url");
            VideoDataSource.updateServerIdAndUrl(this, video.getId(), serverId, serverUrl);
            Log.d(TAG, "video successfully uploaded and serverid successfully saved in database");
        } catch (JSONException ex) {
            Log.d(TAG, ex.getMessage());
        } catch (NullPointerException ex) {
            Log.d(TAG, "null pointer exception");
        }
    }
}
