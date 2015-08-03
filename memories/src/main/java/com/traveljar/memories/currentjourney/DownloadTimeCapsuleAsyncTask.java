package com.traveljar.memories.currentjourney;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.traveljar.memories.SQLitedatabase.TimecapsuleDataSource;
import com.traveljar.memories.models.Timecapsule;
import com.traveljar.memories.utility.HelpMe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadTimeCapsuleAsyncTask extends AsyncTask<String, Integer, String> {

    private static final String TAG = "DOWNLOAD_ASYNC_TASK";
    //private OnVideoDownloadListener mListener;
    private Timecapsule mTimeCapsule;
    private Context mContext;

    public DownloadTimeCapsuleAsyncTask(Timecapsule timeCapsule, Context context) {
        //mListener = listener;
        mContext = context;
        mTimeCapsule = timeCapsule;
    }

    @Override
    protected String doInBackground(String... url) {
        Log.d(TAG, "inside download task do in background");
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;

        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath());
        if(!storageDir.exists()){
            /*Check whether the directory exists if not create one*/
            storageDir.mkdirs();
        }
        /*Location of video to be saved*/
        String fileLocation = storageDir.getAbsolutePath() + "/time_" + HelpMe.getCurrentTime() + "_" +
                mTimeCapsule.getjId() + "_" + ".mp4";
        try {
            Log.d(TAG, "video server url is " + mTimeCapsule.getVideoServerURL());
            URL downloadUrl = new URL(mTimeCapsule.getVideoServerURL());
            Log.d(TAG, "started downloading video");
            connection = (HttpURLConnection) downloadUrl.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report instead of the file
            /*Checking whether network connection is ok*/
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode() + " "
                        + connection.getResponseMessage();
            }

            /*File download starts here*/
            input = connection.getInputStream();
            output = new FileOutputStream(fileLocation);

            byte data[] = new byte[4096];
            int count;
            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
            }
            /*File downloading ends here*/

            //update the video local url
            mTimeCapsule.setVideoLocalURL(fileLocation);

            //save timecapsule to the database
            TimecapsuleDataSource.createTimecapsule(mTimeCapsule, mContext);
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
            /*mListener.onVideoDownload(result, mVideo);*/
        }
    }

/*    public interface OnVideoDownloadListener {
        void onVideoDownload(String videoLocalUrl, Video video);
    }*/

}
