package com.traveljar.memories.utility;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.traveljar.memories.SQLitedatabase.VideoDataSource;
import com.traveljar.memories.models.Video;
import com.traveljar.memories.services.PullMemoriesService;
import com.traveljar.memories.volley.AppController;
import com.traveljar.memories.volley.CustomJsonRequest;

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
import java.util.HashMap;
import java.util.Map;

public class VideoUtil {

    public static final String TAG = "VIDEO_UTIL";

    public static void downloadAndPlayVideo(Context context, Video video) {
        DownloadTask task = (new VideoUtil()).new DownloadTask(context, video);
        task.execute(video.getDataServerURL());
    }

    public static void uploadVideo(final Context context, final Video video) {
        UploadAsyncTask task = (new VideoUtil()).new UploadAsyncTask(context, video);
        task.execute();
    }

    public static void createNewVideoFromServer(final Context context, final Video video, String thumbUrl) {
        final String imagePath = Constants.TRAVELJAR_FOLDER_VIDEO + "/vid_" + System.currentTimeMillis() + ".jpg";
        if (thumbUrl != null) {
            ImageRequest request = new ImageRequest(thumbUrl, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap bitmap) {
                    FileOutputStream out = null;
                    try {
                        out = new FileOutputStream(imagePath);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        video.setLocalThumbPath(imagePath);
                        VideoDataSource.createVideo(video, context);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (out != null) {
                                out.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    PullMemoriesService.isFinished();
                }
            }, 0, 0, null, new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                }
            });
            request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().addToRequestQueue(request);
        }
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
                VideoDataSource.updateVideoLocalUrl(context, video.getId(), video.getDataLocalURL());
                Intent mediaIntent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(new File(video.getDataLocalURL())));
                mediaIntent.setDataAndType(Uri.fromFile(new File(video.getDataLocalURL())), "video/*");
                context.startActivity(mediaIntent);
//                VideoDataSource.createVideo(video, context);
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
            entityBuilder.addTextBody("video[latitude]", String.valueOf(video.getLatitude()));
            entityBuilder.addTextBody("video[longitude]", String.valueOf(video.getLongitude()));

            String url = Constants.URL_MEMORY_UPLOAD + TJPreferences.getActiveJourneyId(context) + "/videos";
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
                Log.d(TAG, "onPostExecute()" + object);
                String serverId = object.getJSONObject("video").getString("id");
                String serverUrl = object.getJSONObject("video")
                        .getJSONObject("video_file").getString("url");
                VideoDataSource.updateServerIdAndUrl(context, video.getId(), serverId,
                        serverUrl);
                Log.d(TAG, "video successfully uploaded and serverid successfully saved in database");
            } catch (JSONException ex) {
                Log.d(TAG, ex.getMessage());
            } catch (NullPointerException ex) {
                Log.d(TAG, "null pointer exception");
            }
        }
    }

    public static boolean uploadVideoOnServer(Context context, Video video){
        Log.d(TAG, "uploading video on server");
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        entityBuilder.addPart("video[video_file]", new FileBody(new File(video.getDataLocalURL())));
        entityBuilder.addTextBody("video[user_id]", video.getCreatedBy());
        entityBuilder.addTextBody("api_key", TJPreferences.getApiKey(context));
        entityBuilder.addTextBody("video[latitude]", String.valueOf(video.getLatitude()));
        entityBuilder.addTextBody("video[longitude]", String.valueOf(video.getLongitude()));
        entityBuilder.addTextBody("video[created_at]", String.valueOf(video.getCreatedAt()));
        entityBuilder.addTextBody("video[updated_at]", String.valueOf(video.getUpdatedAt()));

        String url = Constants.URL_MEMORY_UPLOAD + TJPreferences.getActiveJourneyId(context) + "/videos";
        Log.d(TAG, "upload Url");
        HttpPost updateProfileRequest = new HttpPost(url);
        updateProfileRequest.setEntity(entityBuilder.build());
        HttpResponse response;
        try {
            response = new DefaultHttpClient().execute(updateProfileRequest);
            JSONObject object = new JSONObject(EntityUtils.toString(response.getEntity()));
            Log.d(TAG, "response on uploading video" + object);
            String serverId = object.getJSONObject("video").getString("id");
            String serverUrl = object.getJSONObject("video")
                    .getJSONObject("video_file").getString("url");
            VideoDataSource.updateServerIdAndUrl(context, video.getId(), serverId, serverUrl);
            return true;
        } catch (Exception e) {
            Log.d(TAG, "error in uploading video" + e.getMessage());
            return false;
        }
    }

    public static void updateCaption(final Video video, final String caption, final Context context){
        if(!HelpMe.isNetworkAvailable(context)){
            Toast.makeText(context, "Network unavailable please try after some time", Toast.LENGTH_SHORT).show();
        }else {
            String url = Constants.URL_MEMORY_UPDATE + TJPreferences.getActiveJourneyId(context) + "/videos/" + video.getIdOnServer();
            Map<String, String> params = new HashMap<>();
            params.put("api_key", TJPreferences.getApiKey(context));
            params.put("video[description]", TJPreferences.getApiKey(context));
            CustomJsonRequest uploadRequest = new CustomJsonRequest(Request.Method.PUT, url, params,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, "picture caption updated successfully" + response);
                            VideoDataSource.updateCaption(context, caption, video.getId());
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "error in updating picture caption" + error);
                    error.printStackTrace();
                }
            });
            AppController.getInstance().addToRequestQueue(uploadRequest);
        }
    }

}