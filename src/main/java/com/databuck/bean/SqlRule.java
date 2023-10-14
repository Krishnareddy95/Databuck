package com.databuck.bean;

import java.util.Date;

public class SqlRule {
	private int id;
	private String date;
	private String idApp;
	private int run;
	private String ruleName;
	private String totalFailedRecords;
	private String status;
	private String topFailedData;
	private String ruleThreshold;
	private String totalRecords;

	public String getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(String totalRecords) {
		this.totalRecords = totalRecords;
	}

	public String getRuleThreshold() {
		return ruleThreshold;
	}

	public void setRuleThreshold(String ruleThreshold) {
		this.ruleThreshold = ruleThreshold;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getIdApp() {
		return idApp;
	}

	public void setIdApp(String idApp) {
		this.idApp = idApp;
	}

	public int getRun() {
		return run;
	}

	public void setRun(int run) {
		this.run = run;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getTotalFailedRecords() {
		return totalFailedRecords;
	}

	public void setTotalFailedRecords(String totalFailedRecords) {
		this.totalFailedRecords = totalFailedRecords;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTopFailedData() {
		return topFailedData;
	}

	public void setTopFailedData(String topFailedData) {
		this.topFailedData = topFailedData;
	}
}
