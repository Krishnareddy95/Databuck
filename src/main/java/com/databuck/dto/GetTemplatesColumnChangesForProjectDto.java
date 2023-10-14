package com.databuck.dto;

import io.swagger.annotations.ApiModelProperty;

public class GetTemplatesColumnChangesForProjectDto {
	
	@ApiModelProperty(notes = "Template Id")
	private String templateId;
	@ApiModelProperty(notes = "Template Name")
	private String templateName;
	@ApiModelProperty(notes = "Connection Id")
	private String connectionId;
	@ApiModelProperty(notes = "Connection Name")
	private String connectionName;
	@ApiModelProperty(notes = "Connection Type")
	private String connectionType;
	@ApiModelProperty(notes = "Table Or File")
	private String tableOrFile;
	@ApiModelProperty(notes = "Added Columns")
	private String addedColumns;
	@ApiModelProperty(notes = "Missing Columns")
	private String missingColumns;
	@ApiModelProperty(notes = "Change Detected Time")
	private String changeDetectedTime;
	
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public String getConnectionId() {
		return connectionId;
	}
	public void setConnectionId(String connectionId) {
		this.connectionId = connectionId;
	}
	public String getConnectionName() {
		return connectionName;
	}
	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}
	public String getConnectionType() {
		return connectionType;
	}
	public void setConnectionType(String connectionType) {
		this.connectionType = connectionType;
	}
	public String getTableOrFile() {
		return tableOrFile;
	}
	public void setTableOrFile(String tableOrFile) {
		this.tableOrFile = tableOrFile;
	}
	public String getAddedColumns() {
		return addedColumns;
	}
	public void setAddedColumns(String addedColumns) {
		this.addedColumns = addedColumns;
	}
	public String getMissingColumns() {
		return missingColumns;
	}
	public void setMissingColumns(String missingColumns) {
		this.missingColumns = missingColumns;
	}
	public String getChangeDetectedTime() {
		return changeDetectedTime;
	}
	public void setChangeDetectedTime(String changeDetectedTime) {
		this.changeDetectedTime = changeDetectedTime;
	}

}
