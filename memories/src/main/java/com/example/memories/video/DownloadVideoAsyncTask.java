package com.example.memories.video;

import android.os.AsyncTask;
import android.util.Log;

import com.example.memories.models.Video;
import com.example.memories.utility.Constants;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ankit on 2/6/15.
 */

public class DownloadVideoAsyncTask extends AsyncTask<String, Integer, String> {

    private static final String TAG = "DOWNLOAD_ASYNC_TASK";
    private OnVideoDownloadListener mListener;
    private Video mVideo;

    public DownloadVideoAsyncTask(OnVideoDownloadListener listener, Video video) {
        mListener = listener;
        mVideo = video;
    }

    @Override
    protected String doInBackground(String... url) {
        Log.d(TAG, "inside download task do in background");
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        String fileLocation = Constants.TRAVELJAR_FOLDER_VIDEO + System.currentTimeMillis() + ".mp4";
        URL downloadUrl;
        try {
            Log.d(TAG, "video server url is " + mVideo.getDataServerURL());
            downloadUrl = new URL(mVideo.getDataServerURL());
            Log.d(TAG, "started downloading video");
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
            mVideo.setDataLocalURL(fileLocation);
            Log.d(TAG, "finished Downloading video");
            return fileLocation;
        } catch (Exception e) {
            Log.d(TAG, "Error in downloading video");
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
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            mListener.onVideoDownload(result, mVideo);
        }
    }

    public interface OnVideoDownloadListener {
        void onVideoDownload(String videoLocalUrl, Video video);
    }

}
