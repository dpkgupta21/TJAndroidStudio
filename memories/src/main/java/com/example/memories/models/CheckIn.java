package com.example.memories.models;

import android.content.Context;

import com.example.memories.SQLitedatabase.CheckinDataSource;

import java.util.List;

public class CheckIn extends Memories {
    private String caption;
    private String checkInPlaceName;
    private String checkInPicURL;
    private List<String> checkInWith;

    public CheckIn() {

    }

    public CheckIn(String idOnServer, String jId, String memType, String caption, double lat,
                   double longi, String checkInPlaceName, String checkInPicURL,
                   List<String> checkInWith, String createdBy, long createdAt, long updatedAt, List<String> likedBy) {
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
        this.likedBy = likedBy;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
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

    public void updateLikedBy(Context context, String memId, List<String> likedBy) {
        CheckinDataSource.updateFavourites(context, memId, likedBy);
    }

    @Override
    public String toString() {
        return "id on server -> " + this.getIdOnServer() + "\n" +
                "journey id -> " + this.getjId() + "\n" +
                "memory type -> " + this.getMemType() + "\n" +
                "created by -> " + this.getCreatedBy() + "\n" +
                "created at -> " + this.getCreatedAt() + "\n" +
                "liked by -> " + this.getLikedBy() + "\n" +
                "checkin caption -> " + this.getCaption() + "\n" +
                "latitude -> " + this.getLatitude() + "\n" +
                "longitude -> " + this.getLongitude() + "\n" +
                "checkin place name -> " + this.getCheckInPlaceName() + "\n" +
                "checkin with -> " + this.getCheckInWith();

    }

}
