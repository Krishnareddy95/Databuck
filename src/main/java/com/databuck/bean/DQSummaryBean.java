package com.databuck.bean;

public class DQSummaryBean {

	private String execDate;
	private long run;
	private String columnNameOrRuleName;
	private String typeOfCheck;
	private long totalRecords;
	private long failedRecords;

	public String getExecDate() {
		return execDate;
	}

	public void setExecDate(String execDate) {
		this.execDate = execDate;
	}

	public long getRun() {
		return run;
	}

	public void setRun(long run) {
		this.run = run;
	}

	public String getColumnNameOrRuleName() {
		return columnNameOrRuleName;
	}

	public void setColumnNameOrRuleName(String columnNameOrRuleName) {
		this.columnNameOrRuleName = columnNameOrRuleName;
	}

	public String getTypeOfCheck() {
		return typeOfCheck;
	}

	public void setTypeOfCheck(String typeOfCheck) {
		this.typeOfCheck = typeOfCheck;
	}

	public long getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(long totalRecords) {
		this.totalRecords = totalRecords;
	}

	public long getFailedRecords() {
		return failedRecords;
	}

	public void setFailedRecords(long failedRecords) {
		this.failedRecords = failedRecords;
	}

	@Override
	public String toString() {
		return execDate + "," + run + ",\"" + columnNameOrRuleName + "\"," + typeOfCheck + "," + totalRecords + ","
				+ failedRecords;
	}

}
