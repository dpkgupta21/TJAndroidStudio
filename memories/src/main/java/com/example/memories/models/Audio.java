package com.example.memories.models;

import android.content.Context;

import com.example.memories.SQLitedatabase.AudioDataSource;

public class Audio extends Memories {
    private String extension;
    private long size;
    private String dataServerURL;
    private String dataLocalURL;

    public Audio(String idOnServer, String jId, String memType, String ext, long size,
                 String dataServerURL, String dataLocalURL, String createdBy, long createdAt,
                 long updatedAt, String likedBy) {
        this.idOnServer = idOnServer;
        this.jId = jId;
        this.memType = memType;
        this.extension = ext;
        this.size = size;
        this.dataServerURL = dataServerURL;
        this.dataLocalURL = dataLocalURL;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.likedBy = likedBy;
    }

    public Audio() {

    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getDataServerURL() {
        return dataServerURL;
    }

    public void setDataServerURL(String dataServerURL) {
        this.dataServerURL = dataServerURL;
    }

    public String getDataLocalURL() {
        return dataLocalURL;
    }

    public void setDataLocalURL(String dataLocalURL) {
        this.dataLocalURL = dataLocalURL;
    }

    public void updateLikedBy(Context context, String memId, String likedBy) {
        AudioDataSource.updateFavourites(context, memId, likedBy);
    }
}