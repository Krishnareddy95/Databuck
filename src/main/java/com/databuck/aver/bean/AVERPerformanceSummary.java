package com.databuck.aver.bean;

public class AVERPerformanceSummary {
	private String date;
	private long processedFilesCount;
	private long passedFilesCount;
	private long failedFilesCount;
	private double failPercentage;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public long getProcessedFilesCount() {
		return processedFilesCount;
	}

	public void setProcessedFilesCount(long processedFilesCount) {
		this.processedFilesCount = processedFilesCount;
	}

	public long getPassedFilesCount() {
		return passedFilesCount;
	}

	public void setPassedFilesCount(long passedFilesCount) {
		this.passedFilesCount = passedFilesCount;
	}

	public long getFailedFilesCount() {
		return failedFilesCount;
	}

	public void setFailedFilesCount(long failedFilesCount) {
		this.failedFilesCount = failedFilesCount;
	}

	public double getFailPercentage() {
		return failPercentage;
	}

	public void setFailPercentage(double failPercentage) {
		this.failPercentage = failPercentage;
	}
}
