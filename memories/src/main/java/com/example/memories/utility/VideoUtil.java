package com.example.memories.utility;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.memories.SQLitedatabase.VideoDataSource;
import com.example.memories.models.Video;

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

public class VideoUtil {

    public static final String TAG = "VIDEO_UTIL";

    public static void downloadVideo(Context context, Video video) {
        DownloadTask task = (new VideoUtil()).new DownloadTask(context, video);
        task.execute(video.getDataServerURL());
    }

    public static void uploadVideo(final Context context, final Video video) {
        UploadAsyncTask task = (new VideoUtil()).new UploadAsyncTask(context, video);
        task.execute();
    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {

        Context context;
        Video video;

        public DownloadTask(Context context, Video video) {
            this.context = context;
            this.video = video;
        }

        @Override
        protected String doInBackground(String... url) {
            Log.d(TAG, "inside download task do in background");
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            String fileLocation = Constants.TRAVELJAR_FOLDER_VIDEO
                    + System.currentTimeMillis() + ".mp4";
            URL downloadUrl;
            try {
                downloadUrl = new URL(url[0]);
                Log.d(TAG, "started downloading");
                connection = (HttpURLConnection) downloadUrl.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report instead of the file
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
                video.setDataLocalURL(fileLocation);
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
                VideoDataSource.createVideo(video, context);
            }
        }
    }

    private class UploadAsyncTask extends AsyncTask<String, Void, JSONObject> {

        Context context;
        Video video;

        public UploadAsyncTask(Context context, Video video) {
            this.context = context;
            this.video = video;
        }

        @Override
        protected JSONObject doInBackground(String... maps) {
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            entityBuilder.addPart("video[video_file]", new FileBody(new File(video.getDataLocalURL())));
            entityBuilder.addTextBody("video[user_id]", video.getCreatedBy());
            entityBuilder.addTextBody("api_key", TJPreferences.getApiKey(context));

            String url = "https://www.traveljar.in/api/v1/journeys/" + TJPreferences.getActiveJourneyId(context) + "/videos";
            HttpPost updateProfileRequest = new HttpPost(url);
            updateProfileRequest.setEntity(entityBuilder.build());
            HttpResponse response;
            try {
                response = new DefaultHttpClient().execute(updateProfileRequest);
                JSONObject object = new JSONObject(EntityUtils.toString(response.getEntity()));
                Log.d(TAG, "response on uploading video" + object);
                return object;
            } catch (Exception e) {
                Log.d(TAG, "error in uploading video" + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject object) {
            try {
                Log.d(TAG, "onPostExecute()");
                String serverId = object.getJSONObject("video").getString("id");
                String serverUrl = object.getJSONObject("video")
                        .getJSONObject("video_file").getString("url");
                VideoDataSource.updateServerIdAndUrl(context, video.getId(), serverId,
                        serverUrl);
                Log.d(TAG, "video successfully uploaded and serverid successfully saved in database");
            } catch (JSONException ex) {
                Log.d(TAG, ex.getMessage());
            }
        }
    }

}


/*RestAdapter restAdapter = new RestAdapter.Builder().setConverter(new StringConverter())
                .setEndpoint(Constants.TRAVELJAR_API_BASE_URL).build();
		TravelJarServices myService = restAdapter.create(TravelJarServices.class);

		myService.uploadVideo(TJPreferences.getActiveJourneyId(context), new TypedString(
				TJPreferences.getApiKey(context)),
				new TypedString(TJPreferences.getUserId(context)), new TypedFile("video/*",
						new File(video.getDataLocalURL())), new Callback<String>() {
					@Override
					public void success(String str, retrofit.client.Response response) {
						try {
							Log.d(TAG, "video uploaded successfully " + str);
							JSONObject object = new JSONObject(str);
							String serverId = object.getJSONObject("video").getString("id");
							String serverUrl = object.getJSONObject("video")
									.getJSONObject("video_file").getString("url");
							VideoDataSource.updateServerIdAndUrl(context, video.getId(), serverId,
									serverUrl);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void failure(RetrofitError retrofitError) {
						Log.d(TAG, "error in uploading video" + retrofitError);
						retrofitError.printStackTrace();
					}
				});*/