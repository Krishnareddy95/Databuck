package com.databuck.bean;

import java.util.Date;

public class ListColGlobalRules {
	private long idListColrules;
	private long idCol;
	private int domain_id;
	private String name;
	private String domain;
	private String ruleName;
	private String description;
	private String expression;
	private Date createdAt;
	private String templateName;
	private long idRightData;
	private String matchingRules;
	private String externalDatasetName;
	private String ruleType;
	private String createdByUser;
	private Double ruleThreshold;
	private Long projectId;
	private Long dimensionId;
	private String dimensionName;
	private Integer filterId;
	private String filterCondition;
	private Integer rightTemplateFilterId;
	private String rightTemplateFilterCondition;
	private String aggregateResultsEnabled;

	public String getFilterCondition() {
		return filterCondition;
	}

	public void setFilterCondition(String filterCondition) {
		this.filterCondition = filterCondition;
	}

	public Integer getFilterId() {
		return filterId;
	}

	public void setFilterId(Integer filterId) {
		this.filterId = filterId;
	}

	public String getDimensionName() {
		return dimensionName;
	}
	public void setDimensionName(String dimensionName) {
		this.dimensionName = dimensionName;
	}
	public Long getDimensionId() {
		return dimensionId;
	}
	public void setDimensionId(Long dimensionId) {
		this.dimensionId = dimensionId;
	}
	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	public Double getRuleThreshold() {
		return ruleThreshold;
	}
	public void setRuleThreshold(Double ruleThreshold) {
		this.ruleThreshold = ruleThreshold;
	}
	public String getCreatedByUser() {
		return createdByUser;
	}
	public void setCreatedByUser(String createdByUser) {
		this.createdByUser=createdByUser;
	}
	
	public long getIdRightData() {
		return idRightData;
	}

	public void setIdRightData(long idRightData) {
		this.idRightData = idRightData;
	}

	public String getMatchingRules() {
		return matchingRules;
	}

	public void setMatchingRules(String matchingRules) {
		this.matchingRules = matchingRules;
	}

	public String getExternalDatasetName() {
		return externalDatasetName;
	}

	public void setExternalDatasetName(String externalDatasetName) {
		this.externalDatasetName = externalDatasetName;
	}

	public String getRuleType() {
		return ruleType;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}

	

	public long getIdListColrules() {
		return idListColrules;
	}

	public void setIdListColrules(long idListColrules) {
		this.idListColrules = idListColrules;
	}

	public long getIdCol() {
		return idCol;
	}

	public void setIdCol(long idCol) {
		this.idCol = idCol;
	}

	
	public int getDomain_id() {
		return domain_id;
	}

	public void setDomain_id(int domain_id) {
		this.domain_id = domain_id;
	}
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

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getAggregateResultsEnabled() {
		return aggregateResultsEnabled;
	}

	public void setAggregateResultsEnabled(String aggregateResultsEnabled) {
		this.aggregateResultsEnabled = aggregateResultsEnabled;
	}

	public Integer getRightTemplateFilterId() {
		return rightTemplateFilterId;
	}

	public void setRightTemplateFilterId(Integer rightTemplateFilterId) {
		this.rightTemplateFilterId = rightTemplateFilterId;
	}

	public String getRightTemplateFilterCondition() {
		return rightTemplateFilterCondition;
	}

	public void setRightTemplateFilterCondition(String rightTemplateFilterCondition) {
		this.rightTemplateFilterCondition = rightTemplateFilterCondition;
	}

	@Override
	public String toString() {
		return "ListColGlobalRules [idListColrules=" + idListColrules + ", idCol=" + idCol + ", domain_id=" + domain_id
				+ ", name=" + name + ", domain=" + domain + ", ruleName=" + ruleName + ", description=" + description
				+ ", expression=" + expression + ", createdAt=" + createdAt + ", templateName=" + templateName
				+ ", idRightData=" + idRightData + ", matchingRules=" + matchingRules + ", externalDatasetName="
				+ externalDatasetName + ", ruleType=" + ruleType + ", createdByUser=" + createdByUser
				+ ", ruleThreshold=" + ruleThreshold + ", projectId=" + projectId + ", dimensionId=" + dimensionId
				+ ", dimensionName=" + dimensionName + ", filterId=" + filterId + ", filterCondition=" + filterCondition
				+ ", rightTemplateFilterId=" + rightTemplateFilterId + ", rightTemplateFilterCondition="
				+ rightTemplateFilterCondition + ", aggregateResultsEnabled=" + aggregateResultsEnabled + "]";
	}

}
