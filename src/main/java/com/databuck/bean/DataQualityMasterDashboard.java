package com.databuck.bean;

public class DataQualityMasterDashboard {
	private Long idApp;
	private String date;
	private Long run;
	private String testRun;
	private String validationCheckName;
	private String connectionName;
	private Long templateId;
	private String source1;
	private Long recordCount;
	private String recordCountStatus;
	private String nullCountStatus;
	private String primaryKeyStatus;
	private String userSelectedFieldsStatus;
	private String numericalFieldStatus;
	private String stringFieldStatus;
	private String recordAnomalyStatus;
	private String dataDriftStatus;
	private String projectName;
	private String allFieldsStatus;
	private String fileName;
	private Double upperLimit;
	private Double lowerLimit;
	private String applyRules;
	private String isDerivedTemplate;
	private String profilingEnabled;
	private String dataLocation;
	private String databaseName;
	private String status;
	
	public String getProfilingEnabled() {
	    return profilingEnabled;
	}
	public void setProfilingEnabled(String profilingEnabled) {
	    this.profilingEnabled = profilingEnabled;
	}
	public String getIsDerivedTemplate() {
		return isDerivedTemplate;
	}
	public void setIsDerivedTemplate(String isDerivedTemplate) {
		this.isDerivedTemplate = isDerivedTemplate;
	}
	public String getApplyRules() {
		return applyRules;
	}
	public void setApplyRules(String applyRules) {
		this.applyRules = applyRules;
	}
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getAllFieldsStatus() {
		return allFieldsStatus;
	}

	public void setAllFieldsStatus(String allFieldsStatus) {
		this.allFieldsStatus = allFieldsStatus;
	}

	private double aggregateDQI; // added for UI [priyanka]

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public double getAggreagteDQI() {
		return aggregateDQI;
	}

	public void setAggreagteDQI(double aggreagteDQI) {
		this.aggregateDQI = aggreagteDQI;
	}

	public Long getIdApp() {
		return idApp;
	}

	public void setIdApp(Long idApp) {
		this.idApp = idApp;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Long getRun() {
		return run;
	}

	public void setRun(Long run) {
		this.run = run;
	}

	public String getValidationCheckName() {
		return validationCheckName;
	}

	public void setValidationCheckName(String validationCheckName) {
		this.validationCheckName = validationCheckName;
	}

	public String getSource1() {
		return source1;
	}

	public void setSource1(String source1) {
		this.source1 = source1;
	}

	public String getRecordCountStatus() {
		return recordCountStatus;
	}

	public void setRecordCountStatus(String recordCountStatus) {
		this.recordCountStatus = recordCountStatus;
	}

	public String getNullCountStatus() {
		return nullCountStatus;
	}

	public void setNullCountStatus(String nullCountStatus) {
		this.nullCountStatus = nullCountStatus;
	}

	public String getPrimaryKeyStatus() {
		return primaryKeyStatus;
	}

	public void setPrimaryKeyStatus(String primaryKeyStatus) {
		this.primaryKeyStatus = primaryKeyStatus;
	}

	public String getUserSelectedFieldsStatus() {
		return userSelectedFieldsStatus;
	}

	public void setUserSelectedFieldsStatus(String userSelectedFieldsStatus) {
		this.userSelectedFieldsStatus = userSelectedFieldsStatus;
	}

	public String getNumericalFieldStatus() {
		return numericalFieldStatus;
	}

	public void setNumericalFieldStatus(String numericalFieldStatus) {
		this.numericalFieldStatus = numericalFieldStatus;
	}

	public String getStringFieldStatus() {
		return stringFieldStatus;
	}

	public void setStringFieldStatus(String stringFieldStatus) {
		this.stringFieldStatus = stringFieldStatus;
	}

	public String getRecordAnomalyStatus() {
		return recordAnomalyStatus;
	}

	public void setRecordAnomalyStatus(String recordAnomalyStatus) {
		this.recordAnomalyStatus = recordAnomalyStatus;
	}

	public String getDataDriftStatus() {
		return dataDriftStatus;
	}

	public void setDataDriftStatus(String dataDriftStatus) {
		this.dataDriftStatus = dataDriftStatus;
	}

	public String getTestRun() {
		return testRun;
	}

	public void setTestRun(String testRun) {
		this.testRun = testRun;
	}

	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	public Long getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(Long recordCount) {
		this.recordCount = recordCount;
	}

	public String getConnectionName() {
		return connectionName;
	}

	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}

	public Double getUpperLimit() {
		return upperLimit;
	}

	public void setUpperLimit(Double upperLimit) {
		this.upperLimit = upperLimit;
	}

	public Double getLowerLimit() {
		return lowerLimit;
	}

	public void setLowerLimit(Double lowerLimit) {
		this.lowerLimit = lowerLimit;
	}
	
	public String getDataLocation() {
		return dataLocation;
	}

	public void setDataLocation(String dataLocation) {
		this.dataLocation = dataLocation;
	}
	
	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
