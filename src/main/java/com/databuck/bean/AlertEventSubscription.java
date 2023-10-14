package com.databuck.bean;

public class AlertEventSubscription {
    private int alertSubId;
    private int projectId;
    private int eventId;
    private int commModeId;
    private String isGlobalSubscription;
    private String communicationValues;
    private String communicaionMode;

    public String getCommunicaionMode() {
        return communicaionMode;
    }

    public void setCommunicaionMode(String communicaionMode) {
        this.communicaionMode = communicaionMode;
    }

    public int getAlertSubId() {
        return alertSubId;
    }

    public void setAlertSubId(int alertSubId) {
        this.alertSubId = alertSubId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getCommModeId() {
        return commModeId;
    }

    public void setCommModeId(int commModeId) {
        this.commModeId = commModeId;
    }

    public String getIsGlobalSubscription() {
        return isGlobalSubscription;
    }

    public void setIsGlobalSubscription(String isGlobalSubscription) {
        this.isGlobalSubscription = isGlobalSubscription;
    }

    public String getCommunicationValues() {
        return communicationValues;
    }

    public void setCommunicationValues(String communicationValues) {
        this.communicationValues = communicationValues;
    }
    
}
