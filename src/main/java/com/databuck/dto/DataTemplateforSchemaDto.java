package com.databuck.dto;

import io.swagger.annotations.ApiModelProperty;

public class DataTemplateforSchemaDto {

	@ApiModelProperty(notes = "Id")
	private int idData;
	
	@ApiModelProperty(notes = "Name")
	private String name;
	
	@ApiModelProperty(notes = "Data Location")
	private String dataLocation;
	
	@ApiModelProperty(notes = "Table Name")
	private String tableName;
	
	@ApiModelProperty(notes = "Created At")
	private String createdAt;
	
	@ApiModelProperty(notes = "is Profiling Enabled")
	private String profilingEnabled;
	
	@ApiModelProperty(notes = "is Advanced Rules Enabled")
	private String advancedRulesEnabled;
	
	@ApiModelProperty(notes = "Template Create Message")
	private String templateCreateSuccess;
	
	@ApiModelProperty(notes = "Delta Approval Status")
	private String deltaApprovalStatus;
	
	public int getIdData() {
		return idData;
	}
	public void setIdData(int idData) {
		this.idData = idData;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDataLocation() {
		return dataLocation;
	}
	public void setDataLocation(String dataLocation) {
		this.dataLocation = dataLocation;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public String getProfilingEnabled() {
		return profilingEnabled;
	}
	public void setProfilingEnabled(String profilingEnabled) {
		this.profilingEnabled = profilingEnabled;
	}
	public String getAdvancedRulesEnabled() {
		return advancedRulesEnabled;
	}
	public void setAdvancedRulesEnabled(String advancedRulesEnabled) {
		this.advancedRulesEnabled = advancedRulesEnabled;
	}
	public String getTemplateCreateSuccess() {
		return templateCreateSuccess;
	}
	public void setTemplateCreateSuccess(String templateCreateSuccess) {
		this.templateCreateSuccess = templateCreateSuccess;
	}
	public String getDeltaApprovalStatus() {
		return deltaApprovalStatus;
	}
	public void setDeltaApprovalStatus(String deltaApprovalStatus) {
		this.deltaApprovalStatus = deltaApprovalStatus;
	}
	
	
}
