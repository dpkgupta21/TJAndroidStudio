package com.traveljar.memories.picture;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.traveljar.memories.SQLitedatabase.PictureDataSource;
import com.traveljar.memories.models.Picture;
import com.traveljar.memories.utility.Constants;
import com.traveljar.memories.utility.TJPreferences;

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

/**
 * Created by ankit on 16/6/15.
 */
public class UploadPictureService extends IntentService {

    private Picture picture;

    private static final String TAG = "UploadPictureService";

    public UploadPictureService() {
        super("UploadPictureService");
    }

    public UploadPictureService(String name) {
        super(name);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, startId, startId);
        picture = intent.getParcelableExtra("PICTURE");
        Log.d(TAG, "upload picture service has started for picture" + picture.getId());
        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        Log.d(TAG, "on Handle Intent");
        uploadPicture();
    }

    private void uploadPicture(){
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        entityBuilder.addPart("picture[picture_file]", new FileBody(new File(picture.getDataLocalURL())));
        entityBuilder.addTextBody("picture[user_id]", picture.getCreatedBy());
        entityBuilder.addTextBody("api_key", TJPreferences.getApiKey(this));
        entityBuilder.addTextBody("picture[latitude]", String.valueOf(picture.getLatitude()));
        entityBuilder.addTextBody("picture[longitude]", String.valueOf(picture.getLongitude()));
        entityBuilder.addTextBody("picture[description]", picture.getCaption());

        String url = Constants.URL_MEMORY_UPLOAD + TJPreferences.getActiveJourneyId(this) + "/pictures";
        HttpPost updateProfileRequest = new HttpPost(url);
        updateProfileRequest.setEntity(entityBuilder.build());
        HttpResponse response;
        try {
            response = new DefaultHttpClient().execute(updateProfileRequest);
            JSONObject object = new JSONObject(EntityUtils.toString(response.getEntity()));
            Log.d(TAG, "response on uploading picture" + object);
            parseAndUpdatePicture(object);
        } catch (Exception e) {
            Log.d(TAG, "error in uploading picture" + e.getMessage());
        }
    }

    private void parseAndUpdatePicture(JSONObject object){
        try {
            Log.d(TAG, "onPostExecute()" + picture);
            String serverId = object.getJSONObject("picture").getString("id");
            String serverUrl = object.getJSONObject("picture")
                    .getJSONObject("picture_file").getJSONObject("original").getString("url");
            picture.setIdOnServer(serverId);
            picture.setDataServerURL(serverUrl);
            PictureDataSource.updateServerIdAndUrl(this, picture.getId(), picture.getIdOnServer(), picture.getDataServerURL());
        } catch (JSONException ex) {
            Log.d(TAG, ex.getMessage());
        }
    }

}
