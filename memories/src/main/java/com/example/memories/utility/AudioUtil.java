package com.example.memories.utility;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AudioUtil {

    private static final String TAG = "AUDIO_UTIL";

    public static void downloadAudio(Context context, Audio audio) {
        DownloadTask task = (new AudioUtil()).new DownloadTask(context, audio);
        task.execute(audio.getDataServerURL());
    }

    public static void uploadAudio(final Context context, final Audio audio) {

        UploadAsyncTask task = (new AudioUtil()).new UploadAsyncTask(context, audio);
        task.execute();

    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {

        Context context;
        Audio audio;

        public DownloadTask(Context context, Audio audio) {
            this.context = context;
            this.audio = audio;
        }

        @Override
        protected String doInBackground(String... url) {
            Log.d(TAG, "inside download task do in background");
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            String fileLocation = Constants.TRAVELJAR_FOLDER_AUDIO + System.currentTimeMillis()
                    + ".mp3";
            URL downloadUrl;
            try {
                downloadUrl = new URL(url[0]);
                Log.d(TAG, "started downloading");
                connection = (HttpURLConnection) downloadUrl.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode() + " "
                            + connection.getResponseMessage();
                }

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(fileLocation);

                byte data[] = new byte[4096];
                int count;
                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                }
                audio.setDataLocalURL(fileLocation);
                Log.d(TAG, "download finished");
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                AudioDataSource.createAudio(audio, context);
            }
        }
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

            String url = "http://192.168.1.2:3000/api/v1/journeys/" + TJPreferences.getActiveJourneyId(context) + "/audios";
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

    // No separate thread
    public static String saveAudio(Context context, Audio audio){
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        String fileLocation = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath() + "/aud_"+ System.currentTimeMillis()
                + ".mp3";
        URL downloadUrl;
        try {
            downloadUrl = new URL(audio.getDataServerURL());
            Log.d(TAG, "started downloading audio ");
            connection = (HttpURLConnection) downloadUrl.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode() + " "
                        + connection.getResponseMessage();
            }

            // download the file
            input = connection.getInputStream();
            output = new FileOutputStream(fileLocation);

            byte data[] = new byte[4096];
            int count;
            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
            }
            audio.setDataLocalURL(fileLocation);
            Log.d(TAG, "download finished");
            AudioDataSource.updateDataLocalUrl(context, audio.getId(), fileLocation);
            Log.d(TAG, "updated audio local url in database successfully");
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return fileLocation;
    }

}

/*RestAdapter restAdapter = new RestAdapter.Builder().setConverter(new StringConverter())
                .setEndpoint(Constants.TRAVELJAR_API_BASE_URL).build();
		TravelJarServices myService = restAdapter.create(TravelJarServices.class);

		myService.uploadAudio(TJPreferences.getActiveJourneyId(context), new TypedString(
				TJPreferences.getApiKey(context)),
				new TypedString(TJPreferences.getUserId(context)), new TypedFile("audio/*",
						new File(audio.getDataLocalURL())), new Callback<String>() {
					@Override
					public void success(String str, retrofit.client.Response response) {
						Log.d(TAG, "audio uploaded successfully " + str);
						JSONObject object;
						try {
							object = new JSONObject(str);
							String serverId = object.getJSONObject("audio").getString("id");
							String serverUrl = object.getJSONObject("audio")
									.getJSONObject("audio_file").getString("url");
							Log.d(TAG, "audio uploaded successfully " + serverId + serverUrl);
							AudioDataSource.updateServerIdAndUrl(context, audio.getId(), serverId,
									serverUrl);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void failure(RetrofitError retrofitError) {
						Log.d(TAG, "error in uploading audio " + retrofitError);
						retrofitError.printStackTrace();
					}
				});*/