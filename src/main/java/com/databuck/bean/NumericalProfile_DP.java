package com.databuck.bean;

public class NumericalProfile_DP {

	private String execDate;
	private Long run;
	private String columnName;
	private String columnName1;
	private Double correlation;

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getColumnName1() {
		return columnName1;
	}

	public void setColumnName1(String columnName1) {
		this.columnName1 = columnName1;
	}

	public Double getCorrelation() {
		return correlation;
	}

	public void setCorrelation(Double correlation) {
		this.correlation = correlation;
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
		return "" + columnName + "," + columnName1 + "," + correlation;
	}

}
