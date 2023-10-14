package com.databuck.csvmodel;

public class GlobalRule {

	private String idApp;
	private String date;
	private String run;
	private String ruleName;
	private String totalRecords;
	private String totalFailed;
	private String rulePercentage;
	private String ruleThreshold;
	private String status;
	private String forgotRunEnabled;
	private String dimensionName;
	private String dGroupVal;

	public String getDimensionName() {
		return dimensionName;
	}

	public void setDimensionName(String dimensionName) {
		this.dimensionName = dimensionName;
	}

	public String getIdApp() {
		return idApp;
	}

	public void setIdApp(String idApp) {
		this.idApp = idApp;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getRun() {
		return run;
	}

	public void setRun(String run) {
		this.run = run;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(String totalRecords) {
		this.totalRecords = totalRecords;
	}

	public String getTotalFailed() {
		return totalFailed;
	}

	public void setTotalFailed(String totalFailed) {
		this.totalFailed = totalFailed;
	}

	public String getRulePercentage() {
		return rulePercentage;
	}

	public void setRulePercentage(String rulePercentage) {
		this.rulePercentage = rulePercentage;
	}

	public String getRuleThreshold() {
		return ruleThreshold;
	}

	public void setRuleThreshold(String ruleThreshold) {
		this.ruleThreshold = ruleThreshold;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getForgotRunEnabled() {
		return forgotRunEnabled;
	}

	public void setForgotRunEnabled(String forgotRunEnabled) {
		this.forgotRunEnabled = forgotRunEnabled;
	}

	public String getDGroupVal() {
		return dGroupVal;
	}

	public void setDGroupVal(String dGroupVal) {
		this.dGroupVal = dGroupVal;
	}

}
