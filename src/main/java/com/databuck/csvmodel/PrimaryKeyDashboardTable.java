package com.databuck.csvmodel;

public class PrimaryKeyDashboardTable {
	String keyMetrics;
	String measurement;
	String statusNRecordCount;
	String percentage;
	Double threshold;

	public String getKeyMetrics() {
		return keyMetrics;
	}

	public void setKeyMetrics(String keyMetrics) {
		this.keyMetrics = keyMetrics;
	}

	public String getMeasurement() {
		return measurement;
	}

	public void setMeasurement(String measurement) {
		this.measurement = measurement;
	}

	public String getStatusNRecordCount() {
		return statusNRecordCount;
	}

	public void setStatusNRecordCount(String statusNRecordCount) {
		this.statusNRecordCount = statusNRecordCount;
	}

	public String getPercentage() {
		return percentage;
	}

	public void setPercentage(String percentage) {
		this.percentage = percentage;
	}

	public Double getThreshold() {
		return threshold;
	}

	public void setThreshold(Double threshold2) {
		this.threshold = threshold2;
	}

}
