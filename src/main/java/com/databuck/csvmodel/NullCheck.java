package com.databuck.csvmodel;

public class NullCheck {

	private String execDate;
	private Integer run;
	private String columnName;
	private String status;
	private Integer totalRecords;
	private Integer nullValue;
	
	private String nullPercentage;
	private String nullThreshold;
	private String historicNullMean;
	private String historicNullStdDev;
	private String historicNullStatus;

	public Integer getNullValue() {
		return nullValue;
	}

	public void setNullValue(Integer nullValue) {
		this.nullValue = nullValue;
	}

	public String getExecDate() {
		return execDate;
	}

	public void setExecDate(String execDate) {
		this.execDate = execDate;
	}

	public Integer getRun() {
		return run;
	}

	public void setRun(Integer run) {
		this.run = run;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(Integer totalRecords) {
		this.totalRecords = totalRecords;
	}

	public String getNullPercentage() {
		return nullPercentage;
	}

	public void setNullPercentage(String nullPercentage) {
		this.nullPercentage = nullPercentage;
	}

	public String getNullThreshold() {
		return nullThreshold;
	}

	public void setNullThreshold(String nullThreshold) {
		this.nullThreshold = nullThreshold;
	}

	public String getHistoricNullMean() {
		return historicNullMean;
	}

	public void setHistoricNullMean(String historicNullMean) {
		this.historicNullMean = historicNullMean;
	}

	public String getHistoricNullStdDev() {
		return historicNullStdDev;
	}

	public void setHistoricNullStdDev(String historicNullStdDev) {
		this.historicNullStdDev = historicNullStdDev;
	}

	public String getHistoricNullStatus() {
		return historicNullStatus;
	}

	public void setHistoricNullStatus(String historicNullStatus) {
		this.historicNullStatus = historicNullStatus;
	}

}
