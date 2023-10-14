package com.databuck.bean;

import com.databuck.econstants.DeltaType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RuleCatalog {

	private long rowId;
	private long idApp;
	private String laName;
	private long ruleReference;
	private String ruleCode;
	private String defectCode;
	private String ruleType;
	private String ruleName;
	private String columnName;
	private String ruleCategory;
	private String ruleExpression;
	private String matchingRules;
	private long customOrGlobalRuleId;
	private double threshold;
	private long dimensionId;
	private String dimensionName;
	private String agingCheckEnabled;
	private String reviewComments;
	private String reviewDate;
	private String reviewBy;
	private boolean activeFlag;
	private DeltaType deltaType;
	private String ruleDescription;
	private String ruleTags;
	private String customOrGlobalRuleType;
	private String filterCondition;
	private String rightTemplateFilterCondition;
	private String businessAttributeId;
	private String businessAttributes;
	private String nullFilterColumn;

	public String getBusinessAttributes() {
		return businessAttributes;
	}

	public void setBusinessAttributes(String businessAttributes) {
		this.businessAttributes = businessAttributes;
	}

	public String getFilterCondition() {
		return filterCondition;
	}

	public void setFilterCondition(String filterCondition) {
		this.filterCondition = filterCondition;
	}

	public long getRowId() {
		return rowId;
	}

	public void setRowId(long rowId) {
		this.rowId = rowId;
	}

	public long getIdApp() {
		return idApp;
	}

	public void setIdApp(long idApp) {
		this.idApp = idApp;
	}

	public String getLaName() {
		return laName;
	}

	public void setLaName(String laName) {
		this.laName = laName;
	}

	public long getRuleReference() {
		return ruleReference;
	}

	public void setRuleReference(long ruleReference) {
		this.ruleReference = ruleReference;
	}

	public String getRuleCode() {
		return ruleCode;
	}

	public void setRuleCode(String ruleCode) {
		this.ruleCode = ruleCode;
	}

	public String getDefectCode() {
		return defectCode;
	}

	public void setDefectCode(String defectCode) {
		this.defectCode = defectCode;
	}

	public String getRuleType() {
		return ruleType;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getRuleCategory() {
		return ruleCategory;
	}

	public void setRuleCategory(String ruleCategory) {
		this.ruleCategory = ruleCategory;
	}

	public String getRuleExpression() {
		return ruleExpression;
	}

	public void setRuleExpression(String ruleExpression) {
		this.ruleExpression = ruleExpression;
	}

	public String getMatchingRules() {
		return matchingRules;
	}

	public void setMatchingRules(String matchingRules) {
		this.matchingRules = matchingRules;
	}

	public long getCustomOrGlobalRuleId() {
		return customOrGlobalRuleId;
	}

	public void setCustomOrGlobalRuleId(long customOrGlobalRuleId) {
		this.customOrGlobalRuleId = customOrGlobalRuleId;
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public long getDimensionId() {
		return dimensionId;
	}

	public void setDimensionId(long dimensionId) {
		this.dimensionId = dimensionId;
	}

	public String getDimensionName() {
		return dimensionName;
	}

	public void setDimensionName(String dimensionName) {
		this.dimensionName = dimensionName;
	}

	public String getAgingCheckEnabled() {
		return agingCheckEnabled;
	}

	public void setAgingCheckEnabled(String agingCheckEnabled) {
		this.agingCheckEnabled = agingCheckEnabled;
	}

	public String getReviewComments() {
		return reviewComments;
	}

	public void setReviewComments(String reviewComments) {
		this.reviewComments = reviewComments;
	}

	public String getReviewDate() {
		return reviewDate;
	}

	public void setReviewDate(String reviewDate) {
		this.reviewDate = reviewDate;
	}

	public String getReviewBy() {
		return reviewBy;
	}

	public void setReviewBy(String reviewBy) {
		this.reviewBy = reviewBy;
	}

	public boolean isActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(boolean activeFlag) {
		this.activeFlag = activeFlag;
	}

	public DeltaType getDeltaType() {
		return deltaType;
	}

	public void setDeltaType(DeltaType deltaType) {
		this.deltaType = deltaType;
	}

	public String getRuleDescription() {
		return ruleDescription;
	}

	public void setRuleDescription(String ruleDescription) {
		this.ruleDescription = ruleDescription;
	}

	public String getCustomOrGlobalRuleType() {
		return customOrGlobalRuleType;
	}

	public void setCustomOrGlobalRuleType(String customOrGlobalRuleType) {
		this.customOrGlobalRuleType = customOrGlobalRuleType;
	}
	
	public String getRightTemplateFilterCondition() {
		return rightTemplateFilterCondition;
	}

	public void setRightTemplateFilterCondition(String rightTemplateFilterCondition) {
		this.rightTemplateFilterCondition = rightTemplateFilterCondition;
	}
	
	public String getRuleTags() {
		return ruleTags;
	}

	public void setRuleTags(String ruleTags) {
		this.ruleTags = ruleTags;
	}

	public String getBusinessAttributeId() {
		return businessAttributeId;
	}

	public void setBusinessAttributeId(String businessAttributeId) {
		this.businessAttributeId = businessAttributeId;
	}

	public String getNullFilterColumn() {
		return nullFilterColumn;
	}

	public void setNullFilterColumn(String nullFilterColumn) {
		this.nullFilterColumn = nullFilterColumn;
	}

	@Override
	public String toString() {
		return "{\"rowId\":\"" + rowId + "\", \"idApp\":\"" + idApp + "\", \"laName\":\"" + laName
				+ "\", \"ruleReference\":\"" + ruleReference + "\", \"ruleCode\":\"" + ruleCode
				+ "\", \"defectCode\":\"" + defectCode + "\", \"ruleType\":\"" + ruleType + "\", \"ruleName\":\""
				+ ruleName + "\", \"columnName\":\"" + columnName + "\", \"ruleCategory\":\"" + ruleCategory
				+ "\", \"ruleExpression\":\"" + ruleExpression + "\", \"matchingRules\":\"" + matchingRules
				+ "\", \"customOrGlobalRuleId\":\"" + customOrGlobalRuleId + "\", \"threshold\":\"" + threshold
				+ "\", \"dimensionId\":\"" + dimensionId + "\", \"dimensionName\":\"" + dimensionName
				+ "\", \"agingCheckEnabled\":\"" + agingCheckEnabled + "\", \"reviewComments\":\"" + reviewComments
				+ "\", \"reviewDate\":\"" + reviewDate + "\", \"reviewBy\":\"" + reviewBy + "\", \"activeFlag\":\""
				+ activeFlag + "\", \"deltaType\":\"" + deltaType + "\",\"ruleDescription\":\"" + ruleDescription
				+ "\",\"ruleTags\":\"" + ruleTags + "\",\"customOrGlobalRuleType\":\"" + customOrGlobalRuleType + "\",\"filterCondition\":\""+ filterCondition
				+ "\",\"rightTemplateFilterCondition\":\""+ rightTemplateFilterCondition+ "\"}";
	}

}
