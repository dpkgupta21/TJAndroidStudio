package com.example.memories.retrofit;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.memories.R;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;
import java.io.RandomAccessFile;

public class MultiPartUpload extends Activity {
    Button upload;
    TextView uploadCount;
    ProgressBar progressBar;
    Future<JsonObject> uploading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable global Ion logging
        Ion.getDefault(this).configure().setLogging("ion-sample", Log.DEBUG);

        setContentView(R.layout.progress_upload);

        upload = (Button) findViewById(R.id.upload);
        uploadCount = (TextView) findViewById(R.id.upload_count);
        progressBar = (ProgressBar) findViewById(R.id.progress);

        upload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (uploading != null && !uploading.isCancelled()) {
                    resetUpload();
                    return;
                }

                File f = getFileStreamPath("largefile");
                try {
                    RandomAccessFile rf = new RandomAccessFile(f, "rw");
                    rf.setLength(1024 * 1024 * 2);
                } catch (Exception e) {
                    System.err.println(e);
                }
                File echoedFile = getFileStreamPath("echo");

                upload.setText("Cancel");

                uploading = Ion
                        .with(MultiPartUpload.this)
                        .load("https://koush.clockworkmod.com/test/echo")
                        .uploadProgressBar(progressBar)
                        .setMultipartParameter("goop", "noop")
                        .setMultipartFile("DSC_1031.JPG",
                                new File("/storage/emulated/0/DCIM/100ANDRO/DSC_1031.JPG"))
                        .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                resetUpload();
                                if (e != null) {
                                    Toast.makeText(MultiPartUpload.this, "Error uploading file",
                                            Toast.LENGTH_LONG).show();
                                    return;
                                }
                                Toast.makeText(MultiPartUpload.this, "File upload complete",
                                        Toast.LENGTH_LONG).show();
                            }
                        });

            }
        });
    }

    void resetUpload() {
        // cancel any pending upload
        uploading.cancel();
        uploading = null;

        // reset the ui
        upload.setText("Upload");
        uploadCount.setText(null);
        progressBar.setProgress(0);
    }
}