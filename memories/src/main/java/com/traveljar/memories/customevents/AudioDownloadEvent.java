package com.traveljar.memories.customevents;

import com.traveljar.memories.models.Audio;

public class AudioDownloadEvent {

    private Audio audio;
    private boolean success;
    private int callerCode;

    public AudioDownloadEvent(Audio audio, boolean success, int callerCode) {
        this.audio = audio;
        this.success = success;
        this.callerCode = callerCode;
    }

    public Audio getAudio() {
        return audio;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getCallerCode() {
        return callerCode;
    }
}
