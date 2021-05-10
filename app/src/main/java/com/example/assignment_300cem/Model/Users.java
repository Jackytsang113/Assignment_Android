package com.example.assignment_300cem.Model;

public class Users {
    private String userId;
    private String username;
    private String email;
    private String image_url;

    public Users() {
    }

    public Users(String userId, String username, String email, String image_url) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.image_url = image_url;
    }

    public Users(String userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.image_url = "https://firebasestorage.googleapis.com/v0/b/assignment-300cem-35669.appspot.com/o/placeholder_person.png?alt=media&token=72749e49-d5ee-4907-a324-3ab1a624746b";
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
