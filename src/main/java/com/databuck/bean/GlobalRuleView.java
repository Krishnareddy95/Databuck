package com.databuck.bean;

import java.util.Date;

public class GlobalRuleView {

	private long idListColrules;
	

	private String name;
	private String domain;
	private String ruleName;
	private String description;
	private String expression;
	private String createdAt;
	private String matchingRules;
	private String externalDatasetName;
	private String ruleType;
	private String createdByUser;
	private Long projectId;
	private String projectName;
	private String dimensionName;
	private String filterCondition;
	private Integer filterId;
	
	private Long domainId;
	private Long dimensionId;
	private Double ruleThreshold;
	private long idRightData;

	private Integer rightTemplateFilterId;
	private String rightTemplateFilterCondition;
	
	public Integer getFilterId() {
		return filterId;
	}

	public void setFilterId(Integer filterId) {
		this.filterId = filterId;
	}

	public String getFilterCondition() {
		return filterCondition;
	}

	public void setFilterCondition(String filterCondition) {
		this.filterCondition = filterCondition;
	}

	public String getDimensionName() {
		return dimensionName;
	}

	public void setDimensionName(String dimensionName) {
		this.dimensionName = dimensionName;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	public String getCreatedByUser() {
		return createdByUser;
	}
	public void setCreatedByUser(String createdByUser) {
		this.createdByUser=createdByUser;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
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
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
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
		return "GlobalRuleView [idListColrules=" + idListColrules + ", name=" + name + ", domain=" + domain
				+ ", ruleName=" + ruleName + ", description=" + description + ", expression=" + expression
				+ ", createdAt=" + createdAt + ", matchingRules=" + matchingRules + ", externalDatasetName="
				+ externalDatasetName + ", ruleType=" + ruleType + ", createdByUser=" + createdByUser + ", projectId="
				+ projectId + ", projectName=" + projectName + ", dimensionName=" + dimensionName + ", filterCondition="
				+ filterCondition + ", filterId=" + filterId + ", domainId=" + domainId + ", dimensionId=" + dimensionId
				+ ", ruleThreshold=" + ruleThreshold + ", idRightData=" + idRightData + ", rightTemplateFilterId="
				+ rightTemplateFilterId + ", rightTemplateFilterCondition=" + rightTemplateFilterCondition + "]";
	}

	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}
	
	public long getIdRightData() {
		return idRightData;
	}

	public void setIdRightData(long idRightData) {
		this.idRightData = idRightData;
	}
	
	public Long getDimensionId() {
		return dimensionId;
	}
	public void setDimensionId(Long dimensionId) {
		this.dimensionId = dimensionId;
	}	
	public Double getRuleThreshold() {
		return ruleThreshold;
	}
	public void setRuleThreshold(Double ruleThreshold) {
		this.ruleThreshold = ruleThreshold;
	}
	

}
