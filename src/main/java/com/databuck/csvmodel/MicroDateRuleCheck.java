package com.databuck.csvmodel;

public class MicroDateRuleCheck {

	private String idApp;
	private String date;
	private String run;
	private String dateFieldCols;
	private String dateFieldValues;
	private String dGroupVal;
	private String dGroupCol;
	private String failureReason;
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

	public String getDateFieldCols() {
		return dateFieldCols;
	}

	public void setDateFieldCols(String dateFieldCols) {
		this.dateFieldCols = dateFieldCols;
	}

	public String getDateFieldValues() {
		return dateFieldValues;
	}

	public void setDateFieldValues(String dateFieldValues) {
		this.dateFieldValues = dateFieldValues;
	}

	public String getMicrosegmentValue() {
		return dGroupVal;
	}

	public void setdGroupVal(String dGroupVal) {
		this.dGroupVal = dGroupVal;
	}

	public String getMicrosegment() {
		return dGroupCol;
	}

	public void setdGroupCol(String dGroupCol) {
		this.dGroupCol = dGroupCol;
	}

	public String getFailureReason() {
		return failureReason;
	}

	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}

	public String getForgotRunEnabled() {
		return forgotRunEnabled;
	}

	public void setForgotRunEnabled(String forgotRunEnabled) {
		this.forgotRunEnabled = forgotRunEnabled;
	}

}
