package com.databuck.aver.bean;

public class TrendCheckPerformanceSummary {

	private String date;
	private double OverallScore;
	private long recordCount;
	private int noOfTrendChecks;
	private int noOfFailedChecks;

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

	public int getNoOfTrendChecks() {
		return noOfTrendChecks;
	}

	public void setNoOfTrendChecks(int noOfTrendChecks) {
		this.noOfTrendChecks = noOfTrendChecks;
	}

	public int getNoOfFailedChecks() {
		return noOfFailedChecks;
	}

	public void setNoOfFailedChecks(int noOfFailedChecks) {
		this.noOfFailedChecks = noOfFailedChecks;
	}

}
