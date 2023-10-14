package com.databuck.csvmodel;

public class DateAnomalyCheck {
	private String idApp;
	private String date;
	private String run;
	private String dateField;
	private String totalNumberOfRecords;
	private String totalFailedRecords;
	private String forgotRunEnabled;

	public String getIdApp() {
		return idApp;
	}

	public void setIdApp(String idApp) {
		this.idApp = idApp;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getRun() {
		return run;
	}

	public void setRun(String run) {
		this.run = run;
	}

	public String getDateField() {
		return dateField;
	}

	public void setDateField(String dateField) {
		this.dateField = dateField;
	}

	public String getTotalNumberOfRecords() {
		return totalNumberOfRecords;
	}

	public void setTotalNumberOfRecords(String totalNumberOfRecords) {
		this.totalNumberOfRecords = totalNumberOfRecords;
	}

	public String getTotalFailedRecords() {
		return totalFailedRecords;
	}

	public void setTotalFailedRecords(String totalFailedRecords) {
		this.totalFailedRecords = totalFailedRecords;
	}

	public String getForgotRunEnabled() {
		return forgotRunEnabled;
	}

	public void setForgotRunEnabled(String forgotRunEnabled) {
		this.forgotRunEnabled = forgotRunEnabled;
	}

}
