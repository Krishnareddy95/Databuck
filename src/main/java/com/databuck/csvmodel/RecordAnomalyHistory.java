package com.databuck.csvmodel;

public class RecordAnomalyHistory {
	private String id;
	private String idApp;
	private String date;
	private String run;
	private String colName;
	private String colVal;
	private String mean;
	private String stddev;
	private String dGroupVal;
	private String dGroupCol;
	private String raDeviation;
	private String raDqi;
	private String status;
	private String forgotRunEnabled;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public String getColVal() {
		return colVal;
	}

	public void setColVal(String colVal) {
		this.colVal = colVal;
	}

	public String getMean() {
		return mean;
	}

	public void setMean(String mean) {
		this.mean = mean;
	}

	public String getStddev() {
		return stddev;
	}

	public void setStddev(String stddev) {
		this.stddev = stddev;
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

	public String getDeviationRA() {
		return raDeviation;
	}

	public void setRaDeviation(String raDeviation) {
		this.raDeviation = raDeviation;
	}

	public String getDqiRA() {
		return raDqi;
	}

	public void setRaDqi(String raDqi) {
		this.raDqi = raDqi;
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
