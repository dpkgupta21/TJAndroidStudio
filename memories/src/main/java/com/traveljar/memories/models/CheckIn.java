package com.traveljar.memories.models;

import java.util.List;


public class CheckIn extends Memories {
    private String caption;
    private String checkInPlaceName;
    private String checkInPicLocalURL;
    private List<String> checkInWith;
    private String checkInPicServerPath;
    private String checkInPicThumbPath;

    public CheckIn() {

    }

    public CheckIn(String idOnServer, String jId, String memType, String caption, double lat, double longi, String checkInPlaceName, String checkInPicLocalURL,
                   List<String> checkInWith, String createdBy, long createdAt, long updatedAt) {
        this.idOnServer = idOnServer;
        this.jId = jId;
        this.memType = memType;
        this.caption = caption;
        this.latitude = lat;
        this.longitude = longi;
        this.checkInPlaceName = checkInPlaceName;
        this.checkInPicLocalURL = checkInPicLocalURL;
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

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCheckInPlaceName() {
        return checkInPlaceName;
    }

    public void setCheckInPlaceName(String checkInPlaceName) {
        this.checkInPlaceName = checkInPlaceName;
    }

    public String getCheckInPicLocalURL() {
        return checkInPicLocalURL;
    }

    public void setCheckInPicLocalURL(String checkInPicLocalURL) {
        this.checkInPicLocalURL = checkInPicLocalURL;
    }

    public String getCheckInPicServerPath() {
        return checkInPicServerPath;
    }

    public void setCheckInPicServerPath(String checkInPicServerPath) {
        this.checkInPicServerPath = checkInPicServerPath;
    }

    public String getCheckInPicThumbPath() {
        return checkInPicThumbPath;
    }

    public void setCheckInPicThumbPath(String checkInPicThumbPath) {
        this.checkInPicThumbPath = checkInPicThumbPath;
    }

    public List<String> getCheckInWith() {
        return checkInWith;
    }

    public void setCheckInWith(List<String> checkInWith) {
        this.checkInWith = checkInWith;
    }

    @Override
    public String toString() {
        return "id on server -> " + this.getIdOnServer() +
                "journey id -> " + this.getjId() +
                "memory type -> " + this.getMemType() +
                "created by -> " + this.getCreatedBy() +
                "created at -> " + this.getCreatedAt() +
                "liked by -> " + this.getLikes() +
                "checkin caption -> " + this.getCaption() +
                "latitude -> " + this.getLatitude() +
                "longitude -> " + this.getLongitude() +
                "checkin place name -> " + this.getCheckInPlaceName() +
                "checkin with -> " + this.getCheckInWith();

    }

}
