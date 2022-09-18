package com.dest.tapme.models;

public class User {
    String id;
    String username;
    String email;
    String buttonType;
    String profileImg;
    Integer duplicatePoint;

    public User() {
    }


    public User(String id, String username, String email, String buttonType, String profileImg, Integer duplicatePoint) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.buttonType = buttonType;
        this.profileImg = profileImg;
        this.duplicatePoint = duplicatePoint;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getButtonType() {
        return buttonType;
    }

    public void setButtonType(String buttonType) {
        this.buttonType = buttonType;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public Integer getDuplicatePoint() {
        return duplicatePoint;
    }

    public void setDuplicatePoint(Integer duplicatePoint) {
        this.duplicatePoint = duplicatePoint;
    }
}
