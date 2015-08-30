package com.traveljar.memories.eventbus;

import com.traveljar.memories.models.Picture;

public class PictureDownloadEvent {
    private Picture picture;
    private boolean success;
    private int callerCode;

    public PictureDownloadEvent(Picture picture, boolean success, int callerCode) {
        this.picture = picture;
        this.success = success;
        this.callerCode = callerCode;
    }

    public Picture getPicture() {
        return picture;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getCallerCode() {
        return callerCode;
    }
}
