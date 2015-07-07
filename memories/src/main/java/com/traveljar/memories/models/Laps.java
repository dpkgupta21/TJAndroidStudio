package com.traveljar.memories.models;

public class Laps {
    private String id;
    private String idOnServer;
    private String journeyId;
    private String sourcePlaceId;
    private String destinationPlaceId;
    private int conveyanceMode;
    private long startDate;

    public Laps(){

    }

    public Laps(String id, String idOnServer, String journeyId, String sourcePlaceId, String destinationPlaceId, int conveyanceMode,
               long startDate) {
        this.id = id;
        this.idOnServer = idOnServer;
        this.journeyId = journeyId;
        this.sourcePlaceId = sourcePlaceId;
        this.destinationPlaceId = destinationPlaceId;
        this.conveyanceMode = conveyanceMode;
        this.startDate = startDate;
    }

    public String getJourneyId() {
        return journeyId;
    }

    public void setJourneyId(String journeyId) {
        this.journeyId = journeyId;
    }

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

    public String getSourcePlaceId() {
        return sourcePlaceId;
    }

    public void setSourcePlaceId(String sourcePlaceId) {
        this.sourcePlaceId = sourcePlaceId;
    }

    public String getDestinationPlaceId() {
        return destinationPlaceId;
    }

    public void setDestinationPlaceId(String destinationPlaceId) {
        this.destinationPlaceId = destinationPlaceId;
    }

    public int getConveyanceMode() {
        return conveyanceMode;
    }

    public void setConveyanceMode(int conveyanceMode) {
        this.conveyanceMode = conveyanceMode;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }
}
