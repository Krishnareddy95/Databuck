package com.databuck.bean;

public class DatabuckAlertLog {
	private long alertId;
	private String alertPublishDate;
	private String jobExecutionDate;
	private String taskUniqueId;
	private long jobRunNumber;
	private long projectId;
	private int eventid;
	private long taskId;
	private String taskName;
	private String alertMessage;
	private String alertMessageSubject;
	private String isEventSubscribed;
	private String isEventPublished;
	private String executionErrors;
	private String eventMessageSubject;

	public String getEventMessageSubject() {
		return eventMessageSubject;
	}

	public void setEventMessageSubject(String eventMessageSubject) {
		this.eventMessageSubject = eventMessageSubject;
	}

	public long getAlertId() {
		return alertId;
	}

	public void setAlertId(long alertId) {
		this.alertId = alertId;
	}

	public String getAlertPublishDate() {
		return alertPublishDate;
	}

	public void setAlertPublishDate(String alertPublishDate) {
		this.alertPublishDate = alertPublishDate;
	}

	public String getJobExecutionDate() {
		return jobExecutionDate;
	}

	public void setJobExecutionDate(String jobExecutionDate) {
		this.jobExecutionDate = jobExecutionDate;
	}

	public String getTaskUniqueId() {
		return taskUniqueId;
	}

	public void setTaskUniqueId(String taskUniqueId) {
		this.taskUniqueId = taskUniqueId;
	}

	public long getJobRunNumber() {
		return jobRunNumber;
	}

	public void setJobRunNumber(long jobRunNumber) {
		this.jobRunNumber = jobRunNumber;
	}

	public long getProjectId() {
		return projectId;
	}

	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}

	public int getEventid() {
		return eventid;
	}

	public void setEventid(int eventid) {
		this.eventid = eventid;
	}

	public long getTaskId() {
		return taskId;
	}

	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getAlertMessage() {
		return alertMessage;
	}

	public void setAlertMessage(String alertMessage) {
		this.alertMessage = alertMessage;
	}

	public String getAlertMessageSubject() {
		return alertMessageSubject;
	}

	public void setAlertMessageSubject(String alertMessageSubject) {
		this.alertMessageSubject = alertMessageSubject;
	}

	public String getIsEventSubscribed() {
		return isEventSubscribed;
	}

	public void setIsEventSubscribed(String isEventSubscribed) {
		this.isEventSubscribed = isEventSubscribed;
	}

	public String getIsEventPublished() {
		return isEventPublished;
	}

	public void setIsEventPublished(String isEventPublished) {
		this.isEventPublished = isEventPublished;
	}

	public String getExecutionErrors() {
		return executionErrors;
	}

	public void setExecutionErrors(String executionErrors) {
		this.executionErrors = executionErrors;
	}
}
