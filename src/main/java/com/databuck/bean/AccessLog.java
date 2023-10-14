package com.databuck.bean;

import java.io.Serializable;
import java.util.Date;

public class AccessLog implements Serializable{
	
	private String userName;
	private String activity;
	private String activityLogTime;
	private String applicationUrl;

	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getActivity() {
		return activity;
	}
	public void setActivity(String activity) {
		this.activity = activity;
	}
	public String getActivityLogTime() {
		return activityLogTime;
	}
	public void setActivityLogTime(String activityLogTime) {
		this.activityLogTime = activityLogTime;
	}
	public String getApplicationUrl() {
		return applicationUrl;
	}
	public void setApplicationUrl(String applicationUrl) {
		this.applicationUrl = applicationUrl;
	}
	
	public AccessLog(String userName, String activity, String activityLogTime, String applicationUrl) {
		super();
		this.userName = userName;
		this.activity = activity;
		this.activityLogTime = activityLogTime;
		this.applicationUrl = applicationUrl;
	}

	@Override
	public String toString() {
		return "AccessLog [userName=" + userName + ", activity=" + activity + ", activityLogTime=" + activityLogTime
				+ ", applicationUrl=" + applicationUrl + "]";
	}
	
	
}
