package com.databuck.bean;

public class GlobalRuleBean {

	private Long idListColRules;
	private String ruleName;
	private String description; 
	private String ruleCategory;
	private String ruleExpression;
	private String matchingRules;
	private String externalDatasetName;
	private Integer domainId;
	private Long rightIdData;
	private Double ruleThreshold;
	private long dimensionId;
	private Integer filterId;
	private Integer rightTemplateFilterId;
	private String aggregateResultsEnabled;
	private Long idUser;
	private String createdBy;
	private Long projectId;

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public Double getRuleThreshold() {
		return ruleThreshold;
	}

	public void setRuleThreshold(Double ruleThreshold) {
		this.ruleThreshold = ruleThreshold;
	}

	public long getDimensionId() {
		return dimensionId;
	}

	public void setDimensionId(long dimensionId) {
		this.dimensionId = dimensionId;
	}

	public Integer getFilterId() {
		return filterId;
	}

	public void setFilterId(Integer filterId) {
		this.filterId = filterId;
	}

	public String getMatchingRules() {
		return matchingRules;
	}

	public void setMatchingRules(String matchingRules) {
		this.matchingRules = matchingRules;
	}

	public String getAggregateResultsEnabled() {
		return aggregateResultsEnabled;
	}

	public void setAggregateResultsEnabled(String aggregateResultsEnabled) {
		this.aggregateResultsEnabled = aggregateResultsEnabled;
	}

	public Long getIdUser() {
		return idUser;
	}

	public void setIdUser(Long idUser) {
		this.idUser = idUser;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public String getExternalDatasetName() {
		return externalDatasetName;
	}

	public void setExternalDatasetName(String externalDatasetName) {
		this.externalDatasetName = externalDatasetName;
	}

	public Long getRightIdData() {
		return rightIdData;
	}

	public void setRightIdData(Long rightIdData) {
		this.rightIdData = rightIdData;
	}
	
	public Integer getDomainId() {
		return domainId;
	}

	public void setDomainId(Integer domainId) {
		this.domainId = domainId;
	}
	

	public Long getIdListColRules() {
		return idListColRules;
	}

	public void setIdListColRules(Long idListGlobalColRules) {
		this.idListColRules = idListGlobalColRules;
	}

	public Integer getRightTemplateFilterId() {
		return rightTemplateFilterId;
	}

	public void setRightTemplateFilterId(Integer rightTemplateFilterId) {
		this.rightTemplateFilterId = rightTemplateFilterId;
	}

	@Override
	public String toString() {
		return "GlobalRule [idListColRules=" + idListColRules + ", ruleName=" + ruleName + ", ruleDescription="
				+ description + ", ruleCategory=" + ruleCategory + ", ruleExpression=" + ruleExpression
				+ ", matchingRules=" + matchingRules + ", externalDatasetName=" + externalDatasetName + ", domainId="
				+ domainId + ", rightIdData=" + rightIdData + ", ruleThreshold=" + ruleThreshold + ", dimension="
				+ dimensionId + ", filterId=" + filterId + ", rightTemplateFilterId=" + rightTemplateFilterId
				+ ", aggregateResultsEnabled=" + aggregateResultsEnabled + ", idUser=" + idUser + ", createdBy="
				+ createdBy + ", projectId=" + projectId + "]";
	}

}
