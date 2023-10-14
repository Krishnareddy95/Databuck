package com.databuck.bean;

public class ColumnCombinationProfile_DP {

	private String execDate;
	private Long run;
	private String column_Group_Name;
	private String column_Group_Value;
	private Long count;
	private double percentage;

	public String getColumn_Group_Name() {
		return column_Group_Name;
	}

	public void setColumn_Group_Name(String column_Group_Name) {
		this.column_Group_Name = column_Group_Name;
	}

	public String getColumn_Group_Value() {
		return column_Group_Value;
	}

	public void setColumn_Group_Value(String column_Group_Value) {
		this.column_Group_Value = column_Group_Value;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double percentage) {
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
		return "\"" + column_Group_Name + "\",\"" + column_Group_Value + "\"," + count + "," + percentage;
	}

}
