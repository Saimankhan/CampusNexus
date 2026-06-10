package com.example.your_campus_app.model;

public class UserProfileModel {
    private String uid;
    private String fullName, mobile, institution, profilePhotoUrl, level, email;
    private boolean isAdmin, isSuperAdmin;

    // Firebase database theke data read korar jonno empty constructor oboshshoi lage
    public UserProfileModel() {
        // firebase needs empty public constructor
    }

    // Data initialize korar jonno parameterized constructor
    public UserProfileModel(String uid, String fullName, String mobile, String level, String institution, String profilePhotoUrl, String email) {
        this.uid = uid;
        this.fullName = fullName;
        this.mobile = mobile;
        this.level = level;
        this.institution = institution;
        this.profilePhotoUrl = profilePhotoUrl;
        this.isAdmin = false; // Default bhabe false thakbe
        this.isSuperAdmin = false;
        this.email = email;
    }

    // --- Getters and Setters ---

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email; // Eikhane apnar 'emial' spelling mistake ti fix kora hoyeche
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isSuperAdmin() {
        return isSuperAdmin;
    }

    public void setSuperAdmin(boolean superAdmin) {
        isSuperAdmin = superAdmin;
    }
}