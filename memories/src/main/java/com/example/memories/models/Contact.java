package com.example.memories.models;

public class Contact implements Comparable<Contact> {
    private String idOnServer;
    private String name;
    private String primaryEmail;
    private String status;
    private String picServerUrl;
    private String picLocalUrl;
    private String phone_no;
    private String allJourneyIds;
    private boolean isOnBoard;
    private String interests;
    private boolean isSelected;

    public Contact() {

    }

    public Contact(String idOnServer, String name, String primaryEmail, String status,
                   String picServerUrl, String picLocalUrl, String phone_no, String allJourneyIds, boolean isOnBoard,
                   String interests) {
        this.idOnServer = idOnServer;
        this.name = name;
        this.primaryEmail = primaryEmail;
        this.picServerUrl = picServerUrl;
        this.picLocalUrl = picLocalUrl;
        this.phone_no = phone_no;
        this.isOnBoard = isOnBoard;
    }

    @Override
    public int compareTo(Contact child) {
        return name.compareTo(child.name);
    }

    public String getIdOnServer() {
        return idOnServer;
    }

    public void setIdOnServer(String idOnServer) {
        this.idOnServer = idOnServer;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPicServerUrl() {
        return picServerUrl;
    }

    public void setPicServerUrl(String picServerUrl) {
        this.picServerUrl = picServerUrl;
    }

    public String getPicLocalUrl() {
        return picLocalUrl;
    }

    public void setPicLocalUrl(String picLocalUrl) {
        this.picLocalUrl = picLocalUrl;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getAllJourneyIds() {
        return allJourneyIds;
    }

    public void setAllJourneyIds(String allJourneyIds) {
        this.allJourneyIds = allJourneyIds;
    }

    public boolean isOnBoard() {
        return isOnBoard;
    }

    public void setOnBoard(boolean isOnBoard) {
        this.isOnBoard = isOnBoard;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }


}
