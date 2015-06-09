package com.example.memories.models;

import android.content.Context;

import com.example.memories.SQLitedatabase.PictureDataSource;

import java.util.List;

public class
        Picture extends Memories {

    private String caption;
    private String extension;
    private long size;
    private String dataServerURL;
    private String dataLocalURL;
    private String picLocalThumbnailPath;

    // To check whether the image is selected in gridview or not. Not to be
    // saved in database
    private boolean checked = false;

    public Picture() {

    }

    public Picture(String idOnServer, String jId, String memType, String caption, String ext,
                   long size, String dataServerURL, String dataLocalURL, String createdBy, long createdAt,
                   long updatedAt, List<String> likedBy, String picLocalThumbnailPath, Double latitude, Double longitude) {
        this.idOnServer = idOnServer;
        this.jId = jId;
        this.memType = memType;
        this.caption = caption;
        this.extension = ext;
        this.size = size;
        this.dataServerURL = dataServerURL;
        this.dataLocalURL = dataLocalURL;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.likedBy = likedBy;
        this.picLocalThumbnailPath = picLocalThumbnailPath;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
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

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
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

    public String getPicThumbnailPath() {
        return picLocalThumbnailPath;
    }

    public void setPicThumbnailPath(String picLocalThumbnailPath) {
        this.picLocalThumbnailPath = picLocalThumbnailPath;
    }

    public void updateLikedBy(Context context, String memId, List<String> likedBy) {
        PictureDataSource.updateFavourites(context, memId, likedBy);
    }

    @Override
    public String toString() {
        return "id on server -> " + this.getIdOnServer() + "\n" +
                "journey id -> " + this.getjId() + "\n" +
                "memory type -> " + this.getMemType() + "\n" +
                "created by -> " + this.getCreatedBy() + "\n" +
                "created at -> " + this.getCreatedAt() + "\n" +
                "liked by -> " + this.getLikedBy() + "\n" +
                "caption -> " + this.getCaption() + "\n" +
                "picture server url -> " + this.getDataServerURL() + "\n" +
                "picture local url -> " + this.getDataLocalURL() + "\n" +
                "picture thumbnail url -> " + this.getPicThumbnailPath();
    }

}
