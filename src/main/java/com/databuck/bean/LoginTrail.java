package com.databuck.bean;


public class LoginTrail {
    private int userId;
    private String userName;
    private String accessUrl;
    private String databuckFeature;
    private String sessionId;
    private String activityLogTime;


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAccessUrl() {
        return accessUrl;
    }

    public void setAccessUrl(String accessUrl) {
        this.accessUrl = accessUrl;
    }

    public String getDatabuckFeature() {
        return databuckFeature;
    }

    public void setDatabuckFeature(String databuckFeature) {
        this.databuckFeature = databuckFeature;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getActivityLogTime() {
        return activityLogTime;
    }

    public void setActivityLogTime(String activityLogTime) {
        this.activityLogTime = activityLogTime;
    }
}
