package com.databuck.aver.bean;

public class AVERDashboardSummary {

	private String summaryName;
	private long totalCount;
	private long passedCount;
	private long failedCount;
	private double passPercentage;
	private double failPercentage;
	public String getSummaryName() {
		return summaryName;
	}
	public void setSummaryName(String summaryName) {
		this.summaryName = summaryName;
	}
	public long getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}
	public long getPassedCount() {
		return passedCount;
	}
	public void setPassedCount(long passedCount) {
		this.passedCount = passedCount;
	}
	public long getFailedCount() {
		return failedCount;
	}
	public void setFailedCount(long failedCount) {
		this.failedCount = failedCount;
	}
	public double getPassPercentage() {
		return passPercentage;
	}
	public void setPassPercentage(double passPercentage) {
		this.passPercentage = passPercentage;
	}
	public double getFailPercentage() {
		return failPercentage;
	}
	public void setFailPercentage(double failPercentage) {
		this.failPercentage = failPercentage;
	}
	
}
