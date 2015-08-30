package com.traveljar.memories.eventbus;

import com.traveljar.memories.models.Video;

public class VideoDownloadEvent {
    private Video video;
    private boolean success;
    private int callerCode;

    public VideoDownloadEvent(Video video, boolean success, int callerCode) {
        this.video = video;
        this.success = success;
        this.callerCode = callerCode;
    }

    public Video getVideo() {
        return video;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getCallerCode() {
        return callerCode;
    }
}
