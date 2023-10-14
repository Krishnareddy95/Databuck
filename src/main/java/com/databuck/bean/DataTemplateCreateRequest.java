package com.databuck.bean;

public class DataTemplateCreateRequest {

	private Long idUser;
	private String dataTemplateName;
	private String dataLocation;
	private String createdByUser;
	private String hostName;
	private String schemaName;
	private String srcBrokerUri;
	private String srcTopicName;
	private String dataFormat;
	private String userName;
	private String pwd;
	private String tarBrokerUri;
	private String tarTopicName;
	private String description;
	private Long idDataSchema;
	private String headerId;
	private String rowsId;
	private String tableName;
	private String tableNameList;
	private String whereId;
	private String queryCheckboxId;
	private String historicDateTable;
	private String selectedTables;
	private String queryTextboxId;
	private String fileQueryCheckboxid;
	private String fileQueryTextboxid;
	private String incrementalSourceId;
	private String dateFormatId;
	private String sliceStartId;
	private String sliceEndId;
	private String profilingEnabled;
	private String domainfunction;
	private Long projectId;
	private Integer domainId;
	private String advancedRulesEnabled;
	private String rollingHeaderPresent;
	private String rollingColumn;


	public Long getIdUser() {
		return idUser;
	}

	public void setIdUser(Long idUser) {
		this.idUser = idUser;
	}

	public String getDataLocation() {
		return dataLocation;
	}

	public void setDataLocation(String dataLocation) {
		this.dataLocation = dataLocation;
	}

	public String getCreatedByUser() {
		return createdByUser;
	}

	public void setCreateByUser(String createdByUser) {
		this.createdByUser = createdByUser;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public String getSrcBrokerUri() {
		return srcBrokerUri;
	}

	public void setSrcBrokerUri(String srcBrokerUri) {
		this.srcBrokerUri = srcBrokerUri;
	}

	public String getSrcTopicName() {
		return srcTopicName;
	}

	public void setSrcTopicName(String srcTopicName) {
		this.srcTopicName = srcTopicName;
	}

	public String getDataFormat() {
		return dataFormat;
	}

	public void setDataFormat(String dataFormat) {
		this.dataFormat = dataFormat;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getTarBrokerUri() {
		return tarBrokerUri;
	}

	public void setTarBrokerUri(String tarBrokerUri) {
		this.tarBrokerUri = tarBrokerUri;
	}

	public String getTarTopicName() {
		return tarTopicName;
	}

	public void setTarTopicName(String tarTopicName) {
		this.tarTopicName = tarTopicName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getHeaderId() {
		return headerId;
	}

	public void setHeaderId(String headerId) {
		this.headerId = headerId;
	}

	public String getRowsId() {
		return rowsId;
	}

	public void setRowsId(String rowsId) {
		this.rowsId = rowsId;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableNameList() {
		return tableNameList;
	}

	public void setTableNameList(String tableNameList) {
		this.tableNameList = tableNameList;
	}

	public Long getIdDataSchema() {
		return idDataSchema;
	}

	public void setIdDataSchema(Long idDataSchema) {
		this.idDataSchema = idDataSchema;
	}

	public String getWhereId() {
		return whereId;
	}

	public void setWhereId(String whereId) {
		this.whereId = whereId;
	}

	public String getQueryCheckboxId() {
		return queryCheckboxId;
	}

	public void setQueryCheckboxId(String queryCheckboxId) {
		this.queryCheckboxId = queryCheckboxId;
	}

	public String getHistoricDateTable() {
		return historicDateTable;
	}

	public void setHistoricDateTable(String historicDateTable) {
		this.historicDateTable = historicDateTable;
	}

	public String getSelectedTables() {
		return selectedTables;
	}

	public void setSelectedTables(String selectedTables) {
		this.selectedTables = selectedTables;
	}

	public String getQueryTextboxId() {
		return queryTextboxId;
	}

	public void setQueryTextboxId(String queryTextboxId) {
		this.queryTextboxId = queryTextboxId;
	}


	public String getFileQueryCheckboxid() {
		return fileQueryCheckboxid;
	}

	public void setFileQueryCheckboxid(String fileQueryCheckboxid) {
		this.fileQueryCheckboxid = fileQueryCheckboxid;
	}

	public String getFileQueryTextboxid() {
		return fileQueryTextboxid;
	}

	public void setFileQueryTextboxid(String fileQueryTextboxid) {
		this.fileQueryTextboxid = fileQueryTextboxid;
	}

	public String getIncrementalSourceId() {
		return incrementalSourceId;
	}

	public void setIncrementalSourceId(String incrementalSourceId) {
		this.incrementalSourceId = incrementalSourceId;
	}

	public String getDateFormatId() {
		return dateFormatId;
	}

	public void setDateFormatId(String dateFormatId) {
		this.dateFormatId = dateFormatId;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public String getSliceStartId() {
		return sliceStartId;
	}

	public void setSliceStartId(String sliceStartId) {
		this.sliceStartId = sliceStartId;
	}

	public String getSliceEndId() {
		return sliceEndId;
	}

	public void setSliceEndId(String sliceEndId) {
		this.sliceEndId = sliceEndId;
	}

	public String getProfilingEnabled() {
		return profilingEnabled;
	}

	public void setProfilingEnabled(String profilingEnabled) {
		this.profilingEnabled = profilingEnabled;
	}

	public String getDomainfunction() {
		return domainfunction;
	}

	public void setDomainfunction(String domainfunction) {
		this.domainfunction = domainfunction;
	}

	public Integer getDomainId() {
		return domainId;
	}

	public void setDomainId(Integer domainId) {
		this.domainId = domainId;
	}

	public String getAdvancedRulesEnabled() {
		return advancedRulesEnabled;
	}

	public void setAdvancedRulesEnabled(String advancedRulesEnabled) {
		this.advancedRulesEnabled = advancedRulesEnabled;
	}

	public String getRollingHeaderPresent() {
		return rollingHeaderPresent;
	}

	public void setRollingHeaderPresent(String rollingHeaderPresent) {
		this.rollingHeaderPresent = rollingHeaderPresent;
	}

	public String getRollingColumn() {
		return rollingColumn;
	}

	public void setRollingColumn(String rollingColumn) {
		this.rollingColumn = rollingColumn;
	}

	public String getDataTemplateName() {
		return dataTemplateName;
	}

	public void setDataTemplateName(String dataTemplateName) {
		this.dataTemplateName = dataTemplateName;
	}

}
