package com.traveljar.memories.models;

/**
 * Created by deeksha.chaturvedi on 05/09/2015.
 */
public class MakeRequest {
    private String name;
    private String location;
    private int audioResource;
    private boolean isPlaying;
    private boolean isChecked;


    public boolean isPlaying() {return isPlaying;    }

    public void setIsPlaying(boolean isPlaying) {     this.isPlaying = isPlaying;    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public String getName() {    return name;    }

    public void setName(String name) {  this.name = name;    }

    public int getAudioResource() {
        return audioResource;
    }

    public void setAudioResource(int audioResource) {
        this.audioResource = audioResource;
    }



}
