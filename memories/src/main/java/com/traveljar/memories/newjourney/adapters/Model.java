package com.traveljar.memories.newjourney.adapters;

public class Model implements Comparable<Model> {

    private String id;
    private String name;
    private String phone_no;
    private String email;
    private String image_uri;
    private boolean isOnBoard;
    private boolean selected;

    public Model() {

    }

    public Model(String name) {
        this.name = name;
        selected = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage_uri() {
        return image_uri;
    }

    public void setImage_uri(String image_uri) {
        this.image_uri = image_uri;
    }

    public boolean isOnBoard() {
        return isOnBoard;
    }

    public void setOnBoard(boolean isOnBoard) {
        this.isOnBoard = isOnBoard;
    }

    @Override
    public int compareTo(Model another) {
        return name.compareTo(another.name);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}