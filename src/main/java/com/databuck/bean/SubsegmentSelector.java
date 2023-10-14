package com.databuck.bean;

public class SubsegmentSelector {
	
	String columnName;
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public Double getPercentOfUniqueValues() {
		return percentOfUniqueValues;
	}
	public void setPercentOfUniqueValues(Double percentOfUniqueValues) {
		this.percentOfUniqueValues = percentOfUniqueValues;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	Double percentOfUniqueValues;
	int position;
}
