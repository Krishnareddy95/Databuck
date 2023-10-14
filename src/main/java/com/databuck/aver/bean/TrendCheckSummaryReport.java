package com.databuck.aver.bean;

public class TrendCheckSummaryReport {
	private String microsegment;
	private String microsegmentValue;
	private String columnName;
	private long count;
	private double minValue;
	private double maxValue;
	private double std_dev;
	private double meanValue;
	private String status;

	public String getMicrosegment() {
		return microsegment;
	}

	public void setMicrosegment(String microsegment) {
		this.microsegment = microsegment;
	}

	public String getMicrosegmentValue() {
		return microsegmentValue;
	}

	public void setMicrosegmentValue(String microsegmentValue) {
		this.microsegmentValue = microsegmentValue;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public double getMinValue() {
		return minValue;
	}

	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}

	public double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}

	public double getStd_dev() {
		return std_dev;
	}

	public void setStd_dev(double std_dev) {
		this.std_dev = std_dev;
	}

	public double getMeanValue() {
		return meanValue;
	}

	public void setMeanValue(double meanValue) {
		this.meanValue = meanValue;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
