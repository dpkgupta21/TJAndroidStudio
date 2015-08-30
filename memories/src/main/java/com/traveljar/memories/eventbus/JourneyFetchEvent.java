package com.traveljar.memories.eventbus;

import com.traveljar.memories.models.Journey;

public class JourneyFetchEvent {

    private Journey journey;
    private boolean success;
    private int callerCode;

    public JourneyFetchEvent(Journey journey, boolean success, int callerCode) {
        this.journey = journey;
        this.success = success;
        this.callerCode = callerCode;
    }

    public Journey getJourney() {
        return journey;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getCallerCode() {
        return callerCode;
    }

}
