package com.traveljar.memories.customevents;

import com.traveljar.memories.models.CheckIn;

public class CheckInDownloadEvent {
    private CheckIn checkIn;
    private boolean success;
    private int callerCode;

    public CheckInDownloadEvent(CheckIn checkIn, boolean success, int callerCode) {
        this.checkIn = checkIn;
        this.success = success;
        this.callerCode = callerCode;
    }

    public CheckIn getCheckIn() {
        return checkIn;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getCallerCode() {
        return callerCode;
    }
}
