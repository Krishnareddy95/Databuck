package com.databuck.bean;

import java.util.List;

public class ReportUIProjectCoverage {

	private long domainId;
	private long projectId;
	private String projectName;
	private List<ReportUISchemaCoverage> connectionList;

	public long getDomainId() {
		return domainId;
	}

	public void setDomainId(long domainId) {
		this.domainId = domainId;
	}

	public long getProjectId() {
		return projectId;
	}

	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public List<ReportUISchemaCoverage> getConnectionList() {
		return connectionList;
	}

	public void setConnectionList(List<ReportUISchemaCoverage> connectionList) {
		this.connectionList = connectionList;
	}

}
