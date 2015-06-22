package com.traveljar.memories.models;

/**
 * Created by ankit on 22/6/15.
 */
public class Request {

    private String id;
    private String localId;
    private String journeyId;
    private int operationType;
    private int categoryType;
    private int requestStatus;


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


    public Request(){

    }

    public Request(String id, String localId, String journeyId, int operationType, int categoryType, int requestStatus) {
        this.id = id;
        this.localId = localId;
        this.journeyId = journeyId;
        this.operationType = operationType;
        this.categoryType = categoryType;
        this.requestStatus = requestStatus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocalId() {
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
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

}
