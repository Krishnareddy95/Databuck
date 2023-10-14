package com.databuck.aver.bean;

public class TrendCheckFailedReport {

	private String fileName;
	private String overallStatus;
	private int noOfTrendChecks;
	private int noOfChecksFailed;
	private String columnsInvolved;

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

	public int getNoOfTrendChecks() {
		return noOfTrendChecks;
	}

	public void setNoOfTrendChecks(int noOfTrendChecks) {
		this.noOfTrendChecks = noOfTrendChecks;
	}

	public int getNoOfChecksFailed() {
		return noOfChecksFailed;
	}

	public void setNoOfChecksFailed(int noOfChecksFailed) {
		this.noOfChecksFailed = noOfChecksFailed;
	}

	public String getColumnsInvolved() {
		return columnsInvolved;
	}

	public void setColumnsInvolved(String columnsInvolved) {
		this.columnsInvolved = columnsInvolved;
	}

}
