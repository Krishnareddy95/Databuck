package com.databuck.bean;

public class RuleMappingDetails {

    private int id;
    private int templateid;
    private String ruleId;
    private String ruleName;
    private String ruleExpression;
    private String ruleType;
    private String anchorColumns;
    private String activeFlag;
    private String filterCondition;
    private String matchingRules;
    private String rightTemplateFilterCondition;
    private String nullFilterColumns;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTemplateid() {
        return templateid;
    }

    public void setTemplateid(int templateid) {
        this.templateid = templateid;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
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

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public String getAnchorColumns() {
        return anchorColumns;
    }

    public void setAnchorColumns(String anchorColumns) {
        this.anchorColumns = anchorColumns;
    }

    public String getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(String activeFlag) {
        this.activeFlag = activeFlag;
    }

    public String getFilterCondition() {
        return filterCondition;
    }

    public void setFilterCondition(String filterCondition) {
        this.filterCondition = filterCondition;
    }

    public String getMatchingRules() {
        return matchingRules;
    }

    public void setMatchingRules(String matchingRules) {
        this.matchingRules = matchingRules;
    }

    public String getRightTemplateFilterCondition() {
        return rightTemplateFilterCondition;
    }

    public void setRightTemplateFilterCondition(String rightTemplateFilterCondition) {
        this.rightTemplateFilterCondition = rightTemplateFilterCondition;
    }

    public String getNullFilterColumns() {
        return nullFilterColumns;
    }

    public void setNullFilterColumns(String nullFilterColumns) {
        this.nullFilterColumns = nullFilterColumns;
    }
}
