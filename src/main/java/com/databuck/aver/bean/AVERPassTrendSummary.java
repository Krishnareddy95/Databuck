package com.databuck.aver.bean;

import java.util.Map;

public class AVERPassTrendSummary {
	
	private String date;
	private Map<String,Double> sourcePercentage;
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public Map<String, Double> getSourcePercentage() {
		return sourcePercentage;
	}
	public void setSourcePercentage(Map<String, Double> sourcePercentage) {
		this.sourcePercentage = sourcePercentage;
	}
}
