package com.databuck.bean;

public class RuleCatalogCheckSpecification {
	private String checkType;
	private String checkName;
	private String checkDisplayName;
	private String entityName;
	private String checkColumn;
	private String templateCheckEnabledColumn;
	private String templateCheckThresholdColumn;
	private long dimensionId;
	private String checkDescription;

	public RuleCatalogCheckSpecification(String checkType, String checkName, String checkDisplayName, String entityName,
			String checkColumn, String templateCheckEnabledColumn, String templateCheckThresholdColumn,
			long dimensionId, String checkDescription) {
		super();
		this.checkType = checkType;
		this.checkName = checkName;
		this.checkDisplayName = checkDisplayName;
		this.entityName = entityName;
		this.checkColumn = checkColumn;
		this.dimensionId = dimensionId;
		this.checkDescription = checkDescription;

		// If no threshold value column is present assign 0.0 as default value
		this.templateCheckEnabledColumn = templateCheckEnabledColumn;

		if (templateCheckThresholdColumn == null || templateCheckThresholdColumn.trim().isEmpty()) {
			templateCheckThresholdColumn = "0.0";
		}
		this.templateCheckThresholdColumn = templateCheckThresholdColumn;

	}

	public String getCheckType() {
		return checkType;
	}

	public void setCheckType(String checkType) {
		this.checkType = checkType;
	}

	public String getCheckName() {
		return checkName;
	}

	public void setCheckName(String checkName) {
		this.checkName = checkName;
	}

	public String getCheckDisplayName() {
		return checkDisplayName;
	}

	public void setCheckDisplayName(String checkDisplayName) {
		this.checkDisplayName = checkDisplayName;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public String getCheckColumn() {
		return checkColumn;
	}

	public void setCheckColumn(String checkColumn) {
		this.checkColumn = checkColumn;
	}

	public String getTemplateCheckEnabledColumn() {
		return templateCheckEnabledColumn;
	}

	public void setTemplateCheckEnabledColumn(String templateCheckEnabledColumn) {
		this.templateCheckEnabledColumn = templateCheckEnabledColumn;
	}

	public String getTemplateCheckThresholdColumn() {
		return templateCheckThresholdColumn;
	}

	public void setTemplateCheckThresholdColumn(String templateCheckThresholdColumn) {
		this.templateCheckThresholdColumn = templateCheckThresholdColumn;
	}

	public long getDimensionId() {
		return dimensionId;
	}

	public void setDimensionId(long dimensionId) {
		this.dimensionId = dimensionId;
	}

	public String getCheckDescription() {
		return checkDescription;
	}

	public void setCheckDescription(String checkDescription) {
		this.checkDescription = checkDescription;
	}

}
