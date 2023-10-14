package com.databuck.bean;

public class EssentialCheckSummaryReport {
	private String rule;
	private long checkComponentId;
	private String ruleTechnicalName;
	private String columnName;
	private String dGroupCol;
	private String dGroupVal;
	private long recordCount;
	private long recordFailed;
	private double failPercentage;
	private double threshold;
	private String status;

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public long getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(long recordCount) {
		this.recordCount = recordCount;
	}

	public long getRecordFailed() {
		return recordFailed;
	}

	public void setRecordFailed(long recordFailed) {
		this.recordFailed = recordFailed;
	}

	public double getFailPercentage() {
		return failPercentage;
	}

	public void setFailPercentage(double failPercentage) {
		this.failPercentage = failPercentage;
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getdGroupCol() {
		return dGroupCol;
	}

	public void setdGroupCol(String dGroupCol) {
		this.dGroupCol = dGroupCol;
	}

	public String getdGroupVal() {
		return dGroupVal;
	}

	public void setdGroupVal(String dGroupVal) {
		this.dGroupVal = dGroupVal;
	}

	public String getRuleTechnicalName() {
		return ruleTechnicalName;
	}

	public void setRuleTechnicalName(String ruleTechnicalName) {
		this.ruleTechnicalName = ruleTechnicalName;
	}

	public long getCheckComponentId() {
		return checkComponentId;
	}

	public void setCheckComponentId(long checkComponentId) {
		this.checkComponentId = checkComponentId;
	}

}
