package com.databuck.aver.bean;

public class AVERTableSummary {
	private String sourceType;
	private long totalFilesCount;
	private long processedFilesCount;
	private long passedFilesCount;
	private long failedFilesCount;

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public long getTotalFilesCount() {
		return totalFilesCount;
	}

	public void setTotalFilesCount(long totalFilesCount) {
		this.totalFilesCount = totalFilesCount;
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
}
