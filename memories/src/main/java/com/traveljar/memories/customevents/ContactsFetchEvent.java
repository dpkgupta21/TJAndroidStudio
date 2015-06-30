package com.traveljar.memories.customevents;

public class ContactsFetchEvent {

    private final String message;
    private final int activityCode;
    private final boolean success;

    public ContactsFetchEvent(String message, int activityCode, boolean success){
        this.message = message;
        this.activityCode = activityCode;
        this.success = success;
    }

    public String getMessage(){
        return message;
    }

    public int getActivityCode(){
        return activityCode;
    }

    public boolean isSuccess() {
        return success;
    }
}
