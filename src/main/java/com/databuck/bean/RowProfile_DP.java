package com.databuck.bean;

public class RowProfile_DP {

	private String execDate;
	private Long run;
	private Long number_of_Columns_with_NULL;
	private Long number_of_Records;
	private Double percentageMissing;

	public Long getNumber_of_Columns_with_NULL() {
		return number_of_Columns_with_NULL;
	}

	public void setNumber_of_Columns_with_NULL(Long number_of_Columns_with_NULL) {
		this.number_of_Columns_with_NULL = number_of_Columns_with_NULL;
	}

	public Long getNumber_of_Records() {
		return number_of_Records;
	}

	public void setNumber_of_Records(Long number_of_Records) {
		this.number_of_Records = number_of_Records;
	}

	public Double getPercentageMissing() {
		return percentageMissing;
	}

	public void setPercentageMissing(Double percentageMissing) {
		this.percentageMissing = percentageMissing;
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
		return "" + number_of_Columns_with_NULL + "," + number_of_Records + "," + percentageMissing;
	}

}
