package com.example.memories.models;

import android.content.Context;

import com.example.memories.SQLitedatabase.PictureDataSource;

public class Picture extends Memories {

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
                   long updatedAt, String likedBy, String picLocalThumbnailPath) {
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

    public void setPicThumbnailPath(String picLocalThumbnailPath) {
        this.picLocalThumbnailPath = picLocalThumbnailPath;
    }

    public String getPicThumbnailPath() {
        return picLocalThumbnailPath;
    }

    public void setDataLocalURL(String dataLocalURL) {
        this.dataLocalURL = dataLocalURL;
    }

    public void updateLikedBy(Context context, String memId, String likedBy) {
        PictureDataSource.updateFavourites(context, memId, likedBy);
    }

}
