package com.example.memories.utility;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.memories.SQLitedatabase.AudioDataSource;
import com.example.memories.models.Audio;

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

public class AudioUtil {

    private static final String TAG = "AUDIO_UTIL";

/*    public static void downloadAudio(Context context, Audio audio) {
        DownloadTask task = (new AudioUtil()).new DownloadTask(context, audio);
        task.execute(audio.getDataServerURL());
    }*/

    public static void uploadAudio(final Context context, final Audio audio) {

        UploadAsyncTask task = (new AudioUtil()).new UploadAsyncTask(context, audio);
        task.execute();

    }


    private class UploadAsyncTask extends AsyncTask<String, Void, JSONObject> {

        Context context;
        Audio audio;

        public UploadAsyncTask(Context context, Audio audio) {
            this.context = context;
            this.audio = audio;
        }

        @Override
        protected JSONObject doInBackground(String... maps) {
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            entityBuilder.addPart("audio[audio_file]", new FileBody(new File(audio.getDataLocalURL())));
            entityBuilder.addTextBody("audio[user_id]", audio.getCreatedBy());
            entityBuilder.addTextBody("api_key", TJPreferences.getApiKey(context));
            entityBuilder.addTextBody("audio[duration]", String.valueOf(audio.getAudioDuration()));
            entityBuilder.addTextBody("audio[latitude]", String.valueOf(audio.getLatitude()));
            entityBuilder.addTextBody("audio[longitude]", String.valueOf(audio.getLongitude()));

            String url = Constants.URL_MEMORY_UPLOAD + TJPreferences.getActiveJourneyId(context) + "/audios";
            HttpPost updateProfileRequest = new HttpPost(url);
            updateProfileRequest.setEntity(entityBuilder.build());
            HttpResponse response;
            try {
                response = new DefaultHttpClient().execute(updateProfileRequest);
                JSONObject object = new JSONObject(EntityUtils.toString(response.getEntity()));
                Log.d(TAG, "response on uploading audio" + object);
                return object;
            } catch (Exception e) {
                Log.d(TAG, "error in uploading audio" + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject object) {
            if (object != null) {
                try {
                    Log.d(TAG, "onPostExecute()");
                    String serverId = object.getJSONObject("audio").getString("id");
                    String serverUrl = object.getJSONObject("audio")
                            .getJSONObject("audio_file").getString("url");
                    AudioDataSource.updateServerIdAndUrl(context, audio.getId(), serverId,
                            serverUrl);
                    Log.d(TAG, "audio successfully uploaded and serverid successfully saved in database");
                } catch (JSONException ex) {
                    Log.d(TAG, ex.getMessage());
                }
            }
        }
    }

}
