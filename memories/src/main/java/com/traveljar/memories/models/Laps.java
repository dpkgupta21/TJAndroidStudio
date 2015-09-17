package com.traveljar.memories.models;

import java.util.List;

public class Laps {
    private String id;
    private String idOnServer;
    private String journeyId;
    private String sourcePlaceId;
    private String sourceCityName;
    private String sourceStateName;
    private String sourceCountryName;

    private String destinationPlaceId;
    private String destinationCityName;
    private String destinationStateName;
    private String destinationCountryName;
    private int conveyanceMode;
    private long startDate;

    public Laps(){

    }

    public Laps(String id, String idOnServer, String journeyId,
                String sourcePlaceId,
                String destinationPlaceId,
                int conveyanceMode,
                long startDate) {
        this.id = id;
        this.idOnServer = idOnServer;
        this.journeyId = journeyId;
        this.sourcePlaceId = sourcePlaceId;
        this.destinationPlaceId = destinationPlaceId;
        this.conveyanceMode = conveyanceMode;
        this.startDate = startDate;
    }


//    public Laps(String id, String idOnServer, String journeyId,
//                String sourcePlaceId, String sourceCityName, String sourceStateName, String sourceCountryName,
//                String destinationPlaceId, String destinationCityName, String destinationStateName, String destinationCountryName,
//                int conveyanceMode,
//               long startDate) {
//        this.id = id;
//        this.idOnServer = idOnServer;
//        this.journeyId = journeyId;
//        this.sourcePlaceId = sourcePlaceId;
//        this.sourceCityName=sourceCityName;
//        this.sourceStateName=sourceStateName;
//        this.sourceCountryName=sourceCountryName;
//        this.destinationPlaceId = destinationPlaceId;
//        this.destinationCityName=destinationCityName;
//        this.destinationStateName=destinationStateName;
//        this.destinationCountryName=destinationCountryName;
//        this.conveyanceMode = conveyanceMode;
//        this.startDate = startDate;
//    }


    public String getSourceCityName() {
        return sourceCityName;
    }

    public void setSourceCityName(String sourceCityName) {
        this.sourceCityName = sourceCityName;
    }

    public String getSourceStateName() {
        return sourceStateName;
    }

    public void setSourceStateName(String sourceStateName) {
        this.sourceStateName = sourceStateName;
    }

    public String getSourceCountryName() {
        return sourceCountryName;
    }

    public void setSourceCountryName(String sourceCountryName) {
        this.sourceCountryName = sourceCountryName;
    }

    public String getDestinationCityName() {
        return destinationCityName;
    }

    public void setDestinationCityName(String destinationCityName) {
        this.destinationCityName = destinationCityName;
    }

    public String getDestinationStateName() {
        return destinationStateName;
    }

    public void setDestinationStateName(String destinationStateName) {
        this.destinationStateName = destinationStateName;
    }

    public String getDestinationCountryName() {
        return destinationCountryName;
    }

    public void setDestinationCountryName(String destinationCountryName) {
        this.destinationCountryName = destinationCountryName;
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


    public static Laps getLapFromLapsList(List<Laps> lapsList, String lapsId){
        for (Laps laps : lapsList) {
            if (laps.getId().equals(lapsId)) {
                return laps;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "id->" + id +
                "journey_id->" + journeyId +
                "sourceCityName->" + sourceCityName +
                "journey_id->" + journeyId;

    }
}
