package com.example.memories.models;

import java.util.ArrayList;

public class Journey {
    private String idOnServer;
    private String name;
    private String tagLine;
    private String groupType;
    private String createdBy;
    private ArrayList<String> laps;
    private ArrayList<String> buddies;
    private int isActive;

    public Journey() {

    }

    public Journey(String idOnServer, String name, String tagLine, String groupType,
                   String createdBy, ArrayList<String> jLaps, ArrayList<String> buddies, int isActive) {
        this.idOnServer = idOnServer;
        this.name = name;
        this.tagLine = tagLine;
        this.groupType = groupType;
        this.createdBy = createdBy;
        this.laps = jLaps;
        this.buddies = buddies;
        this.isActive = isActive;
    }

    public String getIdOnServer() {
        return idOnServer;
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

    public ArrayList<String> getLaps() {
        return laps;
    }

    public void setLaps(ArrayList<String> laps) {
        this.laps = laps;
    }

    public ArrayList<String> getBuddies() {
        return buddies;
    }

    public void setBuddies(ArrayList<String> buddies) {
        this.buddies = buddies;
    }

    public int isActive() {
        return isActive;
    }

    public void setActive(int isActive) {
        this.isActive = isActive;
    }
}
