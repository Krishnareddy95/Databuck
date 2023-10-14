package com.databuck.dto;

public class DateVsDTSGraph {
	
	String date;
	String dts;
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getDts() {
		return dts;
	}
	public void setDts(String dts) {
		this.dts = dts;
	}
	
	@Override
	public String toString() {
		return "DateVsDTSGraph [date=" + date + ", dts=" + dts + "]";
	}
	
	
}
