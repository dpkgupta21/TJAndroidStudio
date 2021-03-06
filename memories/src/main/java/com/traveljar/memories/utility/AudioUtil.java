package com.traveljar.memories.utility;

import android.content.Context;
import android.util.Log;

import com.traveljar.memories.SQLitedatabase.AudioDataSource;
import com.traveljar.memories.models.Audio;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.File;

public class AudioUtil {

    private static final String TAG = "AUDIO_UTIL";

    public static boolean uploadAudioOnServer(Context context, Audio audio){
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        entityBuilder.addPart("audio[audio_file]", new FileBody(new File(audio.getDataLocalURL())));
        entityBuilder.addTextBody("audio[user_id]", audio.getCreatedBy());
        entityBuilder.addTextBody("api_key", TJPreferences.getApiKey(context));
        entityBuilder.addTextBody("audio[duration]", String.valueOf(audio.getAudioDuration()));
        entityBuilder.addTextBody("audio[latitude]", String.valueOf(audio.getLatitude()));
        entityBuilder.addTextBody("audio[longitude]", String.valueOf(audio.getLongitude()));
        entityBuilder.addTextBody("audio[created_at]", String.valueOf(audio.getCreatedAt()));
        entityBuilder.addTextBody("audio[updated_at]", String.valueOf(audio.getUpdatedAt()));


        String url = Constants.URL_MEMORY_UPLOAD + TJPreferences.getActiveJourneyId(context) + "/audios";
        HttpPost updateProfileRequest = new HttpPost(url);
        updateProfileRequest.setEntity(entityBuilder.build());
        HttpResponse response;
        try {
            response = new DefaultHttpClient().execute(updateProfileRequest);
            JSONObject object = new JSONObject(EntityUtils.toString(response.getEntity()));
            Log.d(TAG, "response on uploading audio" + object);

            String serverId = object.getJSONObject("audio").getString("id");
            String serverUrl = object.getJSONObject("audio").getJSONObject("audio_file").getString("url");
            AudioDataSource.updateServerIdAndUrl(context, audio.getId(), serverId, serverUrl);
            Log.d(TAG, "audio successfully uploaded and serverid successfully saved in database");
            return true;
        } catch (Exception e) {
            Log.d(TAG, "error in uploading audio" + e.getMessage());
            return false;
        }
    }

}
