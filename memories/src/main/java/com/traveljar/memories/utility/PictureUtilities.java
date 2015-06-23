package com.traveljar.memories.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.traveljar.memories.SQLitedatabase.PictureDataSource;
import com.traveljar.memories.models.Picture;
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
import java.util.HashMap;
import java.util.Map;

public class PictureUtilities {

    private static final String TAG = "PICTURE_UTILITY";

    // Not user right now
    public static String downloadPicFromURL(final Context context, final Picture pic, final ImageView imageView) {
        Log.d(TAG, "download pic called");
        String picServerUrl = pic.getDataServerURL();
        final String imagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/pic_" + System.currentTimeMillis() + ".jpg";
        if (picServerUrl != null) {
            ImageRequest request = new ImageRequest(picServerUrl, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap bitmap) {
                    FileOutputStream out = null;
                    try {
                        out = new FileOutputStream(imagePath);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        imageView.setImageBitmap(bitmap);
                        pic.setPicThumbnailPath(imagePath);
                        PictureDataSource.updatePicLocalPath(context, imagePath, pic.getId());
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
                }
            });
            AppController.getInstance().addToRequestQueue(request);
        }
        return imagePath;
    }

    public static void createNewPicFromServer(final Context context, final Picture pic, String thumbUrl) {
        final String imagePath = Constants.TRAVELJAR_FOLDER_PICTURE + "/thumb_" + System.currentTimeMillis() + ".jpg";
        if (thumbUrl != null) {
            ImageRequest request = new ImageRequest(thumbUrl, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap bitmap) {
                    FileOutputStream out = null;
                    try {
                        out = new FileOutputStream(imagePath);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        pic.setPicThumbnailPath(imagePath);
                        PictureDataSource.createPicture(pic, context);
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
            AppController.getInstance().addToRequestQueue(request);
        }
    }

    public static void uploadPicture(Context context, Picture picture) {
        UploadAsyncTask task = (new PictureUtilities()).new UploadAsyncTask(context, picture);
        task.execute();
    }

    private class UploadAsyncTask extends AsyncTask<String, Void, JSONObject> {

        Context context;
        Picture picture;

        public UploadAsyncTask(Context context, Picture picture) {
            this.context = context;
            this.picture = picture;
        }

        @Override
        protected JSONObject doInBackground(String... maps) {
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
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
                return object;
            } catch (Exception e) {
                Log.d(TAG, "error in uploading picture" + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject object) {
            // If upload is successful than set server id and data server url received from response and create new picture in db
            //else save the icture as it is
            if (object != null) {
                try {
                    Log.d(TAG, "onPostExecute()" + picture);
                    String serverId = object.getJSONObject("picture").getString("id");
                    String serverUrl = object.getJSONObject("picture")
                            .getJSONObject("picture_file").getJSONObject("original").getString("url");
                    picture.setIdOnServer(serverId);
                    picture.setDataServerURL(serverUrl);
                } catch (JSONException ex) {
                    Log.d(TAG, ex.getMessage());
                }
            }
            PictureDataSource.updateServerIdAndUrl(context, picture.getId(), picture.getIdOnServer(), picture.getDataServerURL());
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

    public static void updateCaption(final Picture picture, final String caption, final Context context){
        if(!HelpMe.isNetworkAvailable(context)){
            Toast.makeText(context, "Network unavailable please try after some time", Toast.LENGTH_SHORT).show();
        }else {
            String url = Constants.URL_MEMORY_UPDATE + TJPreferences.getActiveJourneyId(context) + "/pictures/" + picture.getIdOnServer();
            Map<String, String> params = new HashMap<>();
            params.put("api_key", TJPreferences.getApiKey(context));
            params.put("picture[description]", TJPreferences.getApiKey(context));
            CustomJsonRequest uploadRequest = new CustomJsonRequest(Request.Method.PUT, url, params,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, "picture caption updated successfully" + response);
                            PictureDataSource.updateCaption(context, caption, picture.getId());
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

