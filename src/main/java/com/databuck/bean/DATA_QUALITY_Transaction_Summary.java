package com.databuck.bean;

public class DATA_QUALITY_Transaction_Summary {
	private String date;
	private int  run;
	private int  duplicate;
	
	private String type;
	private int totalCount;
	private Double percentage;
	private Double threshold;
	private String status;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public Double getPercentage() {
		return percentage;
	}
	public void setPercentage(Double percentage) {
		this.percentage = percentage;
	}
	public Double getThreshold() {
		return threshold;
	}
	public void setThreshold(Double threshold) {
		this.threshold = threshold;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public int getRun() {
		return run;
	}
	public void setRun(int run) {
		this.run = run;
	}
	public int getDuplicate() {
		return duplicate;
	}
	public void setDuplicate(int duplicate) {
		this.duplicate = duplicate;
	}
}
