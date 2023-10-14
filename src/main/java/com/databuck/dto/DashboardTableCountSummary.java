package com.databuck.dto;

public class DashboardTableCountSummary {

	private Integer totalTablesCount;
	private Integer monitoredTablesCount;
	private Integer highTrustTablesCount;
	private Integer lowTrustTablesCount;
	private Integer unValidatedTablesCount;
	private Integer rulesExecuted;
	private Integer issuesDetected;
	private String effortsSavedHrs;
	private String latestDate;
	
	
	public Integer getTotalTablesCount() {
		return !totalTablesCount.equals(null)?totalTablesCount:0;
	}
	public void setTotalTablesCount(Integer totalTablesCount) {
		this.totalTablesCount = totalTablesCount;
	}
	public Integer getMonitoredTablesCount() {
		return !monitoredTablesCount.equals(null)?monitoredTablesCount:0;
	}
	public void setMonitoredTablesCount(Integer monitoredTablesCount) {
		this.monitoredTablesCount = monitoredTablesCount;
	}
	public Integer getHighTrustTablesCount() {
		return !highTrustTablesCount.equals(null)?highTrustTablesCount:0;
	}
	public void setHighTrustTablesCount(Integer highTrustTablesCount) {
		this.highTrustTablesCount = highTrustTablesCount;
	}
	public Integer getLowTrustTablesCount() {
		return !lowTrustTablesCount.equals(null)?lowTrustTablesCount:0;
	}
	public void setLowTrustTablesCount(Integer lowTrustTablesCount) {
		this.lowTrustTablesCount = lowTrustTablesCount;
	}
	public Integer getUnValidatedTablesCount() {
		return !unValidatedTablesCount.equals(null)?unValidatedTablesCount:0;
	}
	public void setUnValidatedTablesCount(Integer unValidatedTablesCount) {
		this.unValidatedTablesCount = unValidatedTablesCount;
	}
	public Integer getRulesExecuted() {
		return !rulesExecuted.equals(null)?rulesExecuted:0;
	}
	public void setRulesExecuted(Integer rulesExecuted) {
		this.rulesExecuted = rulesExecuted;
	}
	public Integer getIssuesDetected() {
		return !issuesDetected.equals(null)?issuesDetected:0;
	}
	public void setIssuesDetected(Integer issuesDetected) {
		this.issuesDetected = issuesDetected;
	}
	public String getEffortsSavedHrs() {
		return !effortsSavedHrs.equals(null)?effortsSavedHrs:"0";
	}
	public void setEffortsSavedHrs(String effortsSavedHrs) {
		this.effortsSavedHrs = effortsSavedHrs;
	}
	public String getLatestDate() {
		return latestDate;
	}
	public void setLatestDate(String latestDate) {
		this.latestDate = latestDate;
	}
	
	
}
