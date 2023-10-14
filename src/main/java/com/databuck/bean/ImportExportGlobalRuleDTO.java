package com.databuck.bean;

public class ImportExportGlobalRuleDTO {

	private String ruleName;
	private String columnName;
	private String ruleExpression;
	private double ruleThreshold;

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getRuleExpression() {
		return ruleExpression;
	}

	public void setRuleExpression(String ruleExpression) {
		this.ruleExpression = ruleExpression;
	}

	public double getRuleThreshold() {
		return ruleThreshold;
	}

	public void setRuleThreshold(double ruleThreshold) {
		this.ruleThreshold = ruleThreshold;
	}

	@Override
	public String toString() {
		return ruleName + "," + columnName + "," + ruleExpression + "," + ruleThreshold;
	}

}
