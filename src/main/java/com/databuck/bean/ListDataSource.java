package com.databuck.bean;

import java.util.Date;

public class ListDataSource {

	// data member
	private int idData;
	private String name;
	private String description;
	private String dataLocation;
	private String dataSource;
	private long createdBy;
	private int idDataBlend;
	private Date createdAt;
	private Date updatedAt;
	private long updatedBy;
	private Long idDataSchema;
	private Long garbageRows;
	private String tableName;
	private Long ignoreRowsCount;
	private int domain;
	
	private String srcBrokerUri;
	private String srcTopicName;
	private String tarBrokerUri;
	private String tarTopicName;

	
	private String profilingEnabled;
	private String advancedRulesEnabled;

	private String schemaName; // changes for profiling UI [ by priyanka a1 ] 
	private String active;
	private String createdByUser;
	private int projectId;
	
	private String templateCreateSuccess;
	private String deltaApprovalStatus;
	private String projectName;
	
	private String CreDate;
	
	private String databaseName;
	
	

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		databaseName = databaseName;
	}
	
	
	public String getCreDate() {
		return CreDate;
	}

	public void setCreDate(String creDate) {
		CreDate = creDate;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public int getDomain() {
		return domain;
	}

	public void setDomain(int domain) {
		this.domain = domain;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public String getCreatedByUser() {
		return createdByUser;
	}

	public void setCreatedByUser(String createdByUser) {
		this.createdByUser = createdByUser;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}
	
	
	public String getProfilingEnabled() {
		return profilingEnabled;
	}

	public void setProfilingEnabled(String profilingEnabled) {
		this.profilingEnabled = profilingEnabled;
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

		
	
	
	public Long getIgnoreRowsCount() {
		return ignoreRowsCount;
	}

	public void setIgnoreRowsCount(Long ignoreRowsCount) {
		this.ignoreRowsCount = ignoreRowsCount;
	}

	public long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}
	public Long getIdDataSchema() {
		return idDataSchema;
	}

	public void setIdDataSchema(Long idDataSchema) {
		this.idDataSchema = idDataSchema;
	}

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

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	

	public int getIdDataBlend() {
		return idDataBlend;
	}

	public void setIdDataBlend(int idDataBlend) {
		this.idDataBlend = idDataBlend;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public long getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(long updatedBy) {
		this.updatedBy = updatedBy;
	}

	
	

	public Long getGarbageRows() {
		return garbageRows;
	}

	public void setGarbageRows(Long garbageRows) {
		this.garbageRows = garbageRows;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getAdvancedRulesEnabled() {
		return advancedRulesEnabled;
	}

	public void setAdvancedRulesEnabled(String advancedRulesEnabled) {
		this.advancedRulesEnabled = advancedRulesEnabled;
	}

	public ListDataSource() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ListDataSource(int idData, String name, String description, String dataLocation, String dataSource,
			long createdBy, int idDataBlend, Date createdAt, Date updatedAt, long updatedBy, Long idDataSchema,
			Long garbageRows, String tableName, Long ignoreRowsCount, String srcBrokerUri, String srcTopicName,
			String tarBrokerUri, String tarTopicName, String profilingEnabled, String advancedRulesEnabled, String databaseName) {
		super();
		this.idData = idData;
		this.name = name;
		this.description = description;
		this.dataLocation = dataLocation;
		this.dataSource = dataSource;
		this.createdBy = createdBy;
		this.idDataBlend = idDataBlend;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.updatedBy = updatedBy;
		this.idDataSchema = idDataSchema;
		this.garbageRows = garbageRows;
		this.tableName = tableName;
		this.ignoreRowsCount = ignoreRowsCount;
		this.srcBrokerUri = srcBrokerUri;
		this.srcTopicName = srcTopicName;
		this.tarBrokerUri = tarBrokerUri;
		this.tarTopicName = tarTopicName;
		this.profilingEnabled = profilingEnabled;
		this.advancedRulesEnabled = advancedRulesEnabled;
		this.databaseName = databaseName;
	}

	@Override
	public String toString() {
		return "ListDataSource [idData=" + idData + ", name=" + name + ", description=" + description
				+ ", dataLocation=" + dataLocation + ", dataSource=" + dataSource + ", createdBy=" + createdBy
				+ ", idDataBlend=" + idDataBlend + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt
				+ ", updatedBy=" + updatedBy + ", idDataSchema=" + idDataSchema + ", garbageRows=" + garbageRows
				+ ", tableName=" + tableName + ", ignoreRowsCount=" + ignoreRowsCount + ", srcBrokerUri=" + srcBrokerUri
				+ ", srcTopicName=" + srcTopicName + ", tarBrokerUri=" + tarBrokerUri + ", tarTopicName=" + tarTopicName + ", databaseName="+databaseName
				+ ", profilingEnabled=" + profilingEnabled + ", advancedRulesEnabled="+advancedRulesEnabled+"]";
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
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
