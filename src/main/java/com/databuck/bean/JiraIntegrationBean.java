package com.databuck.bean;

public class JiraIntegrationBean {
    private long id;
    private String msgBody;
    private String ticketProcessStatus;
    private String ticketSubmitStatus;
    private String createdAt;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(String msgBody) {
        this.msgBody = msgBody;
    }

    public String getTicketProcessStatus() {
        return ticketProcessStatus;
    }

    public void setTicketProcessStatus(String ticketProcessStatus) {
        this.ticketProcessStatus = ticketProcessStatus;
    }

    public String getTicketSubmitStatus() {
        return ticketSubmitStatus;
    }

    public void setTicketSubmitStatus(String ticketSubmitStatus) {
        this.ticketSubmitStatus = ticketSubmitStatus;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
