package com.databuck.bean;

public class ColumnProfileDetails_DP {

	private String execDate;
	private Long run;
	private String columnName;
	private String columnValue;
	private Long count;
	private Double percentage;

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getColumnValue() {
		return columnValue;
	}

	public void setColumnValue(String columnValue) {
		this.columnValue = columnValue;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public Double getPercentage() {
		return percentage;
	}

	public void setPercentage(Double percentage) {
		this.percentage = percentage;
	}

	public String getExecDate() {
		return execDate;
	}

	public void setExecDate(String execDate) {
		this.execDate = execDate;
	}

	public Long getRun() {
		return run;
	}

	public void setRun(Long run) {
		this.run = run;
	}

	@Override
	public String toString() {
		return "" + columnName + "," + columnValue + "," + count + "," + percentage;
	}

}
