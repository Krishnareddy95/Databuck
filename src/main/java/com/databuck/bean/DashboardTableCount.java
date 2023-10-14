package com.databuck.bean;

public class DashboardTableCount {

	Long id;
	Long schemaId;
	Integer tableCount;
	Integer monitoredTableCount;
	Integer issueDetected;
	Integer unvalidatedTableCount;
	Integer rulesExecuted;
	Integer highTrustTableCount;
	Integer lowTrustTableCount;
	Integer hoursSaved;
	String updatedDateTime;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getSchemaId() {
		return schemaId;
	}
	public void setSchemaId(Long schemaId) {
		this.schemaId = schemaId;
	}
	public Integer getTableCount() {
		return !tableCount.equals(null)?tableCount:0;
	}
	public void setTableCount(Integer tableCount) {
		this.tableCount = tableCount;
	}
	public Integer getMonitoredTableCount() {
		return !monitoredTableCount.equals(null)?monitoredTableCount:0;
	}
	public void setMonitoredTableCount(Integer monitoredTableCount) {
		this.monitoredTableCount = monitoredTableCount;
	}
	public String getUpdatedDateTime() {
		return updatedDateTime;
	}
	public void setUpdatedDateTime(String updatedDateTime) {
		this.updatedDateTime = updatedDateTime;
	}
	public Integer getIssueDetected() {
		return !issueDetected.equals(null)?issueDetected:0;
	}
	public void setIssueDetected(Integer issueDetected) {
		this.issueDetected = issueDetected;
	}
	public Integer getUnvalidatedTableCount() {
		return !unvalidatedTableCount.equals(null)?unvalidatedTableCount:0;
	}
	public void setUnvalidatedTableCount(Integer unvalidatedTableCount) {
		this.unvalidatedTableCount = unvalidatedTableCount;
	}
	public Integer getRulesExecuted() {
		return !rulesExecuted.equals(null)?rulesExecuted:0;
	}
	public void setRulesExecuted(Integer rulesExecuted) {
		this.rulesExecuted = rulesExecuted;
	}
	public Integer getHighTrustTableCount() {
		return !highTrustTableCount.equals(null)?highTrustTableCount:0;
	}
	public void setHighTrustTableCount(Integer highTrustTableCount) {
		this.highTrustTableCount = highTrustTableCount;
	}
	public Integer getLowTrustTableCount() {
		return !lowTrustTableCount.equals(null)?lowTrustTableCount:0;
	}
	public void setLowTrustTableCount(Integer lowTrustTableCount) {
		this.lowTrustTableCount = lowTrustTableCount;
	}
	public Integer getHoursSaved() {
		return !hoursSaved.equals(null)?hoursSaved:0;
	}
	public void setHoursSaved(Integer hoursSaved) {
		this.hoursSaved = hoursSaved;
	}
	public DashboardTableCount() {
		// TODO Auto-generated constructor stub
	}
	public DashboardTableCount(Long id, Long schemaId, Integer tableCount, Integer monitoredTableCount,
			Integer issueDetected, Integer unvalidatedTableCount, Integer rulesExecuted, Integer highTrustTableCount,
			Integer lowTrustTableCount, Integer hoursSaved, String updatedDateTime) {
		super();
		this.id = id;
		this.schemaId = schemaId;
		this.tableCount = tableCount;
		this.monitoredTableCount = monitoredTableCount;
		this.issueDetected = issueDetected;
		this.unvalidatedTableCount = unvalidatedTableCount;
		this.rulesExecuted = rulesExecuted;
		this.highTrustTableCount = highTrustTableCount;
		this.lowTrustTableCount = lowTrustTableCount;
		this.hoursSaved = hoursSaved;
		this.updatedDateTime = updatedDateTime;
	}
	
	@Override
	public String toString() {
		return "DashboardTableCount [id=" + id + ", schemaId=" + schemaId + ", tableCount=" + tableCount
				+ ", monitoredTableCount=" + monitoredTableCount + ", issueDetected=" + issueDetected
				+ ", unvalidatedTableCount=" + unvalidatedTableCount + ", rulesExecuted=" + rulesExecuted
				+ ", highTrustTableCount=" + highTrustTableCount + ", lowTrustTableCount=" + lowTrustTableCount
				+ ", hoursSaved=" + hoursSaved + ", updatedDateTime=" + updatedDateTime + "]";
	}

	
}
