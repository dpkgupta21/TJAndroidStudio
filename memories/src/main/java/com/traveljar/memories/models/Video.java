package com.traveljar.memories.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;


public class Video extends Memories implements Parcelable {

    public static final Creator<Video> CREATOR = new Creator<Video>() {

        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        public Video[] newArray(int size) {
            return new Video[size];
        }
    };

    private String caption;
    private String extension;
    private long size;
    private String dataServerURL;
    private String dataLocalURL;
    private boolean checked;
    private String localThumbPath;

    public Video() {

    }

    public Video(String idOnServer, String jId, String memType, String caption, String ext,
                 long size, String dataServerURL, String dataLocalURL, String createdBy, long createdAt,
                 long updatedAt, List<Like> likes, String localThumbPath, Double latitude, Double longitude) {
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
        this.localThumbPath = localThumbPath;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
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

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
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

/*    public void updateLikedBy(Context context, String memId, List<String> likedBy) {
        VideoDataSource.updateFavourites(context, memId, likedBy);
    }*/

    public String getLocalThumbPath() {
        return localThumbPath;
    }

    public void setLocalThumbPath(String localThumbPath) {
        this.localThumbPath = localThumbPath;
    }

    @Override
    public String toString() {
        return "id on server -> " + this.getIdOnServer() + "\n" +
                "journey id -> " + this.getjId() + "\n" +
                "memory type -> " + this.getMemType() + "\n" +
                "created by -> " + this.getCreatedBy() + "\n" +
                "created at -> " + this.getCreatedAt() + "\n" +
                "caption -> " + this.getCaption() + "\n" +
                "picture server url -> " + this.getDataServerURL() + "\n" +
                "picture local url -> " + this.getDataLocalURL() + "\n" +
                "picture thumbnail url -> " + this.getLocalThumbPath();
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
        parcel.writeList(likes);
        parcel.writeString(caption);
        parcel.writeString(extension);
        parcel.writeLong(size);
        parcel.writeString(dataServerURL);
        parcel.writeString(dataLocalURL);
        parcel.writeString(localThumbPath);
    }

    public Video(Parcel parcel){
        id = parcel.readString();
        idOnServer = parcel.readString();
        jId = parcel.readString();
        memType = parcel.readString();
        createdBy = parcel.readString();
        createdAt = parcel.readLong();
        updatedAt = parcel.readLong();
        latitude = parcel.readDouble();
        longitude = parcel.readDouble();
        likes = parcel.readArrayList(Like.class.getClassLoader());
        caption = parcel.readString();
        extension = parcel.readString();
        size = parcel.readLong();
        dataServerURL = parcel.readString();
        dataLocalURL = parcel.readString();
        localThumbPath = parcel.readString();
    }

}
