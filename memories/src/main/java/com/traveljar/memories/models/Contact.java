package com.traveljar.memories.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Contact implements Comparable<Contact>, Parcelable {

    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {

        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };
    private String idOnServer;
    private String profileName;
    private String phoneBookName;
    private String primaryEmail;
    private String status;
    private String picServerUrl;
    private String picLocalUrl;
    private String phoneNo;
    private String allJourneyIds;
    private boolean isOnBoard;
    private String interests;
    private boolean isSelected;

    public Contact() {

    }

    public Contact(String idOnServer, String profileName, String phoneBookName, String primaryEmail, String status,
                   String picServerUrl, String picLocalUrl, String phoneNo, String allJourneyIds, boolean isOnBoard,
                   String interests) {
        this.idOnServer = idOnServer;
        this.profileName = profileName;
        this.phoneBookName = phoneBookName;
        this.status = status;
        this.primaryEmail = primaryEmail;
        this.picServerUrl = picServerUrl;
        this.picLocalUrl = picLocalUrl;
        this.phoneNo = phoneNo;
        this.isOnBoard = isOnBoard;
    }

    //To make contact object from parcel
    public Contact(Parcel parcel) {
        this.idOnServer = parcel.readString();
        this.profileName = parcel.readString();
        this.phoneBookName = parcel.readString();
        this.primaryEmail = parcel.readString();
        this.status = parcel.readString();
        this.picServerUrl = parcel.readString();
        this.picLocalUrl = parcel.readString();
        this.phoneNo = parcel.readString();
        this.allJourneyIds = parcel.readString();
        this.isOnBoard = parcel.readByte() == 1 ? true : false;
        this.interests = parcel.readString();
        this.isSelected = parcel.readByte() == 1 ? true : false;
    }

    public String getPhoneBookName() {
        return phoneBookName;
    }

    public void setPhoneBookName(String phoneBookName) {
        this.phoneBookName = phoneBookName;
    }

    public String getIdOnServer() {
        return idOnServer;
    }

    public void setIdOnServer(String idOnServer) {
        this.idOnServer = idOnServer;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
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

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
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

    @Override
    public String toString() {
        return "id on server -> " + this.getIdOnServer() +
                "profileName -> " + this.getProfileName() +
                "primary email -> " + this.getPrimaryEmail() +
                "status -> " + this.getStatus() +
                "pic server url -> " + this.getPicServerUrl() +
                "pic local url -> " + this.getPicLocalUrl() +
                "phone number -> " + this.getPhoneNo() +
                "is on board -> " + this.isOnBoard() +
                "all journey ids -> " + this.getAllJourneyIds();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    //Methods to make Contact class Parcelable
    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(idOnServer);
        parcel.writeString(profileName);
        parcel.writeString(phoneBookName);
        parcel.writeString(primaryEmail);
        parcel.writeString(status);
        parcel.writeString(picServerUrl);
        parcel.writeString(picLocalUrl);
        parcel.writeString(phoneNo);
        parcel.writeString(allJourneyIds);
        parcel.writeByte((byte) (isOnBoard ? 1 : 0));
        parcel.writeString(interests);
        parcel.writeByte((byte) (isSelected() ? 1 : 0));
    }

    @Override
    public int compareTo(Contact child) {
        return profileName.compareTo(child.profileName);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Contact)) {
            return false;
        }
        Contact other = (Contact) o;
        return idOnServer.equals(other.idOnServer);
    }

    @Override
    public int hashCode() {
        return idOnServer.hashCode();
    }

}
