package com.databuck.bean;

import java.util.Date;

public class ListApplicationsandListDataSources {
	private String laName;
	private Date createdAt;
	private String lsName;
	private Long idData;
	private Long idApp;
	private String appType;
	private String stagingApprovalStatus;
	private String approvalStatus;
	private String approvalDate;
	private String approverName;
	private String createdByUser;
	private String active;
	private Long projectId;
	private String projectName;

	private String profilingEnabled;
	private String advancedRulesEnabled;
	
	private int data_domain;
	
	

	public int getData_domain() {
		return data_domain;
	}

	public void setData_domain(int data_domain) {
		this.data_domain = data_domain;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	public String getCreatedByUser() {
		return createdByUser;
	}
	
	public void setCreatedByUser(String createdByUser) {
		this.createdByUser=createdByUser;
	}
	

	public String getActive() {
		return active;
	}
	
	public void setActive(String active) {
		this.active=active;
	}

	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public String getLsName() {
		return lsName;
	}
	public void setLsName(String lsName) {
		this.lsName = lsName;
	}
	public Long getIdData() {
		return idData;
	}
	public void setIdData(Long idData) {
		this.idData = idData;
	}
	public Long getIdApp() {
		return idApp;
	}
	public void setIdApp(Long idApp) {
		this.idApp = idApp;
	}
	public String getLaName() {
		return laName;
	}
	public void setLaName(String laName) {
		this.laName = laName;
	}
	public String getAppType() {
		return appType;
	}
	public void setAppType(String appType) {
		this.appType = appType;
	}
	public String getApprovalStatus() {
		return approvalStatus;
	}

	public void setApprovalStatus(String approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	public String getProfilingEnabled() {
		return profilingEnabled;
	}

	public void setProfilingEnabled(String profilingEnabled) {
		this.profilingEnabled = profilingEnabled;
	}

	public String getAdvancedRulesEnabled() {
		return advancedRulesEnabled;
	}

	public void setAdvancedRulesEnabled(String advancedRulesEnabled) {
		this.advancedRulesEnabled = advancedRulesEnabled;
	}

	public String getApprovalDate() {
		return approvalDate;
	}

	public void setApprovalDate(String approvalDate) {
		this.approvalDate = approvalDate;
	}

	public String getApproverName() {
		return approverName;
	}

	public void setApproverName(String approverName) {
		this.approverName = approverName;
	}

	public String getStagingApprovalStatus() {
		return stagingApprovalStatus;
	}

	public void setStagingApprovalStatus(String stagingApprovalStatus) {
		this.stagingApprovalStatus = stagingApprovalStatus;
	}

}