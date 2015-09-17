package com.traveljar.memories.models;

/**
 * Created by abhi on 26/06/15.
 */
public class Timecapsule {

    private String id;
    private String idOnServer;
    private String jId;
    private String videoLocalURL;
    private String videoServerURL;
    private String localThumbPath;
    private String caption;
    private String extension;
    private long size;
    private String createdBy;
    private boolean makeVideo;

    public Timecapsule(){

    }

    public Timecapsule(String idOnServer, String jId, String videoLocalURL, String videoServerURL, String localThumbPath,
                       String caption, String extension, long size, String createdBy, long createdAt, long updatedAt) {
        this.idOnServer = idOnServer;
        this.jId = jId;
        this.videoLocalURL = videoLocalURL;
        this.videoServerURL = videoServerURL;
        this.localThumbPath = localThumbPath;
        this.caption = caption;
        this.extension = extension;
        this.size = size;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    private long createdAt;
    private long updatedAt;

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

    public String getjId() {
        return jId;
    }

    public void setjId(String jId) {
        this.jId = jId;
    }

    public String getVideoLocalURL() {
        return videoLocalURL;
    }

    public void setVideoLocalURL(String videoLocalURL) {
        this.videoLocalURL = videoLocalURL;
    }

    public String getVideoServerURL() {
        return videoServerURL;
    }

    public void setVideoServerURL(String videoServerURL) {
        this.videoServerURL = videoServerURL;
    }

    public String getLocalThumbPath() {
        return localThumbPath;
    }

    public void setLocalThumbPath(String localThumbPath) {
        this.localThumbPath = localThumbPath;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isMakeVideo() { return makeVideo; }

    public void setMakeVideo(boolean makeVideo) {   this.makeVideo = makeVideo;    }

}
