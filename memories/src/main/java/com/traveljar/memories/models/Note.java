package com.traveljar.memories.models;

import android.content.Context;

import com.traveljar.memories.SQLitedatabase.NoteDataSource;

import java.util.List;

public class Note extends Memories {

    private String content;
    private String caption;

    public Note() {

    }

    public Note(String idOnServer, String jId, String memType, String caption, String content,
                String createdBy, long createdAt, long updatedAt, List<Like> likes, Double latitude, Double longitude) {
        this.idOnServer = idOnServer;
        this.jId = jId;
        this.memType = memType;
        this.caption = caption;
        this.content = content;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.likes = likes;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void updateLikedBy(Context context, String memId, List<String> likedBy) {
        NoteDataSource.updateFavourites(context, memId, likedBy);
    }

    @Override
    public String toString() {
        return "id on server -> " + this.getIdOnServer() +
                "journey id -> " + this.getjId() +
                "memory type -> " + this.getMemType() +
                "created by -> " + this.getCreatedBy() +
                "created at -> " + this.getCreatedAt() +
                "liked by -> " + this.getLikes() +
                "content -> " + this.getContent() +
                "caption -> " + this.getCaption();
    }

}
