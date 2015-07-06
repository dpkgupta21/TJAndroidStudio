package com.traveljar.memories.models;

import java.util.List;


public class CheckIn extends Memories {
    private String caption;
    private String checkInPlaceName;
    private String checkInPicLocalPath;
    private List<String> checkInWith;
    private String checkInPicServerUrl;
    private String checkInPicThumbUrl;

    public CheckIn() {

    }

    public CheckIn(String idOnServer, String jId, String memType, String caption, double lat, double longi, String checkInPlaceName,
                   String checkInPicLocalPath, String checkInPicServerUrl, String checkInPicThumbUrl, List<String> checkInWith,
                   String createdBy, long createdAt, long updatedAt) {
        this.idOnServer = idOnServer;
        this.jId = jId;
        this.memType = memType;
        this.caption = caption;
        this.latitude = lat;
        this.longitude = longi;
        this.checkInPlaceName = checkInPlaceName;
        this.checkInPicServerUrl = checkInPicServerUrl;
        this.checkInPicLocalPath = checkInPicLocalPath;
        this.checkInPicThumbUrl = checkInPicThumbUrl;
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

    public String getCheckInPicLocalPath() {
        return checkInPicLocalPath;
    }

    public void setCheckInPicLocalPath(String checkInPicLocalPath) {
        this.checkInPicLocalPath = checkInPicLocalPath;
    }

    public String getCheckInPicServerUrl() {
        return checkInPicServerUrl;
    }

    public void setCheckInPicServerUrl(String checkInPicServerUrl) {
        this.checkInPicServerUrl = checkInPicServerUrl;
    }

    public String getCheckInPicThumbUrl() {
        return checkInPicThumbUrl;
    }

    public void setCheckInPicThumbUrl(String checkInPicThumbUrl) {
        this.checkInPicThumbUrl = checkInPicThumbUrl;
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
