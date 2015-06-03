package com.example.memories.models;

import java.util.List;

public class Journey {
    private String idOnServer;
    private String name;
    private String tagLine;
    private String groupType;
    private String createdBy;
    private List<String> laps;
    private List<String> buddies;
    private String journeyStatus;

    public Journey() {

    }

    public Journey(String idOnServer, String name, String tagLine, String groupType,
                   String createdBy, List<String> jLaps, List<String> buddies, String journeyStatus) {
        this.idOnServer = idOnServer;
        this.name = name;
        this.tagLine = tagLine;
        this.groupType = groupType;
        this.createdBy = createdBy;
        this.laps = jLaps;
        this.buddies = buddies;
        this.journeyStatus = journeyStatus;
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

    public boolean isAdmin(String userId){
        return userId.equals(this.getIdOnServer());
    }

    @Override
    public String toString(){
        return "id on server -> " + this.getIdOnServer()+"\n"+
                "name -> " + this.getName()+"\n"+
                "tagline -> " + this.getTagLine()+"\n"+
                "group type -> " + this.getGroupType()+"\n"+
                "created by -> " + this.getGroupType()+"\n"+
                "laps -> " + this.getLaps()+"\n"+
                "buddies -> " + this.getBuddies()+"\n"+
                "journey status -> " + this.getJourneyStatus();
    }
}
