package com.databuck.aver.bean;

public class ABCCheckFailedReport {
	private String fileName;
	private String overallStatus;
	private String nullCheckStatus;
	private String lengthCheckStatus;
	private String maxLengthCheckStatus;
	public String getMaxLengthCheckStatus() {
		return maxLengthCheckStatus;
	}

	public void setMaxLengthCheckStatus(String maxLengthCheckStatus) {
		this.maxLengthCheckStatus = maxLengthCheckStatus;
	}

	private String domainCheckStatus;
	private String recordReasonabilityStatus;
	
	

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

	public String getNullCheckStatus() {
		return nullCheckStatus;
	}

	public void setNullCheckStatus(String nullCheckStatus) {
		this.nullCheckStatus = nullCheckStatus;
	}

	public String getLengthCheckStatus() {
		return lengthCheckStatus;
	}

	public void setLengthCheckStatus(String lengthCheckStatus) {
		this.lengthCheckStatus = lengthCheckStatus;
	}

	public String getDomainCheckStatus() {
		return domainCheckStatus;
	}

	public void setDomainCheckStatus(String domainCheckStatus) {
		this.domainCheckStatus = domainCheckStatus;
	}

	public String getRecordReasonabilityStatus() {
		return recordReasonabilityStatus;
	}

	public void setRecordReasonabilityStatus(String recordReasonabilityStatus) {
		this.recordReasonabilityStatus = recordReasonabilityStatus;
	}

}
