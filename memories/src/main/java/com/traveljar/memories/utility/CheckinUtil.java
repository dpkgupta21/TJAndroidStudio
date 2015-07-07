package com.traveljar.memories.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.traveljar.memories.SQLitedatabase.CheckinDataSource;
import com.traveljar.memories.customevents.CheckInDownloadEvent;
import com.traveljar.memories.models.CheckIn;
import com.traveljar.memories.services.PullMemoriesService;
import com.traveljar.memories.volley.AppController;

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

import de.greenrobot.event.EventBus;

public class CheckinUtil {
    private static final String TAG = "CHECKIN_UTIL";

/*    public static void uploadCheckin(final CheckIn checkin, final Context context) {

        String uploadRequestTag = "UPLOAD_CHECKIN";
        Map<String, String> params = new HashMap<>();
        params.put("api_key", TJPreferences.getApiKey(context));
        params.put("checkin[user_id]", checkin.getCreatedBy());
        params.put("checkin[place_name]", checkin.getCheckInPlaceName());
        params.put("checkin[latitude]", String.valueOf(checkin.getLatitude()));
        params.put("checkin[longitude]", String.valueOf(checkin.getLongitude()));
        params.put("checkin[buddies]", checkin.getCheckInWith() == null ? null : checkin.getCheckInWith().toString());
        params.put("checkin[note]", checkin.getCaption());
        params.put("checkin[created_at]", String.valueOf(checkin.getCreatedAt()));
        params.put("checkin[updated_at]", String.valueOf(checkin.getUpdatedAt()));
        Log.d(TAG, "uploading checkin with parameters " + params);

        String url = Constants.URL_MEMORY_UPLOAD + TJPreferences.getActiveJourneyId(context) + "/checkins";
        Log.d(TAG, "uploading checkin on url " + url);

        CustomJsonRequest uploadRequest = new CustomJsonRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "checkin uploaded successfully" + response);
                        try {
                            String serverId = response.getJSONObject("checkin").getString("id");
                            CheckinDataSource.updateServerId(context.getApplicationContext(), checkin.getId(), serverId);
                        } catch (Exception ex) {
                            Log.d(TAG, "exception in parsing checkin received from server" + ex);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "error in uploading checkin");
                error.printStackTrace();
            }
        });
        AppController.getInstance().addToRequestQueue(uploadRequest, uploadRequestTag);
    }*/

    /*public static boolean uploadCheckInOnServer(Context context, CheckIn checkIn){
        String url = Constants.URL_MEMORY_UPLOAD + TJPreferences.getActiveJourneyId(context) + "/checkins";
        Map<String, String> params = new HashMap<>();
        params.put("api_key", TJPreferences.getApiKey(context));
        params.put("checkin[user_id]", checkIn.getCreatedBy());
        params.put("checkin[place_name]", checkIn.getCheckInPlaceName());
        params.put("checkin[latitude]", String.valueOf(checkIn.getLatitude()));
        params.put("checkin[longitude]", String.valueOf(checkIn.getLongitude()));
        params.put("checkin[buddies]", checkIn.getCheckInWith() == null ? null : checkIn.getCheckInWith().toString());
        params.put("checkin[note]", checkIn.getCaption());
        params.put("checkin[created_at]", String.valueOf(checkIn.getCreatedAt()));
        params.put("checkin[updated_at]", String.valueOf(checkIn.getUpdatedAt()));

        Log.d(TAG, "uploading checkIn with parameters " + params);
        Log.d(TAG, "uploading checkIn with url " + url);

        final RequestFuture<JSONObject> futureRequest = RequestFuture.newFuture();
        CustomJsonRequest jsonRequest = new CustomJsonRequest(Request.Method.POST, url, params, futureRequest, futureRequest);

        AppController.getInstance().getRequestQueue().add(jsonRequest);
        try {
            JSONObject response = futureRequest.get(30, TimeUnit.SECONDS);
            Log.d(TAG, "checkIn uploaded with response " + response);
            String serverId = response.getJSONObject("checkin").getString("id");
            CheckinDataSource.updateServerId(context.getApplicationContext(), checkIn.getId(), serverId);
            return true;
        } catch (InterruptedException e) {
            Log.d(TAG, "checkIn couldnot be uploaded InterruptedException");
            e.printStackTrace();
        } catch (ExecutionException e) {
            Log.d(TAG, "checkIn couldnot be uploaded ExecutionException");
            e.printStackTrace();
        } catch (TimeoutException e) {
            Log.d(TAG, "checkIn couldnot be uploaded TimeoutException");
            e.printStackTrace();
        } catch (JSONException e) {
            Log.d(TAG, "checkIn could not be parsed although uploaded successfully");
            e.printStackTrace();
        }
        return false;
    }*/

    public static boolean uploadCheckInOnServer(Context context, CheckIn checkIn){
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        if(checkIn.getCheckInPicLocalPath() != null) {
            entityBuilder.addPart("checkin[picture_file]", new FileBody(new File(checkIn.getCheckInPicLocalPath())));
        }
        entityBuilder.addTextBody("api_key", TJPreferences.getApiKey(context));
        entityBuilder.addTextBody("checkin[user_id]", checkIn.getCreatedBy());
        entityBuilder.addTextBody("checkin[place_name]", checkIn.getCheckInPlaceName());
        entityBuilder.addTextBody("checkin[latitude]", String.valueOf(checkIn.getLatitude()));
        entityBuilder.addTextBody("checkin[longitude]", String.valueOf(checkIn.getLongitude()));
        entityBuilder.addTextBody("checkin[buddies]", checkIn.getCheckInWith() == null ? null : checkIn.getCheckInWith().toString());
        entityBuilder.addTextBody("checkin[note]", checkIn.getCaption());
        entityBuilder.addTextBody("checkin[created_at]", String.valueOf(checkIn.getCreatedAt()));
        entityBuilder.addTextBody("checkin[updated_at]", String.valueOf(checkIn.getUpdatedAt()));

        String url = Constants.URL_MEMORY_UPLOAD + TJPreferences.getActiveJourneyId(context) + "/checkins";
        HttpPost updateProfileRequest = new HttpPost(url);
        updateProfileRequest.setEntity(entityBuilder.build());
        HttpResponse response;
        try {
            response = new DefaultHttpClient().execute(updateProfileRequest);
            JSONObject object = new JSONObject(EntityUtils.toString(response.getEntity()));

            Log.d(TAG, "checkIn uploaded with response " + object);
            String serverId = object.getJSONObject("checkin").getString("id");
            String serverUrl = object.getJSONObject("checkin").getJSONObject("picture_file").getJSONObject("original").getString("url");
            CheckinDataSource.updateServerIdAndPicUrl(context.getApplicationContext(), checkIn.getId(), serverId, serverUrl);
            Log.d(TAG, "checkin successfully uploaded on server");
            return true;
        } catch (Exception e) {
            Log.d(TAG, "error in uploading audio" + e.getMessage());
            return false;
        }
    }

    public static void createNewCheckInFromServer(final Context context, final CheckIn checkIn, final int downloadRequesterCode) {
        final String imagePath = Constants.TRAVELJAR_FOLDER_PICTURE + "thumb_" + TJPreferences.getUserId(context) + "_"+
                checkIn.getjId() + "_"+ checkIn.getCreatedAt() + ".jpg";
        File file = new File(imagePath);
        if(!file.exists()) {
            if (checkIn.getCheckInPicThumbUrl() != null && !checkIn.getCheckInPicThumbUrl().equals("null")) {
                ImageRequest request = new ImageRequest(checkIn.getCheckInPicThumbUrl(), new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        FileOutputStream out = null;
                        try {
                            out = new FileOutputStream(imagePath);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            checkIn.setCheckInPicThumbUrl(imagePath);
                            long id = CheckinDataSource.createCheckIn(checkIn, context);
                            checkIn.setId(String.valueOf(id));
                            Log.d(TAG, "saving checkin " + checkIn);
                            PullMemoriesService.isFinished();
                            EventBus.getDefault().post(new CheckInDownloadEvent(checkIn, true, downloadRequesterCode));
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
                        Log.d(TAG, "error oaccuered" + error.getMessage());
                        EventBus.getDefault().post(new CheckInDownloadEvent(checkIn, false, downloadRequesterCode));
                    }
                });
                AppController.getInstance().addToRequestQueue(request);
            }else {
                checkIn.setCheckInPicThumbUrl(null);
                long id = CheckinDataSource.createCheckIn(checkIn, context);
                checkIn.setId(String.valueOf(id));
                PullMemoriesService.isFinished();
                EventBus.getDefault().post(new CheckInDownloadEvent(checkIn, true, downloadRequesterCode));
            }
        }else {
            checkIn.setCheckInPicThumbUrl(imagePath);
            long id = CheckinDataSource.createCheckIn(checkIn, context);
            checkIn.setId(String.valueOf(id));
            PullMemoriesService.isFinished();
            EventBus.getDefault().post(new CheckInDownloadEvent(checkIn, true, downloadRequesterCode));
        }
    }

}
