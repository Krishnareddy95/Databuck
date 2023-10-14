package com.databuck.bean;

public class TemplateRuleMapping {
	private long templateId;
	private int ruleId;
	private String ruleName;
	private String ruleExpression;
	public String ruleType;
	public String getRuleType() {
		return ruleType;
	}
	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}
	public long getTemplateId() {
		return templateId;
	}
	public void setTemplateId(long templateId) {
		this.templateId = templateId;
	}
	public int getRuleId() {
		return ruleId;
	}
	public void setRuleId(int ruleId) {
		this.ruleId = ruleId;
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
	@Override
	public String toString() {
		return "TemplateRuleMapping [templateId=" + templateId + ", ruleId=" + ruleId + ", ruleName=" + ruleName
				+ ", ruleExpression=" + ruleExpression + ", ruleType=" + ruleType + "]";
	}
	
	
	
	
	

}
