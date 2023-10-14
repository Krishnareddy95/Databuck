package com.databuck.bean;

public class ActivateRuleReq {
	private long idData;
	private long ruleId;
	private String ruleSql;
	private String columnName;
	private String ruleType;
	private String createdByUser;

	public long getIdData() {
		return idData;
	}

	public void setIdData(long idData) {
		this.idData = idData;
	}

	public long getRuleId() {
		return ruleId;
	}

	public void setRuleId(long ruleId) {
		this.ruleId = ruleId;
	}

	public String getRuleSql() {
		return ruleSql;
	}

	public void setRuleSql(String ruleSql) {
		this.ruleSql = ruleSql;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getRuleType() {
		return ruleType;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}

	public String getCreatedByUser() {
		return createdByUser;
	}

	public void setCreatedByUser(String createdByUser) {
		this.createdByUser = createdByUser;
	}

}
