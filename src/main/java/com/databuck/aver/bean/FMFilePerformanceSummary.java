package com.databuck.aver.bean;

public class FMFilePerformanceSummary {
	private String date;
	private double overallScore;
	private String overallStatus;
	private String zeroSizeFileCheck;
	private String recordLengthCheck;
	private String recordMaxLengthCheck;
	private String columnCountCheck;
	private String columnSequenceCheck;

	
	public String getRecordMaxLengthCheck() {
		return recordMaxLengthCheck;
	}

	public void setRecordMaxLengthCheck(String recordMaxLengthCheck) {
		this.recordMaxLengthCheck = recordMaxLengthCheck;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public double getOverallScore() {
		return overallScore;
	}

	public void setOverallScore(double overallScore) {
		this.overallScore = overallScore;
	}

	public String getOverallStatus() {
		return overallStatus;
	}

	public void setOverallStatus(String overallStatus) {
		this.overallStatus = overallStatus;
	}

	public String getZeroSizeFileCheck() {
		return zeroSizeFileCheck;
	}

	public void setZeroSizeFileCheck(String zeroSizeFileCheck) {
		this.zeroSizeFileCheck = zeroSizeFileCheck;
	}

	public String getRecordLengthCheck() {
		return recordLengthCheck;
	}

	public void setRecordLengthCheck(String recordLengthCheck) {
		this.recordLengthCheck = recordLengthCheck;
	}

	public String getColumnCountCheck() {
		return columnCountCheck;
	}

	public void setColumnCountCheck(String columnCountCheck) {
		this.columnCountCheck = columnCountCheck;
	}

	public String getColumnSequenceCheck() {
		return columnSequenceCheck;
	}

	public void setColumnSequenceCheck(String columnSequenceCheck) {
		this.columnSequenceCheck = columnSequenceCheck;
	}

}
