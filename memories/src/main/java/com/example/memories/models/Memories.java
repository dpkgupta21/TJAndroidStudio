package com.example.memories.models;

import android.content.Context;

import com.example.memories.utility.TJPreferences;

import java.util.List;

public class Memories implements Comparable<Memories> {
    protected String idOnServer;
    protected String jId;
    protected String memType;
    protected String createdBy;
    protected long createdAt;
    protected long updatedAt;
    protected Double latitude;
    protected Double longitude;
    protected List<Like> likes;

    protected String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdOnServer() {
        return idOnServer;
    }

    public void setIdOnServer(String idOnServer) {
        this.idOnServer = idOnServer;
    }

    public String getjId() {
        return jId;
    }

    public void setjId(String jId) {
        this.jId = jId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Like> getLikes() {
        return likes;
    }

    public void setLikes(List<Like> likes) {
        this.likes = likes;
    }

    public String getMemType() {
        return memType;
    }

    public void setMemType(String memType) {
        this.memType = memType;
    }

    @Override
    public int compareTo(Memories child) {
        return (createdAt > child.createdAt) ? -1 : 1;
    }

/*    public void updateLikedBy(Context context, String memId, List<String> likedBy) {

    }*/

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String isMemoryLikedByCurrentUser(Context context){
        for(Like like : likes){
            if (like.getUserId().equals(TJPreferences.getUserId(context))) {
                return like.getId();
            }
        }
        return null;
    }

    public Like getLikeById(String likeId){
        for(Like like : likes){
            if (like.getId().equals(likeId)) {
                return like;
            }
        }
        return null;
    }
}
