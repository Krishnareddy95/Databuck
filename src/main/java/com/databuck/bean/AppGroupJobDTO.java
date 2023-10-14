package com.databuck.bean;

import java.util.Date;

public class AppGroupJobDTO {

	private Long queueId;
	private Long idAppGroup;
	private String appGroupName;
	private String uniqueId;
	private String status;
	private Date createdAt;
	private String sparkAppId;
	private String deployMode;
	private Date startTime;
	private Date endTime;
	private Long processId;
	private String triggeredByHost;
	private String fullDuration;

	public Long getQueueId() {
		return queueId;
	}

	public void setQueueId(Long queueId) {
		this.queueId = queueId;
	}

	public Long getIdAppGroup() {
		return idAppGroup;
	}

	public void setIdAppGroup(Long idAppGroup) {
		this.idAppGroup = idAppGroup;
	}

	public String getAppGroupName() {
		return appGroupName;
	}

	public void setAppGroupName(String appGroupName) {
		this.appGroupName = appGroupName;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
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
