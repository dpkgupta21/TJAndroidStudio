package com.traveljar.memories.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ankit on 19/6/15.
 */
public class Like implements Parcelable {

    public static final Parcelable.Creator<Like> CREATOR = new Parcelable.Creator<Like>() {
        public Like createFromParcel(Parcel in) {
            return new Like(in);
        }

        public Like[] newArray(int size) {
            return new Like[size];
        }
    };

    private String id;
    private String idOnServer;
    private String journeyId;
    private String memoryLocalId;
    private String userId;
    private String memType;
    private boolean isValid;

    public boolean isValid() {
        return isValid;
    }

    public void setIsValid(boolean isValid) {
        this.isValid = isValid;
    }

    public Like() {

    }

    // Memorable id -> id of child element, memoryLocalId -> id of parent class
    public Like(String id, String idOnServer, String journeyId, String memoryLocalId, String userId, String memType, boolean isValid) {
        this.id = id;
        this.idOnServer = idOnServer;
        this.journeyId = journeyId;
        this.memoryLocalId = memoryLocalId;
        this.userId = userId;
        this.memType = memType;
        this.isValid = isValid;
    }

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

    public String getJourneyId() {
        return journeyId;
    }

    public void setJourneyId(String journeyId) {
        this.journeyId = journeyId;
    }

    public String getMemorableId() {
        return memoryLocalId;
    }

    public void setMemorableId(String memorableId) {
        this.memoryLocalId = memorableId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMemType() {
        return memType;
    }

    public void setMemType(String memType) {
        this.memType = memType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(id);
        parcel.writeString(idOnServer);
        parcel.writeString(journeyId);
        parcel.writeString(memoryLocalId);
        parcel.writeString(userId);
        parcel.writeString(memType);
        parcel.writeInt(isValid ? 1 : 0);
    }

    public Like(Parcel parcel) {
        id = parcel.readString();
        idOnServer = parcel.readString();
        journeyId = parcel.readString();
        memoryLocalId = parcel.readString();
        userId = parcel.readString();
        memType = parcel.readString();

        isValid = (parcel.readInt() == 1) ? true : false;
    }

    @Override
    public String toString(){
        return "like_id->" + id + ", user_id->" + userId + " isValid->" + isValid + " memtype->" + memType;
    }

}
