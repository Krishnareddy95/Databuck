package com.databuck.bean;

public class ListAdvancedRules {

	private String execDate;
	private long run;
	private long ruleId;
	private long idData;
	private String ruleType;
	private String columnName;
	private String ruleExpr;
	private String ruleSql;
	private String isCustomRuleEligible;
	private String isRuleActive;
	private long idListColrules;

	public long getRuleId() {
		return ruleId;
	}

	public void setRuleId(long ruleId) {
		this.ruleId = ruleId;
	}

	public long getIdData() {
		return idData;
	}

	public void setIdData(long idData) {
		this.idData = idData;
	}

	public String getRuleType() {
		return this.ruleType;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getRuleExpr() {
		return ruleExpr;
	}

	public void setRuleExpr(String ruleExpr) {
		this.ruleExpr = ruleExpr;
	}

	public String getRuleSql() {
		return ruleSql;
	}

	public void setRuleSql(String ruleSql) {
		this.ruleSql = ruleSql;
	}

	public String getIsCustomRuleEligible() {
		return isCustomRuleEligible;
	}

	public void setIsCustomRuleEligible(String isCustomRuleEligible) {
		this.isCustomRuleEligible = isCustomRuleEligible;
	}

	public String getIsRuleActive() {
		return isRuleActive;
	}

	public void setIsRuleActive(String isRuleActive) {
		this.isRuleActive = isRuleActive;
	}

	public long getIdListColrules() {
		return idListColrules;
	}

	public void setIdListColrules(long idListColrules) {
		this.idListColrules = idListColrules;
	}

	public String getExecDate() {
		return execDate;
	}

	public void setExecDate(String execDate) {
		this.execDate = execDate;
	}

	public long getRun() {
		return run;
	}

	public void setRun(long run) {
		this.run = run;
	}

	@Override
	public String toString() {
		return ruleId + "," + ruleType + "," + columnName + "," + ruleExpr + "," + ruleSql;
	}
}
