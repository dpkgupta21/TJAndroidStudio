package com.traveljar.memories.audio;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.traveljar.memories.customevents.AudioDownloadEvent;
import com.traveljar.memories.models.Audio;
import com.traveljar.memories.utility.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.greenrobot.event.EventBus;

public class DownloadAudioAsyncTask extends AsyncTask<String, Integer, String> {

    private static final String TAG = "DOWNLOAD_ASYNC_TASK";
    private Audio mAudio;
    private final int CALLING_ACTIVITY_CODE;
    private Context context;

    public DownloadAudioAsyncTask(int activityCode, Audio audio) {
        CALLING_ACTIVITY_CODE = activityCode;
        mAudio = audio;
    }

    @Override
    protected String doInBackground(String... params) {
        Log.d(TAG, "inside download task do in background");
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        String fileLocation = Constants.TRAVELJAR_FOLDER_AUDIO + "/aud_" + mAudio.getCreatedBy() + "_" + mAudio.getjId() +
                "_" + mAudio.getCreatedAt() + ".3gp";

        if(!(new File(fileLocation)).exists()){
            try {
                URL downloadUrl = new URL(mAudio.getDataServerURL());
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
                Log.d(TAG, "download finished");
                mAudio.setDataLocalURL(fileLocation);
                return fileLocation;
            } catch (Exception e) {
                Log.d(TAG, "exception in downloading audio " + e);
                e.printStackTrace();
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
        }else {
            mAudio.setDataLocalURL(fileLocation);
            return fileLocation;
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            EventBus.getDefault().post(new AudioDownloadEvent(mAudio, true, CALLING_ACTIVITY_CODE));
        }else {
            EventBus.getDefault().post(new AudioDownloadEvent(mAudio, false, CALLING_ACTIVITY_CODE));
        }
    }
}
