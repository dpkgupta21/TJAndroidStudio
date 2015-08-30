package com.traveljar.memories.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.traveljar.memories.SQLitedatabase.PictureDataSource;
import com.traveljar.memories.eventbus.PictureDownloadEvent;
import com.traveljar.memories.models.Picture;
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

public class PictureUtilities {

    private static final String TAG = "PICTURE_UTILITY";

    private static PictureUtilities instance;

    public static PictureUtilities getInstance(){
        if(instance == null)
            instance = new PictureUtilities();
        return instance;
    }

    public void createNewPicFromServer(final Context context, final Picture pic, String thumbUrl, final int downloadRequesterCode) {
        final String imagePath = Constants.TRAVELJAR_FOLDER_PICTURE + "thumb_" + TJPreferences.getUserId(context) + "_"+ pic.getjId() + "_"+ pic.getCreatedAt() + ".jpg";
        File file = new File(imagePath);
        if(!file.exists()) {
            if (thumbUrl != null) {
                ImageRequest request = new ImageRequest(thumbUrl, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        FileOutputStream out = null;
                        try {
                            out = new FileOutputStream(imagePath);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            pic.setPicThumbnailPath(imagePath);
                            long id = PictureDataSource.createPicture(pic, context);
                            pic.setId(String.valueOf(id));
                            Log.d(TAG, "saving picture " + pic);
/*                        if(finishListener != null) {
                            finishListener.onFinishDownload(pic.getIdOnServer(), pic.getMemType(), String.valueOf(id));
                        }*/
                            PullMemoriesService.isFinished();
                            EventBus.getDefault().post(new PictureDownloadEvent(pic, true, downloadRequesterCode));
                        } catch (Exception e) {
                            EventBus.getDefault().post(new PictureDownloadEvent(pic, false, downloadRequesterCode));
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
                        PullMemoriesService.isFinished();
                        EventBus.getDefault().post(new PictureDownloadEvent(pic, false, downloadRequesterCode));
                        Log.d(TAG, "error oaccuered" + error.getMessage());
                    }
                });
                AppController.getInstance().addToRequestQueue(request);
            }
        }else {
            pic.setPicThumbnailPath(imagePath);
            long id = PictureDataSource.createPicture(pic, context);
            pic.setId(String.valueOf(id));
            PullMemoriesService.isFinished();
            EventBus.getDefault().post(new PictureDownloadEvent(pic, true, downloadRequesterCode));
        }
    }

    public static boolean uploadPicOnServer(Context context, final Picture picture){
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        Log.d(TAG, "picture " + picture);
        entityBuilder.addPart("picture[picture_file]", new FileBody(new File(picture.getDataLocalURL())));
        entityBuilder.addTextBody("picture[user_id]", picture.getCreatedBy());
        entityBuilder.addTextBody("api_key", TJPreferences.getApiKey(context));
        entityBuilder.addTextBody("picture[latitude]", String.valueOf(picture.getLatitude()));
        entityBuilder.addTextBody("picture[longitude]", String.valueOf(picture.getLongitude()));
        entityBuilder.addTextBody("picture[description]", picture.getCaption());
        entityBuilder.addTextBody("picture[created_at]", String.valueOf(picture.getCreatedAt()));
        entityBuilder.addTextBody("picture[updated_at]", String.valueOf(picture.getUpdatedAt()));
        String url = Constants.URL_MEMORY_UPLOAD + TJPreferences.getActiveJourneyId(context) + "/pictures";
        HttpPost updateProfileRequest = new HttpPost(url);
        updateProfileRequest.setEntity(entityBuilder.build());
        HttpResponse response;
        try {
            response = new DefaultHttpClient().execute(updateProfileRequest);
            JSONObject object = new JSONObject(EntityUtils.toString(response.getEntity()));
            Log.d(TAG, "response on uploading picture" + object);
            String serverId = object.getJSONObject("picture").getString("id");
            String serverUrl = object.getJSONObject("picture").getJSONObject("picture_file").getJSONObject("original").getString("url");
            PictureDataSource.updateServerIdAndUrl(context, picture.getId(), serverId, serverUrl);
            return true;
        } catch (Exception e) {
            Log.d(TAG, "error in uploading picture" + e.getMessage());
            return false;
        }
        //parsing response received from server
    }

}

