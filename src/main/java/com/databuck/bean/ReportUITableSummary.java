package com.databuck.bean;

public class ReportUITableSummary {
	private String datasource;
	private long processedCount;
	private long passedCount;
	private long failedCount;

	public String getDatasource() {
		return datasource;
	}

	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}

	public long getProcessedCount() {
		return processedCount;
	}

	public void setProcessedCount(long processedCount) {
		this.processedCount = processedCount;
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

}
