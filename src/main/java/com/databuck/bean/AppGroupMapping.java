package com.databuck.bean;

public class AppGroupMapping {
	private long idAppGroupMapping;
	private long appId;
	private String appName;

	public long getIdAppGroupMapping() {
		return idAppGroupMapping;
	}

	public void setIdAppGroupMapping(long idAppGroupMapping) {
		this.idAppGroupMapping = idAppGroupMapping;
	}

	public long getAppId() {
		return appId;
	}

	public void setAppId(long appId) {
		this.appId = appId;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}
}
