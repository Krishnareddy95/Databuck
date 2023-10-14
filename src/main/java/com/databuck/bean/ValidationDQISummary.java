package com.databuck.bean;

public class ValidationDQISummary {
	long idApp;
	String validationName;
	double aggregateDTS;
	String tableName;
	
	
	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public long getIdApp() {
		return idApp;
	}
	public void setIdApp(long idApp) {
		this.idApp = idApp;
	}
	public String getValidationName() {
		return validationName;
	}
	public void setValidationName(String ValidationName) {
		validationName = ValidationName;
	}
	public double getAggregateDTS() {
		return aggregateDTS;
	}
	public void setAggregateDTS(double AggregateDTS) {
		aggregateDTS = AggregateDTS;
	}
	
	
	

}
