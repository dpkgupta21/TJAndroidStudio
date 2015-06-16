package com.example.memories.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.memories.SQLitedatabase.PictureDataSource;

import java.util.List;

public class Picture extends Memories implements Parcelable{

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

    public Picture(Parcel parcel){
        this.idOnServer = parcel.readString();
        this.jId = parcel.readString();
        this.memType = parcel.readString();
        this.createdBy = parcel.readString();
        this.createdAt = parcel.readLong();
        this.updatedAt = parcel.readLong();
        this.latitude = parcel.readDouble();
        this.longitude = parcel.readDouble();
        parcel.readStringList(likedBy);
        this.caption = parcel.readString();
        this.extension = parcel.readString();
        this.size = parcel.readLong();
        this.dataServerURL = parcel.readString();
        this.dataLocalURL = parcel.readString();
        this.picLocalThumbnailPath = parcel.readString();
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
        return "id -> " + this.getId() + "\n" +
                "id on server -> " + this.getIdOnServer() + "\n" +
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(idOnServer);
        parcel.writeString(jId);
        parcel.writeString(memType);
        parcel.writeString(createdBy);
        parcel.writeLong(createdAt);
        parcel.writeLong(updatedAt);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeStringList(likedBy);
        parcel.writeString(caption);
        parcel.writeString(extension);
        parcel.writeLong(size);
        parcel.writeString(dataServerURL);
        parcel.writeString(dataLocalURL);
        parcel.writeString(picLocalThumbnailPath);
    }
}
