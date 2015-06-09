package com.example.memories.models;

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

    //To make contact object from parcel
    public Contact(Parcel parcel) {
        this.idOnServer = parcel.readString();
        this.name = parcel.readString();
        this.primaryEmail = parcel.readString();
        this.status = parcel.readString();
        this.picServerUrl = parcel.readString();
        this.picLocalUrl = parcel.readString();
        this.phone_no = parcel.readString();
        this.allJourneyIds = parcel.readString();
        this.isOnBoard = parcel.readByte() == 1 ? true : false;
        this.interests = parcel.readString();
        this.isSelected = parcel.readByte() == 1 ? true : false;
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

    @Override
    public String toString() {
        return "id on server -> " + this.getIdOnServer() + "\n" +
                "name -> " + this.getName() + "\n" +
                "primary email -> " + this.getPrimaryEmail() + "\n" +
                "status -> " + this.getStatus() + "\n" +
                "pic server url -> " + this.getPicServerUrl() + "\n" +
                "pic local url -> " + this.getPicLocalUrl() + "\n" +
                "phone number -> " + this.getPhone_no() + "\n" +
                "is on board -> " + this.isOnBoard() + "\n" +
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
        parcel.writeString(name);
        parcel.writeString(primaryEmail);
        parcel.writeString(status);
        parcel.writeString(picServerUrl);
        parcel.writeString(picLocalUrl);
        parcel.writeString(phone_no);
        parcel.writeString(allJourneyIds);
        parcel.writeByte((byte) (isOnBoard ? 1 : 0));
        parcel.writeString(interests);
        parcel.writeByte((byte) (isSelected() ? 1 : 0));
    }

    @Override
    public int compareTo(Contact child) {
        return name.compareTo(child.name);
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
