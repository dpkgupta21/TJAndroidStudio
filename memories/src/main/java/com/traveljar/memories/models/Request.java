package com.traveljar.memories.models;

/**
 * Created by ankit on 22/6/15.
 */
public class Request {

    private String id;
    private String objectLocalId;
    private String journeyId;
    private int operationType;
    private int categoryType;
    private int requestStatus;
    private int noOfAttempts;


    public static final int OPERATION_TYPE_CREATE = 0;
    public static final int OPERATION_TYPE_UPLOAD = 1;
    public static final int OPERATION_TYPE_DELETE = 2;
    public static final int OPERATION_TYPE_FETCH = 3;
    public static final int OPERATION_TYPE_LIKE = 4;
    public static final int OPERATION_TYPE_UNLIKE = 5;

    public static final int CATEGORY_TYPE_AUDIO = 0;
    public static final int CATEGORY_TYPE_CHECKIN = 1;
    public static final int CATEGORY_TYPE_MOOD = 2;
    public static final int CATEGORY_TYPE_NOTE = 3;
    public static final int CATEGORY_TYPE_PICTURE = 4;
    public static final int CATEGORY_TYPE_VIDEO = 5;

    public static final int REQUEST_STATUS_NOT_STARTED = -1;
    public static final int REQUEST_STATUS_RUNNING = 0;
    public static final int REQUEST_STATUS_COMPLETED = 1;
    public static final int REQUEST_STATUS_FAILED = 0;


    public Request() {

    }

    public int getNoOfAttempts() {
        return noOfAttempts;
    }

    public void setNoOfAttempts(int noOfAttempts) {
        this.noOfAttempts = noOfAttempts;
    }

    public Request(String id, String objectLocalId, String journeyId, int operationType, int categoryType, int requestStatus, int noOfAttempts) {
        this.id = id;
        this.objectLocalId = objectLocalId;
        this.journeyId = journeyId;
        this.operationType = operationType;
        this.categoryType = categoryType;
        this.requestStatus = requestStatus;
        this.noOfAttempts = noOfAttempts;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObjectLocalId() {
        return objectLocalId;
    }

    public void setObjectLocalId(String objectLocalId) {
        this.objectLocalId = objectLocalId;
    }

    public String getJourneyId() {
        return journeyId;
    }

    public void setJourneyId(String journeyId) {
        this.journeyId = journeyId;
    }

    public int getOperationType() {
        return operationType;
    }

    public void setOperationType(int operationType) {
        this.operationType = operationType;
    }

    public int getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(int categoryType) {
        this.categoryType = categoryType;
    }

    public int getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(int requestStatus) {
        this.requestStatus = requestStatus;
    }

    // Returns the category code for a particular memory
    public static int getCategoryTypeFromMemory(Memories memory){
        if(memory instanceof Audio)
            return CATEGORY_TYPE_AUDIO;
        if(memory instanceof CheckIn)
            return CATEGORY_TYPE_CHECKIN;
        if(memory instanceof Mood)
            return CATEGORY_TYPE_MOOD;
        if(memory instanceof Note)
            return CATEGORY_TYPE_NOTE;
        if(memory instanceof Picture)
            return CATEGORY_TYPE_PICTURE;
        if(memory instanceof Video)
            return CATEGORY_TYPE_VIDEO;
        return 0;
    }

}
