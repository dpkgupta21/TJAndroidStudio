package com.traveljar.memories.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.traveljar.memories.SQLitedatabase.VideoDataSource;
import com.traveljar.memories.customevents.VideoDownloadEvent;
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
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

public class VideoUtil {

    private static VideoUtil instance;

    public static VideoUtil getInstance(){
        if(instance == null)
            instance = new VideoUtil();
        return instance;
    }

    public static final String TAG = "VIDEO_UTIL";

    public void createNewVideoFromServer(final Context context, final Video video, String thumbUrl, final int downloadRequesterCode) {
        final String imagePath = Constants.TRAVELJAR_FOLDER_VIDEO + "thumb_" + TJPreferences.getUserId(context) + "_"+ video.getjId()
                + "_"+ video.getCreatedAt() + ".jpg";
        Log.d(TAG, "creating new video from server" + imagePath);
        if(!(new File(imagePath).exists())) {
            Log.d(TAG, "video thumbnail not presend hence downloading");
            if (thumbUrl != null) {
                ImageRequest request = new ImageRequest(thumbUrl, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        FileOutputStream out = null;
                        try {
                            out = new FileOutputStream(imagePath);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            video.setLocalThumbPath(imagePath);
                            long id = VideoDataSource.createVideo(video, context);
                            video.setId(String.valueOf(id));
                            Log.d(TAG, "video downloaded successfully");
                            PullMemoriesService.isFinished();
                            EventBus.getDefault().post(new VideoDownloadEvent(video, true, downloadRequesterCode));
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
                    }
                }, 0, 0, null, new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        EventBus.getDefault().post(new VideoDownloadEvent(video, false, downloadRequesterCode));
                        Log.d(TAG, "error occured" + error.getMessage());
                    }
                });
                request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                AppController.getInstance().addToRequestQueue(request);
            }
        }else {
            Log.d(TAG, "video thumbnail already presend");
            video.setLocalThumbPath(imagePath);
            long id = VideoDataSource.createVideo(video, context);
            video.setId(String.valueOf(id));
            PullMemoriesService.isFinished();
            EventBus.getDefault().post(new VideoDownloadEvent(video, true, downloadRequesterCode));
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
        entityBuilder.addTextBody("video[description]", video.getCaption());

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