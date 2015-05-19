package com.example.memories.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.example.memories.SQLitedatabase.PictureDataSource;
import com.example.memories.models.Picture;
import com.example.memories.volley.AppController;

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

public class PictureUtilities {

    private static final String TAG = "PICTURE_UTILITY";

    public static String downloadPicFromURL(final Context context, final Picture pic) {
        String picServerUrl = pic.getDataServerURL();
        final String picLocalUrl = Constants.TRAVELJAR_FOLDER_PICTURE + System.currentTimeMillis()
                + ".jpeg";
        if (picServerUrl != null) {
            ImageRequest request = new ImageRequest(picServerUrl, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap bitmap) {
                    FileOutputStream out = null;
                    try {
                        File tjDir = new File(Constants.TRAVELJAR_FOLDER_PICTURE);
                        if (!tjDir.exists()) {
                            tjDir.mkdirs();
                        }
                        out = new FileOutputStream(picLocalUrl);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        pic.setDataLocalURL(picLocalUrl);
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
                }
            }, 0, 0, null, new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                }
            });
            AppController.getInstance().addToRequestQueue(request);
        }
        return picLocalUrl;
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

            String url = "https://www.traveljar.in/api/v1/journeys/" + TJPreferences.getActiveJourneyId(context) + "/pictures";
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
            try {
                Log.d(TAG, "onPostExecute()");
                String serverId = object.getJSONObject("picture").getString("id");
                String serverUrl = object.getJSONObject("picture")
                        .getJSONObject("picture_file").getJSONObject("original").getString("url");
                PictureDataSource.updateServerIdAndUrl(context, picture.getId(), serverId,
                        serverUrl);
                Log.d(TAG, "picture successfully uploaded and serverid successfully saved in database");
            } catch (JSONException ex) {
                Log.d(TAG, ex.getMessage());
            }
        }
    }
}



/*RestAdapter restAdapter = new RestAdapter.Builder().setConverter(new StringConverter())
                .setEndpoint(Constants.TRAVELJAR_API_BASE_URL).build();
		TravelJarServices myService = restAdapter.create(TravelJarServices.class);

		myService.uploadPicture(TJPreferences.getActiveJourneyId(context), new TypedString(
				TJPreferences.getApiKey(context)),
				new TypedString(TJPreferences.getUserId(context)),
				new TypedFile("image*//*",
				new File(picture.getDataLocalURL())),
				new Callback<String>() {
					@Override
					public void success(String str, retrofit.client.Response response) {
						try {
							Log.d(TAG, "image uploaded successfully " + str);
							JSONObject object = new JSONObject(str);
							Log.d(TAG, "image uploaded successfully " + object.getJSONObject("picture").getString("id"));
							Log.d(TAG, "image uploaded successfully " + object.getJSONObject("picture_file").getJSONObject("original").getString("url"));
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void failure(RetrofitError retrofitError) {
						Log.d(TAG, "error in uploading picture" + retrofitError);
						retrofitError.printStackTrace();
					}
				});*/