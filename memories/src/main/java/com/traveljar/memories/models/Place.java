package com.traveljar.memories.models;

/**
 * Created by abhi on 05/06/15.
 */
public class Place {

    private String id;
    private String idOnServer;
    private String idOnGoogle;
    private String country;
    private String state;
    private String city;
    private String createdBy;
    private long createdAt;

    public Place(String id, String idOnServer, String idOnGoogle, String country, String state, String city, String createdBy, long createdAt) {
        this.id = id;
        this.idOnServer = idOnServer;
        this.idOnGoogle = idOnGoogle;
        this.country = country;
        this.state = state;
        this.city = city;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
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

    public String getIdOnGoogle() {
        return idOnGoogle;
    }

    public void setIdOnGoogle(String idOnGoogle) {
        this.idOnGoogle = idOnGoogle;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
