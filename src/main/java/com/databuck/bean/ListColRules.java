package com.databuck.bean;

import java.util.Date;

public class ListColRules {
	private long idListColrules;
	private long idData;
	private long idCol;
	private String name;
	private String ruleName;
	private String description;
	private String ruleType;
	private String expression;
	private String external;
	private String externalDatasetName;
	private long idRightData;
	private String matchingRules;
	private Date createdAt;
	private String matchType;
	private String sourcetemplateone;
	private String sourcetemplatesecond;
	private String templateName;
	private String createdByUser;
	private double ruleThreshold;
	private Long project_id;
	private Integer domainId;
	private Long idDimension;
	private String dimensionName;
	private String projectName;
	private String anchorColumns;
	
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public Integer getDomainId() {
		return domainId;
	}
	public void setDomainId(Integer domainId) {
		this.domainId = domainId;
	}
	public Long getProjectId() {
		return project_id;
	}
	public void setProjectId(Long projectId) {
		this.project_id = projectId;
	}
	public String getCreatedByUser() {
		return createdByUser;
	}
	public void setCreatedByUser(String createdByUser) {
		this.createdByUser=createdByUser;
	}

	public long getIdListColrules() {
		return idListColrules;
	}
	public void setIdListColrules(long idListColrules) {
		this.idListColrules = idListColrules;
	}
	public long getIdData() {
		return idData;
	}
	public void setIdData(long idData) {
		this.idData = idData;
	}
	public long getIdCol() {
		return idCol;
	}
	public void setIdCol(long idCol) {
		this.idCol = idCol;
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
	public String getRuleType() {
		return ruleType;
	}
	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	public String getExternal() {
		return external;
	}
	public void setExternal(String external) {
		this.external = external;
	}
	public String getExternalDatasetName() {
		return externalDatasetName;
	}
	public void setExternalDatasetName(String externalDatasetName) {
		this.externalDatasetName = externalDatasetName;
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
	public String getMatchType() {
		return matchType;
	}
	public void setMatchType(String matchType) {
		this.matchType = matchType;
	}
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public double getRuleThreshold() {
		return ruleThreshold;
	}
	public void setRuleThreshold(double ruleThreshold) {
		this.ruleThreshold = ruleThreshold;
	}
	public String getSourcetemplatesecond() {
		return sourcetemplatesecond;
	}
	public void setSourcetemplatesecond(String sourcetemplatesecond) {
		this.sourcetemplatesecond = sourcetemplatesecond;
	}
	public String getSourcetemplateone() {
		return sourcetemplateone;
	}
	public void setSourcetemplateone(String sourcetemplateone) {
		this.sourcetemplateone = sourcetemplateone;
	}
	public String getDimensionName() {
		return dimensionName;
	}
	public void setDimensionName(String dimensionName) {
		this.dimensionName = dimensionName;
	}
	public Long getIdDimension() {
		return idDimension;
	}
	public void setIdDimension(Long idDimension) {
		this.idDimension = idDimension;
	}
	public String getAnchorColumns() {
		return anchorColumns;
	}
	public void setAnchorColumns(String anchorColumns) {
		this.anchorColumns = anchorColumns;
	}
	
}
