package com.databuck.dto;

import io.swagger.annotations.ApiModelProperty;

public class GetAdvancedRulesDto {

	@ApiModelProperty(notes = "Rule Id")
	private long ruleId;
	@ApiModelProperty(notes = "Rule Type")
	private String ruleType;
	@ApiModelProperty(notes = "Column Name")
	private String columnName;
	@ApiModelProperty(notes = "Rule Expression")
	private String ruleExpression;
	@ApiModelProperty(notes = "Rule Sql")
	private String ruleSql;
	@ApiModelProperty(notes = "Is Rule Active")
	private String isRuleActive;
	@ApiModelProperty(notes = "Is Custom Rule Eligible")
	private String isCustomRuleEligible;
	
	
	public long getRuleId() {
		return ruleId;
	}
	public void setRuleId(long ruleId) {
		this.ruleId = ruleId;
	}
	public String getRuleType() {
		return ruleType;
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
	public String getRuleExpression() {
		return ruleExpression;
	}
	public void setRuleExpression(String ruleExpression) {
		this.ruleExpression = ruleExpression;
	}
	public String getRuleSql() {
		return ruleSql;
	}
	public void setRuleSql(String ruleSql) {
		this.ruleSql = ruleSql;
	}
	public String getIsRuleActive() {
		return isRuleActive;
	}
	public void setIsRuleActive(String isRuleActive) {
		this.isRuleActive = isRuleActive;
	}
	public String getIsCustomRuleEligible() {
		return isCustomRuleEligible;
	}
	public void setIsCustomRuleEligible(String isCustomRuleEligible) {
		this.isCustomRuleEligible = isCustomRuleEligible;
	}
	
}
