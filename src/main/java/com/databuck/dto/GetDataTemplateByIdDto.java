package com.databuck.dto;

import io.swagger.annotations.ApiModelProperty;

public class GetDataTemplateByIdDto {

	@ApiModelProperty(notes = "Id")
	private int idData;
	@ApiModelProperty(notes = "Schema Id")
	private long idDataSchema;
	@ApiModelProperty(notes = "Name")
	private String name;
	@ApiModelProperty(notes = "Data Location")
	private String dataLocation;
	@ApiModelProperty(notes = "Table Name")
	private String tableName;
	@ApiModelProperty(notes = "Host Name")
	private String hostName;
	@ApiModelProperty(notes = "Port")
	private String port;
	@ApiModelProperty(notes = "Username")
	private String userName;
	@ApiModelProperty(notes = "Schema Name")
	private String schemaName;
	@ApiModelProperty(notes = "Folder Name")
	private String folderName;
	@ApiModelProperty(notes = "Query")
	private String query;
	@ApiModelProperty(notes = "Query String")
	private String queryString;
	@ApiModelProperty(notes = "Incremental Type")
	private String incrementalType;
	@ApiModelProperty(notes = "Where Condition")
	private String whereCondition;
	@ApiModelProperty(notes = "Domain")
	private String domain;
	@ApiModelProperty(notes = "File Header")
	private String fileHeader;
	@ApiModelProperty(notes = "Rolling Header")
	private String rollingHeader;
	@ApiModelProperty(notes = "Rolling Column")
	private String rollingColumn;
	@ApiModelProperty(notes = "Date Format")
	private String dateFormat;
	@ApiModelProperty(notes = "Slice Start")
	private String sliceStart;
	@ApiModelProperty(notes = "Slice End")
	private String sliceEnd;
	@ApiModelProperty(notes = "Is Profiling Enabled")
	private String profilingEnabled;
	@ApiModelProperty(notes = "Is Advanced Rules Enabled")
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
	public long getIdDataSchema() {
		return idDataSchema;
	}
	public void setIdDataSchema(long idDataSchema) {
		this.idDataSchema = idDataSchema;
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
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getSchemaName() {
		return schemaName;
	}
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}
	public String getFolderName() {
		return folderName;
	}
	public void setFolderName(String folderName) {
		this.folderName = folderName;
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
	public String getIncrementalType() {
		return incrementalType;
	}
	public void setIncrementalType(String incrementalType) {
		this.incrementalType = incrementalType;
	}
	public String getWhereCondition() {
		return whereCondition;
	}
	public void setWhereCondition(String whereCondition) {
		this.whereCondition = whereCondition;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getFileHeader() {
		return fileHeader;
	}
	public void setFileHeader(String fileHeader) {
		this.fileHeader = fileHeader;
	}
	public String getRollingHeader() {
		return rollingHeader;
	}
	public void setRollingHeader(String rollingHeader) {
		this.rollingHeader = rollingHeader;
	}
	public String getRollingColumn() {
		return rollingColumn;
	}
	public void setRollingColumn(String rollingColumn) {
		this.rollingColumn = rollingColumn;
	}
	public String getDateFormat() {
		return dateFormat;
	}
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}
	public String getSliceStart() {
		return sliceStart;
	}
	public void setSliceStart(String sliceStart) {
		this.sliceStart = sliceStart;
	}
	public String getSliceEnd() {
		return sliceEnd;
	}
	public void setSliceEnd(String sliceEnd) {
		this.sliceEnd = sliceEnd;
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
