package com.traveljar.memories.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.traveljar.memories.SQLitedatabase.CheckinDataSource;
import com.traveljar.memories.eventbus.CheckInDownloadEvent;
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
                            EventBus.getDefault().post(new CheckInDownloadEvent(checkIn, false, downloadRequesterCode));
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
                        PullMemoriesService.isFinished();
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
