package com.databuck.bean;

import org.springframework.context.annotation.Bean;


public class LoggingActivity {
    private int rowId;
    private int userId;
    private String userName;
    private String accessUrl;
    private String databuckFeature;
    private String sessionId;
    private String activityLogTime;
    private int entityId;
    private String entityName;
    private String activityName;

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

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

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }
}
