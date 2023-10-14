package com.databuck.csvmodel;

public class DefaultPatternCheck {

	private String idApp;
	private String date;
	private String run;
	private String colName;
	private String totalRecords;
	private String totalFailedRecords;
	private String totalMatchedRecords;
	private String patternsList;
	private String newPattern;
	private String failedRecordsPercentage;
	private String patternThreshold;
	private String status;
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

	public String getColName() {
		return colName;
	}

	public void setColName(String colName) {
		this.colName = colName;
	}

	public String getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(String totalRecords) {
		this.totalRecords = totalRecords;
	}

	public String getTotalFailedRecords() {
		return totalFailedRecords;
	}

	public void setTotalFailedRecords(String totalFailedRecords) {
		this.totalFailedRecords = totalFailedRecords;
	}

	public String getTotalMatchedRecords() {
		return totalMatchedRecords;
	}

	public void setTotalMatchedRecords(String totalMatchedRecords) {
		this.totalMatchedRecords = totalMatchedRecords;
	}

	public String getPatternsList() {
		return patternsList;
	}

	public void setPatternsList(String patternsList) {
		this.patternsList = patternsList;
	}

	public String getNewPattern() {
		return newPattern;
	}

	public void setNewPattern(String newPattern) {
		this.newPattern = newPattern;
	}

	public String getFailedRecordsPercentage() {
		return failedRecordsPercentage;
	}

	public void setFailedRecordsPercentage(String failedRecordsPercentage) {
		this.failedRecordsPercentage = failedRecordsPercentage;
	}

	public String getPatternThreshold() {
		if (!status.equalsIgnoreCase("new")) {
			return "";
		}
		return patternThreshold;
	}

	public void setPatternThreshold(String patternThreshold) {
		this.patternThreshold = patternThreshold;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getForgotRunEnabled() {
		return forgotRunEnabled;
	}

	public void setForgotRunEnabled(String forgotRunEnabled) {
		this.forgotRunEnabled = forgotRunEnabled;
	}

}
