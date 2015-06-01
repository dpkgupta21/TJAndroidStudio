package com.example.memories.models;

import android.content.Context;

import com.example.memories.SQLitedatabase.MoodDataSource;

import java.util.List;


public class Mood extends Memories {
    private List<String> buddyIds;
    private String mood;
    private String reason;

    public Mood() {

    }

    public Mood(String idOnServer, String jId, String memType, List<String> buddyIds, String mood,
                String reason, String createdBy, long createdAt, long updatedAt, List<String> likedBy) {
        this.idOnServer = idOnServer;
        this.jId = jId;
        this.memType = memType;
        this.buddyIds = buddyIds;
        this.mood = mood;
        this.reason = reason;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.likedBy = likedBy;
    }

    public List<String> getBuddyIds() {
        return buddyIds;
    }

    public void setBuddyIds(List<String> buddyIds) {
        this.buddyIds = buddyIds;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }


    public void updateLikedBy(Context context, String memId, List<String> likedBy) {
        MoodDataSource.updateFavourites(context, memId, likedBy);
    }

}
