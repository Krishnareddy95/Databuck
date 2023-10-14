package com.databuck.bean;

import java.sql.Timestamp;
import java.util.Date;

public class RunningTaskDTO {

	private String taskType;
	private long applicationId;
	private String applicationName;
	private long processId;
	private String deployMode;
	private String sparkAppId;
	private Timestamp startTime;
	private Date endTime;
	private long duration;
	private String jobDurationStatus;
	private String fullDuration;
	private String status;
	private String uniqueId;
	private Long projectId;
	private String projectName;
	private String triggeredByHost;

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

	public String getTaskType() {
		return taskType;
	}

	public String getSparkAppId() {
		return sparkAppId;
	}

	public void setSparkAppId(String sparkAppId) {
		this.sparkAppId = sparkAppId;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public long getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(long applicationId) {
		this.applicationId = applicationId;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getJobDurationStatus() {
		return jobDurationStatus;
	}

	public void setJobDurationStatus(String jobDurationStatus) {
		this.jobDurationStatus = jobDurationStatus;
	}

	public long getProcessId() {
		return processId;
	}

	public void setProcessId(long processId) {
		this.processId = processId;
	}

	public String getDeployMode() {
		return deployMode;
	}

	public void setDeployMode(String deployMode) {
		this.deployMode = deployMode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFullDuration() {
		return fullDuration;
	}

	public void setFullDuration(String fullDuration) {
		this.fullDuration = fullDuration;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getTriggeredByHost() {
		return triggeredByHost;
	}

	public void setTriggeredByHost(String triggeredByHost) {
		this.triggeredByHost = triggeredByHost;
	}
}
