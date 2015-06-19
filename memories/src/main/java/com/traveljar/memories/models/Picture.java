package com.traveljar.memories.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Picture extends Memories implements Parcelable {

    public static final Parcelable.Creator<Picture> CREATOR = new Parcelable.Creator<Picture>() {
        public Picture createFromParcel(Parcel in) {
            return new Picture(in);
        }

        public Picture[] newArray(int size) {
            return new Picture[size];
        }
    };

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
                   long updatedAt, List<Like> likes, String picLocalThumbnailPath, Double latitude, Double longitude) {
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
        this.likes = likes;
        this.picLocalThumbnailPath = picLocalThumbnailPath;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Picture(Parcel parcel){
        id = parcel.readString();
        idOnServer = parcel.readString();
        jId = parcel.readString();
        memType = parcel.readString();
        createdBy = parcel.readString();
        createdAt = parcel.readLong();
        updatedAt = parcel.readLong();
        latitude = parcel.readDouble();
        longitude = parcel.readDouble();
        parcel.readTypedList(likes, Like.CREATOR);
        caption = parcel.readString();
        extension = parcel.readString();
        size = parcel.readLong();
        dataServerURL = parcel.readString();
        dataLocalURL = parcel.readString();
        picLocalThumbnailPath = parcel.readString();
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

    /*public void updateLikedBy(Context context, String memId, List<String> likedBy) {
        PictureDataSource.updateFavourites(context, memId, likedBy);
    }*/

    @Override
    public String toString() {
        return "id -> " + this.getId() +
                "id on server -> " + this.getIdOnServer() +
                "journey id -> " + this.getjId() +
                "memory type -> " + this.getMemType() +
                "created by -> " + this.getCreatedBy() +
                "created at -> " + this.getCreatedAt() +
                "liked by -> " + this.getLikes() +
                "caption -> " + this.getCaption() +
                "picture server url -> " + this.getDataServerURL() +
                "picture local url -> " + this.getDataLocalURL() +
                "picture thumbnail url -> " + this.getPicThumbnailPath();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(id);
        parcel.writeString(idOnServer);
        parcel.writeString(jId);
        parcel.writeString(memType);
        parcel.writeString(createdBy);
        parcel.writeLong(createdAt);
        parcel.writeLong(updatedAt);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeTypedList(likes);
        parcel.writeString(caption);
        parcel.writeString(extension);
        parcel.writeLong(size);
        parcel.writeString(dataServerURL);
        parcel.writeString(dataLocalURL);
        parcel.writeString(picLocalThumbnailPath);
    }
}
