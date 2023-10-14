package com.databuck.dto;

import io.swagger.annotations.ApiModelProperty;

public class TemplateInfoDto {

	@ApiModelProperty(notes = "Id")
	private int idData;
	@ApiModelProperty(notes = "Name")
	private String name;
	@ApiModelProperty(notes = "Description")
	private String description;
	@ApiModelProperty(notes = "Data Location")
	private String dataLocation;
	@ApiModelProperty(notes = "Host Or Folder")
	private String hostOrFolder;
	@ApiModelProperty(notes = "Table Or File")
	private String tableOrFile;
	@ApiModelProperty(notes = "Query")
	private String query;
	@ApiModelProperty(notes = "Query String")
	private String queryString;
	@ApiModelProperty(notes = "Where Condition")
	private String whereCondition;
	@ApiModelProperty(notes = "Project Id")
	private int project_id;
	@ApiModelProperty(notes = "Created At")
	private String createdAt;
	
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDataLocation() {
		return dataLocation;
	}
	public void setDataLocation(String dataLocation) {
		this.dataLocation = dataLocation;
	}
	public String getHostOrFolder() {
		return hostOrFolder;
	}
	public void setHostOrFolder(String hostOrFolder) {
		this.hostOrFolder = hostOrFolder;
	}
	public String getTableOrFile() {
		return tableOrFile;
	}
	public void setTableOrFile(String tableOrFile) {
		this.tableOrFile = tableOrFile;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getQueryString() {
		return queryString;
	}
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}
	public String getWhereCondition() {
		return whereCondition;
	}
	public void setWhereCondition(String whereCondition) {
		this.whereCondition = whereCondition;
	}
	public int getProject_id() {
		return project_id;
	}
	public void setProject_id(int project_id) {
		this.project_id = project_id;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public String setCreatedAt() {
		return createdAt;
	}
	
}
