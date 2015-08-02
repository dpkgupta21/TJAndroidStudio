package com.traveljar.memories.currentjourney.adapters;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.traveljar.memories.models.Timecapsule;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by abhi on 02/08/15.
 */
public class DownloadTimecapsuleAsyncTask extends AsyncTask<String, Integer, String> {

    private static final String TAG = "DOWNLOAD_ASYNC_TASK";
    private OnTimecapsuleDownloadListener mListener;
    private Timecapsule mTimecapsule;

    public DownloadTimecapsuleAsyncTask(OnTimecapsuleDownloadListener listener, Timecapsule timecapsule) {
        mListener = listener;
        mTimecapsule = timecapsule;
    }

    @Override
    protected String doInBackground(String... url) {
        Log.d(TAG, "inside timecapsule download task do in background");
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        String fileLocation = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath() +
                "/timecap_" + mTimecapsule.getCreatedBy() + "_" + mTimecapsule.getjId() + "_" + mTimecapsule.getCreatedAt() + ".mp4";
        if(!(new File(fileLocation)).exists()) {
            try {
                Log.d(TAG, "timecapsule server url is " + mTimecapsule.getVideoServerURL());
                URL downloadUrl = new URL(mTimecapsule.getVideoServerURL());
                Log.d(TAG, "started downloading timecapsule");
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
                mTimecapsule.setVideoLocalURL(fileLocation);
                Log.d(TAG, "finished Downloading timecapsule");
                return fileLocation;
            } catch (Exception e) {
                Log.d(TAG, "Error in downloading timecapsule");
                e.printStackTrace();
                return null;
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
            mTimecapsule.setVideoLocalURL(fileLocation);
            return fileLocation;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            mListener.onTimecapsuleDownload(result, mTimecapsule);
        }
    }

    public interface OnTimecapsuleDownloadListener {
        void onTimecapsuleDownload(String timecapsuleLocalUrl, Timecapsule timecapsule);
    }

}

