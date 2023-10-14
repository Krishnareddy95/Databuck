package com.databuck.aver.bean;

public class FileCheckReport {

	private String folderPath;
	private String fileName;
	private String overallStatus;
	private String zeroSizeFileCheck;
	private String recordLengthCheck;
	private String recordMaxLengthCheck;
	private String columnCountCheck;
	private String columnSequenceCheck;
	private long recordCount;
	private long threshold;

	
	public String getRecordMaxLengthCheck() {
		return recordMaxLengthCheck;
	}

	public void setRecordMaxLengthCheck(String recordMaxLengthCheck) {
		this.recordMaxLengthCheck = recordMaxLengthCheck;
	}

	public String getFolderPath() {
		return folderPath;
	}

	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
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

	public long getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(long recordCount) {
		this.recordCount = recordCount;
	}

	public long getThreshold() {
		return threshold;
	}

	public void setThreshold(long threshold) {
		this.threshold = threshold;
	}

}
