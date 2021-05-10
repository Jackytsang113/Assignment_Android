package com.example.assignment_300cem.Model;

public class Posts {
    private String postId;
    private String mapLatitude;
    private String mapLongitude;
    private String postImage;
    private String locationName;
    private String description;
    private String sender;

    public Posts() {
    }

    public Posts(String postId, String mapLatitude, String mapLongitude, String postImage, String locationName, String description, String sender) {
        this.postId = postId;
        this.mapLatitude = mapLatitude;
        this.mapLongitude = mapLongitude;
        this.postImage = postImage;
        this.locationName = locationName;
        this.description = description;
        this.sender = sender;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getMapLatitude() {
        return mapLatitude;
    }

    public void setMapLatitude(String mapLatitude) {
        this.mapLatitude = mapLatitude;
    }

    public String getMapLongitude() {
        return mapLongitude;
    }

    public void setMapLongitude(String mapLongitude) {
        this.mapLongitude = mapLongitude;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
