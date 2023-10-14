package com.databuck.bean;

import java.util.Date;

public class ProjectJobDTO {

	private Long queueId;
	private Long projectId;
	private String projectName;
	private String status;
	private Date createdAt;
	private String uniqueId;
	private String sparkAppId;
	private String deployMode;
	private Date startTime;
	private Date endTime;
	private Long processId;
	private String triggeredByHost;
	private String fullDuration;

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

	public Long getQueueId() {
		return queueId;
	}

	public void setQueueId(Long queueId) {
		this.queueId = queueId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getSparkAppId() {
		return sparkAppId;
	}

	public void setSparkAppId(String sparkAppId) {
		this.sparkAppId = sparkAppId;
	}

	public String getDeployMode() {
		return deployMode;
	}

	public void setDeployMode(String deployMode) {
		this.deployMode = deployMode;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Long getProcessId() {
		return processId;
	}

	public void setProcessId(Long processId) {
		this.processId = processId;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getTriggeredByHost() {
		return triggeredByHost;
	}

	public void setTriggeredByHost(String triggeredByHost) {
		this.triggeredByHost = triggeredByHost;
	}

	public String getFullDuration() {
		return fullDuration;
	}

	public void setFullDuration(String fullDuration) {
		this.fullDuration = fullDuration;
	}

}
