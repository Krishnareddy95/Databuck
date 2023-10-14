package com.databuck.bean;

public class DateRuleMap {

	private String NAAcceptable;
	private String minAcceptable;
	private String maxAcceptable;
	private String nullColumn;
	
	
	
	public String getMinAcceptable() {
		return minAcceptable;
	}
	public void setMinAcceptable(String minAcceptable) {
		this.minAcceptable = minAcceptable;
	}
	
	public String getNAAcceptable() {
		return NAAcceptable;
	}
	public void setNAAcceptable(String NAAcceptable) {
		this.NAAcceptable = NAAcceptable;
	}
	
	public String getMaxAcceptable() {
		return maxAcceptable;
	}
	public void setMaxAcceptable(String maxAcceptable) {
		this.maxAcceptable = maxAcceptable;
	}
	public String getNullColumn() {
		return nullColumn;
	}
	public void setNullColumn(String nullColumn) {
		this.nullColumn = nullColumn;
	}
	
}
