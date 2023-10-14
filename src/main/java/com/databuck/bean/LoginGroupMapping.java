package com.databuck.bean;

public class LoginGroupMapping {

    private int loginId;
    private String loginGroupName;
    private String approverGroup;
    private String assignedRoles;
    private String assignedProjects;

    public LoginGroupMapping(String loginGroupName, String approverGroup, String assignedRoles, String assignedProjects) {
        this.loginGroupName = loginGroupName;
        this.approverGroup = approverGroup;
        this.assignedRoles = assignedRoles;
        this.assignedProjects = assignedProjects;
    }

    @Override
    public String toString() {
        return "LoginGroupMapping{" +
                "loginId=" + loginId +
                ", loginGroupName='" + loginGroupName + '\'' +
                ", approverGroup='" + approverGroup + '\'' +
                ", assignedRoles='" + assignedRoles + '\'' +
                ", assignedProjects='" + assignedProjects + '\'' +
                '}';
    }

    public int getLoginId() {
        return loginId;
    }

    public void setLoginId(int loginId) {
        this.loginId = loginId;
    }

    public String getLoginGroupName() {
        return loginGroupName;
    }

    public void setLoginGroupName(String loginGroupName) {
        this.loginGroupName = loginGroupName;
    }

    public String getApproverGroup() {
        return approverGroup;
    }

    public void setApproverGroup(String approverGroup) {
        this.approverGroup = approverGroup;
    }

    public String getAssignedRoles() {
        return assignedRoles;
    }

    public void setAssignedRoles(String assignedRoles) {
        this.assignedRoles = assignedRoles;
    }

    public String getAssignedProjects() {
        return assignedProjects;
    }

    public void setAssignedProjects(String assignedProjects) {
        this.assignedProjects = assignedProjects;
    }
}
