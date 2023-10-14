package com.databuck.bean;

public class ReportUIDashboardSummary {

	private long connectionId;
	private String connectionName;
	private String displayName;
	private int displayOrder;
	private long totalCount;
	private long passedCount;
	private long failedCount;
	private double passPercentage;
	private double failPercentage;

	public long getConnectionId() {
		return connectionId;
	}

	public void setConnectionId(long connectionId) {
		this.connectionId = connectionId;
	}

	public String getConnectionName() {
		return connectionName;
	}

	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
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

	public int getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}

}
