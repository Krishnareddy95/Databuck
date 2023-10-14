package com.databuck.bean;

public class DATA_QUALITY_Transaction_Detail {
	private String Date;
	private long Run;
	private String name;
	private long dupcount;
	public String getDate() {
		return Date;
	}
	public void setDate(String date) {
		Date = date;
	}
	public long getRun() {
		return Run;
	}
	public void setRun(long run) {
		Run = run;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getDupcount() {
		return dupcount;
	}
	public void setDupcount(long dupcount) {
		this.dupcount = dupcount;
	}

}
