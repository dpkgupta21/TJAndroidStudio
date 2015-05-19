package com.example.memories.models;

import java.util.ArrayList;

public class User {
    private long id;
    private String name;
    private String primaryEmail;
    private String pswrd;
    private long phone_no;
    private String profilePic;
    private String handle;
    private long joinedOn;
    private ArrayList<Long> journeyList;
    private ArrayList<Long> connections;
    private ArrayList<String> interests;

    public User(String name, String primaryEmail, String pswrd, long joinedOn) {
        this.name = name;
        this.primaryEmail = primaryEmail;
        this.pswrd = pswrd;
        this.joinedOn = joinedOn;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrimaryEmail() {
        return primaryEmail;
    }

    public void setPrimaryEmail(String primaryEmail) {
        this.primaryEmail = primaryEmail;
    }

    public long getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(long phone_no) {
        this.phone_no = phone_no;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public long getJoinedOn() {
        return joinedOn;
    }

    public void setJoinedOn(long joinedOn) {
        this.joinedOn = joinedOn;
    }

    public ArrayList<Long> getJourneyList() {
        return journeyList;
    }

    public void setJourneyList(ArrayList<Long> journeyList) {
        this.journeyList = journeyList;
    }

    public ArrayList<Long> getConnections() {
        return connections;
    }

    public void setConnections(ArrayList<Long> connections) {
        this.connections = connections;
    }

    public ArrayList<String> getInterests() {
        return interests;
    }

    public void setInterests(ArrayList<String> interests) {
        this.interests = interests;
    }

    public String getPswrd() {
        return pswrd;
    }

    public void setPswrd(String pswrd) {
        this.pswrd = pswrd;
    }

}
