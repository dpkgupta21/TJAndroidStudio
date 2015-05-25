package com.example.memories.models;

import android.content.Context;

import com.example.memories.SQLitedatabase.CheckinDataSource;

import java.util.List;

public class CheckIn extends Memories {
    private String caption;
    private double latitude;
    private double longitude;
    private String checkInPlaceName;
    private String checkInPicURL;
    private List<String> checkInWith;

    public CheckIn() {

    }

    public CheckIn(String idOnServer, String jId, String memType, String caption, double lat,
                   double longi, String checkInPlaceName, String checkInPicURL,
                   List<String> checkInWith, String createdBy, long createdAt, long updatedAt) {
        this.idOnServer = idOnServer;
        this.jId = jId;
        this.memType = memType;
        this.caption = caption;
        this.latitude = lat;
        this.longitude = longi;
        this.checkInPlaceName = checkInPlaceName;
        this.checkInPicURL = checkInPicURL;
        this.checkInWith = checkInWith;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCheckInPlaceName() {
        return checkInPlaceName;
    }

    public void setCheckInPlaceName(String checkInPlaceName) {
        this.checkInPlaceName = checkInPlaceName;
    }

    public String getCheckInPicURL() {
        return checkInPicURL;
    }

    public void setCheckInPicURL(String checkInPicURL) {
        this.checkInPicURL = checkInPicURL;
    }

    public List<String> getCheckInWith() {
        return checkInWith;
    }

    public void setCheckInWith(List<String> checkInWith) {
        this.checkInWith = checkInWith;
    }

    public void updateLikedBy(Context context, String memId, String likedBy) {
        CheckinDataSource.updateFavourites(context, memId, likedBy);
    }
}
