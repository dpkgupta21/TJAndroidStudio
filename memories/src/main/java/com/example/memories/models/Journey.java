package com.example.memories.models;

import java.util.List;

public class Journey implements Comparable<Journey> {
    private String idOnServer;
    private String name;
    private String tagLine;
    private String groupType;
    private String createdBy;
    private List<String> laps;
    private List<String> buddies;
    private String journeyStatus;
    private long createdAt;
    private long updatedAt;
    private long completedAt;

    public Journey() {

    }

    public Journey(String idOnServer, String name, String tagLine, String groupType,
                   String createdBy, List<String> jLaps, List<String> buddies, String journeyStatus
            , long createdAt, long updatedAt, long completedAt) {
        this.idOnServer = idOnServer;
        this.name = name;
        this.tagLine = tagLine;
        this.groupType = groupType;
        this.createdBy = createdBy;
        this.laps = jLaps;
        this.buddies = buddies;
        this.journeyStatus = journeyStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.completedAt = completedAt;
    }

    public String getIdOnServer() {
        return idOnServer;
    }

    public void setIdOnServer(String idOnServer) {
        this.idOnServer = idOnServer;
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

    public long getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(long completedAt) {
        this.completedAt = completedAt;
    }

    public void setId(String idOnServer) {
        this.idOnServer = idOnServer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTagLine() {
        return tagLine;
    }

    public void setTagLine(String tagLine) {
        this.tagLine = tagLine;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public List<String> getLaps() {
        return laps;
    }

    public void setLaps(List<String> laps) {
        this.laps = laps;
    }

    public List<String> getBuddies() {
        return buddies;
    }

    public void setBuddies(List<String> buddies) {
        this.buddies = buddies;
    }

    public String getJourneyStatus() {
        return journeyStatus;
    }

    public void setJourneyStatus(String journeyStatus) {
        this.journeyStatus = journeyStatus;
    }

    public boolean isAdmin(String userId) {
        return userId.equals(this.getIdOnServer());
    }

    @Override
    public String toString() {
        return "id on server -> " + this.getIdOnServer() +
                "name -> " + this.getName() +
                "tagline -> " + this.getTagLine() +
                "group type -> " + this.getGroupType() +
                "created by -> " + this.getCreatedBy() +
                "laps -> " + this.getLaps() +
                "buddies -> " + this.getBuddies() +
                "journey status -> " + this.getJourneyStatus();
    }

    @Override
    public int compareTo(Journey another) {
        if (this.createdAt > (another.createdAt)) {
            return -1;
        } else {
            return 1;
        }
    }
}
