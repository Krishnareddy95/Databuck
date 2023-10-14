package com.databuck.bean;

import java.util.Date;

public class DataQualitySQSRequest {

	private Long id;
	private Long idApp;
	private String uniqueId;
	private Date executionDate;
	private long run;
	private String sqsAlertEnabled;
	private String sqsAlertSent;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getIdApp() {
		return idApp;
	}

	public void setIdApp(Long idApp) {
		this.idApp = idApp;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public Date getExecutionDate() {
		return executionDate;
	}

	public void setExecutionDate(Date executionDate) {
		this.executionDate = executionDate;
	}

	public long getRun() {
		return run;
	}

	public void setRun(long run) {
		this.run = run;
	}

	public String getSqsAlertEnabled() {
		return sqsAlertEnabled;
	}

	public void setSqsAlertEnabled(String sqsAlertEnabled) {
		this.sqsAlertEnabled = sqsAlertEnabled;
	}

	public String getSqsAlertSent() {
		return sqsAlertSent;
	}

	public void setSqsAlertSent(String sqsAlertSent) {
		this.sqsAlertSent = sqsAlertSent;
	}

}
