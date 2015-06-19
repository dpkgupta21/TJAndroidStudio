package com.example.memories.models;

import java.util.List;

public class Audio extends Memories {
    private String extension;
    private long size;
    private String dataServerURL;
    private String dataLocalURL;
    private long audioDuration;

    public Audio(String idOnServer, String jId, String memType, String ext, long size,
                 String dataServerURL, String dataLocalURL, String createdBy, long createdAt,
                 long updatedAt, List<Like> likes, long audioDuration, Double latitude, Double longitude) {
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
        this.likes = likes;
        this.audioDuration = audioDuration;
        this.latitude = latitude;
        this.longitude = longitude;
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

/*
    public void updateLikedBy(Context context, String memId, List<String> likedBy) {
        AudioDataSource.updateFavourites(context, memId, likedBy);
    }
*/

    public long getAudioDuration() {
        return audioDuration;
    }

    public void setAudioDuration(long audioDuration) {
        this.audioDuration = audioDuration;
    }

    @Override
    public String toString() {
        return "id on server -> " + this.getIdOnServer()+
                "journey id -> " + this.getjId() +
                "memory type -> " + this.getMemType() +
                "created by -> " + this.getCreatedBy() +
                "created at -> " + this.getCreatedAt() +
                "liked by -> " + this.getLikes() +
                "data server url -> " + this.getDataServerURL() +
                "data local url -> " + this.getDataLocalURL();


    }
}