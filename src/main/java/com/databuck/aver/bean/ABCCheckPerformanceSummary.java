package com.databuck.aver.bean;

public class ABCCheckPerformanceSummary {
	private String date;
	private double OverallScore;
	private long recordCount;
	private String nullCheckStatus;
	private String lengthCheckStatus;
	private String maxLengthCheckStatus;
	private String domainCheckStatus;
	private String recordReasonabilityStatus;

	
	public String getMaxLengthCheckStatus() {
		return maxLengthCheckStatus;
	}

	public void setMaxLengthCheckStatus(String maxLengthCheckStatus) {
		this.maxLengthCheckStatus = maxLengthCheckStatus;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public double getOverallScore() {
		return OverallScore;
	}

	public void setOverallScore(double overallScore) {
		OverallScore = overallScore;
	}

	public long getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(long recordCount) {
		this.recordCount = recordCount;
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
