package com.example.your_campus_app.model;

import com.google.firebase.Timestamp;

public class NewsPostModel {

    private String postId;
    private String title;
    private String desc;
    private String imageUrl;
    private String uid;
    private String type;
    private Boolean isLive;
    private Timestamp dateTime;
    private Boolean isRejected;
    private String campus;

    // Firebase Required Empty Constructor
    public NewsPostModel() {
    }

    // Constructor used in AddNewsFragment
    public NewsPostModel(String postId,
                         String title,
                         String desc,
                         String imageUrl,
                         String uid,
                         Timestamp timestamp) {

        this.postId = postId;
        this.title = title;
        this.desc = desc;
        this.imageUrl = imageUrl;
        this.uid = uid;
        this.dateTime = timestamp;

        this.isLive = false;
        this.isRejected = false;
    }

    // Full Constructor
    public NewsPostModel(String postId,
                         String title,
                         String desc,
                         String imageUrl,
                         String uid,
                         Timestamp dateTime,
                         String type,
                         String campus) {

        this.postId = postId;
        this.title = title;
        this.desc = desc;
        this.imageUrl = imageUrl;
        this.uid = uid;
        this.dateTime = dateTime;
        this.type = type;
        this.campus = campus;

        this.isLive = false;
        this.isRejected = false;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }

    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getLive() {
        return isLive;
    }

    public void setLive(Boolean live) {
        isLive = live;
    }

    public Boolean getRejected() {
        return isRejected;
    }

    public void setRejected(Boolean rejected) {
        isRejected = rejected;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }
}