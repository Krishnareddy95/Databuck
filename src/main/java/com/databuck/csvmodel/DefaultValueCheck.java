package com.databuck.csvmodel;

import java.text.DecimalFormat;

public class DefaultValueCheck {

	private String idApp;
	private String date;
	private String colName;
	private String forgotRunEnabled;
	private String defaultValue;
	private String run;
	private String defaultPercentage;
	private String id;
	private String defaultCount;

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

	public String getColName() {
		return colName;
	}

	public void setColName(String colName) {
		this.colName = colName;
	}

	public String getForgotRunEnabled() {
		return forgotRunEnabled;
	}

	public void setForgotRunEnabled(String forgotRunEnabled) {
		this.forgotRunEnabled = forgotRunEnabled;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getRun() {
		return run;
	}

	public void setRun(String run) {
		this.run = run;
	}

	public String getDefaultPercentage() {
		if(defaultPercentage!=null && !defaultPercentage.isEmpty()) {
			DecimalFormat decimalFormat = new DecimalFormat("#0.00");
			defaultPercentage = decimalFormat.format(Double.valueOf(defaultPercentage));
		}
		return defaultPercentage;
	}

	public void setDefaultPercentage(String defaultPercentage) {
		this.defaultPercentage = defaultPercentage;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDefaultCount() {
		return defaultCount;
	}

	public void setDefaultCount(String defaultCount) {
		this.defaultCount = defaultCount;
	}

}
