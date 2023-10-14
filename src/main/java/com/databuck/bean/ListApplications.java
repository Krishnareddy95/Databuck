package com.databuck.bean;

import java.util.Date;

public class ListApplications {

	private String schema;
	private String name;
	private long idApp;
	private String description;
	private String appType;
	private Long idData;
	private Long idLeftData;
	private int idRightData;
	private String createdBy;
	private String createdAt;
	private String updatedBy;
	private String updatedAt;
	private String fileNameValidation;
	private int garbageRows;
	private String entityColumn;
	private String colOrderValidation;
	private String nonNullCheck;
	private Double nonNullThreshold;
	private String numericalStatCheck;
	private Double numericalStatThreshold;
	private String stringStatCheck;
	private Double stringStatThreshold;
	private String recordAnomalyCheck;
	private Double recordAnomalyThreshold;
	private String dataDriftCheck;
	private Double dataDriftThreshold;
	private String recordCountAnomaly;
	private Double recordCountAnomalyThreshold;
	private String outOfNormCheck;
	private String applyRules;
	private String applyDerivedColumns;
	private String keyGroupRecordCountAnomaly;
	private String updateFrequency;
	private String keyBasedRecordCountAnomaly;
	private int frequencyDays;
	private String buildHistoricFingerPrint;
	private String historicStartDate;
	private String historicEndDate;
	private String historicDateFormat;
	private String incrementalMatching;
	private String csvDir;
	private String groupEquality;
	private Double groupEqualityThreshold;
	private String timeSeries;
	private Double matchingThreshold;
	private Date incrementalTimestamp;

	private String dupRowAll;
	private String dupRowIdentity;
	private Double dupRowAllThreshold;
	private Double dupRowIdentityThreshold;
	private String duplicateCheck;
	private String startDateChk;
	private String endDateChk;
	private String timelinessKeyChk;
	private String defaultCheck;

	private String patternCheck;
	private String dateRuleChk;
	private String badData;
	private String prefix1;
	private String prefix2;
	private String defaultValues;
	private String dGroupNullCheck;
	private String dGroupDateRuleCheck;

	private String active;

	// changes for kafka
	private int windowTime;
	private String startTime;
	private String endTime;
	private String fileMonitoringType;
	private Double validityThreshold;
	private String dGroupDataDriftCheck;

	private Long rollTargetSchemaId;
	private int thresholdsApplyOption;    // Pradeep 8-Mar-2020 for global thresholds feature
	
	private String continuousFileMonitoring;
	private String rollType;
	private String createdByUser;
	private long domainId;
	
	private int data_domain;
	
	private int approveBy;
	private String approverName;
	private int approveStatus; 
	private String approveComments;
	private String approveDate;
	private int stagingApproveStatus;
	private String subcribedEmailId;
	
	private String defaultPatternCheck;

	private Double lengthCheckThreshold; // added for DC-148

	private String reprofiling;

	private String validationJobSize;

	public String getValidationJobSize() {
		return validationJobSize;
	}

	public void setValidationJobSize(String validationJobSize) {
		this.validationJobSize = validationJobSize;
	}

	public String getReprofiling() {
		return reprofiling;
	}

	public void setReprofiling(String reprofiling) {
		this.reprofiling = reprofiling;
	}

	public Double getLengthCheckThreshold() {
		return lengthCheckThreshold;
	}

	public void setLengthCheckThreshold(Double lengthCheckThreshold) {
		this.lengthCheckThreshold = lengthCheckThreshold;
	}

	public int getData_domain() {
		return data_domain;
	}

	public void setData_domain(int data_domain) {
		this.data_domain = data_domain;
	}

	public long getDomainId() {
		return domainId;
	}

	public void setDomainId(long domainId) {
		this.domainId = domainId;
	}
	
	public String getCreatedByUser() {
		return createdByUser;
	}
	public void setCreatedByUser(String createdByUser) {
		this.createdByUser = createdByUser;
	}

	public String getdGroupDataDriftCheck() {
		return dGroupDataDriftCheck;
	}
	public void setdGroupDataDriftCheck(String dGroupDataDriftCheck) {
		this.dGroupDataDriftCheck = dGroupDataDriftCheck;
	}
	public Double getValidityThreshold() {
		return validityThreshold;
	}
	public void setValidityThreshold(Double validityThreshold) {
		this.validityThreshold = validityThreshold;
	}
	public int getWindowTime() {
		return windowTime;
	}
	public void setWindowTime(int windowTime) {
		this.windowTime = windowTime;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getDefaultValues() {
		return defaultValues;
	}
	public void setDefaultValues(String defaultValues) {
		this.defaultValues = defaultValues;
	}

	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	public String getCorrelationcheck() {
		return correlationcheck;
	}
	public void setCorrelationcheck(String correlationcheck) {
		this.correlationcheck = correlationcheck;
	}
	public long getProjectId() {
		return projectId;
	}
	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}
	private String correlationcheck;
	private long projectId;

	//24_DEC_2018 (12.43pm) Priyanka
	private String lengthCheck;
	
	//Max Length Check
	private String maxLengthCheck;
	
	

	public String getMaxLengthCheck() {
		return maxLengthCheck;
	}

	public void setMaxLengthCheck(String maxLengthCheck) {
		this.maxLengthCheck = maxLengthCheck;
	}

	public String getPrefix1() {
		return prefix1;
	}
	public void setPrefix1(String prefix1) {
		this.prefix1 = prefix1;
	}
	public String getPrefix2() {
		return prefix2;
	}
	public void setPrefix2(String prefix2) {
		this.prefix2 = prefix2;
	}

	public String getlengthCheck() {
		return lengthCheck;
	}
	public void setlengthCheck(String lengthCheck) {
		this.lengthCheck = lengthCheck;
	}
	public String getDateRuleChk(){
		return dateRuleChk;
	}
	public void setDateRuleChk(String dateRuleChk){
		this.dateRuleChk = dateRuleChk;
	}

	public String getPatternCheck() {
		return patternCheck;
	}
	public void setPatternCheck(String patternCheck) {
		this.patternCheck = patternCheck;
	}
	public String getStartDateChk() {
		return startDateChk;
	}
	public void setStartDateChk(String startDateChk) {
		this.startDateChk = startDateChk;
	}
	public String getEndDateChk() {
		return endDateChk;
	}
	public void setEndDateChk(String endDateChk) {
		this.endDateChk = endDateChk;
	}
	public String getTimelinessKeyChk() {
		return timelinessKeyChk;
	}
	public void setTimelinessKeyChk(String timelinessKeyChk) {
		this.timelinessKeyChk = timelinessKeyChk;
	}
	public String getDupRowAll() {
		return dupRowAll;
	}
	public void setDupRowAll(String dupRowAll) {
		this.dupRowAll = dupRowAll;
	}
	public String getDupRowIdentity() {
		return dupRowIdentity;
	}
	public void setDupRowIdentity(String dupRowIdentity) {
		this.dupRowIdentity = dupRowIdentity;
	}
	public Double getDupRowAllThreshold() {
		return dupRowAllThreshold;
	}
	public void setDupRowAllThreshold(Double dupRowAllThreshold) {
		this.dupRowAllThreshold = dupRowAllThreshold;
	}
	public Double getDupRowIdentityThreshold() {
		return dupRowIdentityThreshold;
	}
	public void setDupRowIdentityThreshold(Double dupRowIdentityThreshold) {
		this.dupRowIdentityThreshold = dupRowIdentityThreshold;
	}
	public String getCsvDir() {
		return csvDir;
	}
	public void setCsvDir(String csvDir) {
		this.csvDir = csvDir;
	}
	public String getGroupEquality() {
		return groupEquality;
	}
	public void setGroupEquality(String groupEquality) {
		this.groupEquality = groupEquality;
	}
	public Double getGroupEqualityThreshold() {
		return groupEqualityThreshold;
	}
	public void setGroupEqualityThreshold(Double groupEqualityThreshold) {
		this.groupEqualityThreshold = groupEqualityThreshold;
	}
	public String getHistoricStartDate() {
		return historicStartDate;
	}
	public void setHistoricStartDate(String historicStartDate) {
		this.historicStartDate = historicStartDate;
	}
	public String getHistoricEndDate() {
		return historicEndDate;
	}
	public void setHistoricEndDate(String historicEndDate) {
		this.historicEndDate = historicEndDate;
	}
	public String getHistoricDateFormat() {
		return historicDateFormat;
	}
	public void setHistoricDateFormat(String historicDateFormat) {
		this.historicDateFormat = historicDateFormat;
	}

	public String getDataDriftCheck() {
		return dataDriftCheck;
	}
	public void setDataDriftCheck(String dataDriftCheck) {
		this.dataDriftCheck = dataDriftCheck;
	}
	public String getRecordCountAnomaly() {
		return recordCountAnomaly;
	}
	public void setRecordCountAnomaly(String recordCountAnomaly) {
		this.recordCountAnomaly = recordCountAnomaly;
	}
	public String getNonNullCheck() {
		return nonNullCheck;
	}
	public void setNonNullCheck(String nonNullCheck) {
		this.nonNullCheck = nonNullCheck;
	}
	public String getNumericalStatCheck() {
		return numericalStatCheck;
	}
	public void setNumericalStatCheck(String numericalStatCheck) {
		this.numericalStatCheck = numericalStatCheck;
	}
	public String getStringStatCheck() {
		return stringStatCheck;
	}
	public void setStringStatCheck(String stringStatCheck) {
		this.stringStatCheck = stringStatCheck;
	}
	public String getRecordAnomalyCheck() {
		return recordAnomalyCheck;
	}
	public void setRecordAnomalyCheck(String recordAnomalyCheck) {
		this.recordAnomalyCheck = recordAnomalyCheck;
	}

	public Double getRecordAnomalyThreshold() {
		return recordAnomalyThreshold;
	}
	public void setRecordAnomalyThreshold(Double recordAnomalyThreshold) {
		this.recordAnomalyThreshold = recordAnomalyThreshold;
	}

	public String getFileNameValidation() {
		return fileNameValidation;
	}
	public void setFileNameValidation(String fileNameValidation) {
		this.fileNameValidation = fileNameValidation;
	}
	public int getGarbageRows() {
		return garbageRows;
	}
	public void setGarbageRows(int garbageRows) {
		this.garbageRows = garbageRows;
	}
	public String getEntityColumn() {
		return entityColumn;
	}
	public void setEntityColumn(String entityColumn) {
		this.entityColumn = entityColumn;
	}
	public String getColOrderValidation() {
		return colOrderValidation;
	}
	public void setColOrderValidation(String colOrderValidation) {
		this.colOrderValidation = colOrderValidation;
	}


	public String getSchema() {
		return schema;
	}
	public void setSchema(String schema) {
		this.schema = schema;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAppType() {
		return appType;
	}
	public void setAppType(String appType) {
		this.appType = appType;
	}
	public int getIdRightData() {
		return idRightData;
	}
	public void setIdRightData(int idRightData) {
		this.idRightData = idRightData;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public String getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	public String getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getIdApp() {
		return idApp;
	}
	public void setIdApp(long idApp) {
		this.idApp = idApp;
	}
	public Double getRecordCountAnomalyThreshold() {
		return recordCountAnomalyThreshold;
	}
	public void setRecordCountAnomalyThreshold(Double recordCountAnomalyThreshold) {
		this.recordCountAnomalyThreshold = recordCountAnomalyThreshold;
	}
	public String getOutOfNormCheck() {
		return outOfNormCheck;
	}
	public void setOutOfNormCheck(String outOfNormCheck) {
		this.outOfNormCheck = outOfNormCheck;
	}
	public String getApplyRules() {
		return applyRules;
	}
	public void setApplyRules(String applyRules) {
		this.applyRules = applyRules;
	}
	public String getApplyDerivedColumns() {
		return applyDerivedColumns;
	}
	public void setApplyDerivedColumns(String applyDerivedColumns) {
		this.applyDerivedColumns = applyDerivedColumns;
	}
	public String getKeyGroupRecordCountAnomaly() {
		return keyGroupRecordCountAnomaly;
	}
	public void setKeyGroupRecordCountAnomaly(String keyGroupRecordCountAnomaly) {
		this.keyGroupRecordCountAnomaly = keyGroupRecordCountAnomaly;
	}
	public int getFrequencyDays() {
		return frequencyDays;
	}
	public void setFrequencyDays(int frequencyDays) {
		this.frequencyDays = frequencyDays;
	}
	public String getUpdateFrequency() {
		return updateFrequency;
	}
	public void setUpdateFrequency(String updateFrequency) {
		this.updateFrequency = updateFrequency;
	}
	public String getBuildHistoricFingerPrint() {
		return buildHistoricFingerPrint;
	}
	public void setBuildHistoricFingerPrint(String buildHistoricFingerPrint) {
		this.buildHistoricFingerPrint = buildHistoricFingerPrint;
	}
	public String getIncrementalMatching() {
		return incrementalMatching;
	}
	public void setIncrementalMatching(String incrementalMatching) {
		this.incrementalMatching = incrementalMatching;
	}
	public String getTimeSeries() {
		return timeSeries;
	}
	public void setTimeSeries(String timeSeries) {
		this.timeSeries = timeSeries;
	}
	public Double getMatchingThreshold() {
		return matchingThreshold;
	}
	public void setMatchingThreshold(Double matchingThreshold) {
		this.matchingThreshold = matchingThreshold;
	}
	public Double getDataDriftThreshold() {
		return dataDriftThreshold;
	}
	public void setDataDriftThreshold(Double dataDriftThreshold) {
		this.dataDriftThreshold = dataDriftThreshold;
	}
	public Double getNumericalStatThreshold() {
		return numericalStatThreshold;
	}
	public void setNumericalStatThreshold(Double numericalStatThreshold) {
		this.numericalStatThreshold = numericalStatThreshold;
	}
	public Double getStringStatThreshold() {
		return stringStatThreshold;
	}
	public void setStringStatThreshold(Double stringStatThreshold) {
		this.stringStatThreshold = stringStatThreshold;
	}
	public Double getNonNullThreshold() {
		return nonNullThreshold;
	}
	public void setNonNullThreshold(Double nonNullThreshold) {
		this.nonNullThreshold = nonNullThreshold;
	}
	public String getkeyBasedRecordCountAnomaly() {
		return keyBasedRecordCountAnomaly;
	}
	public void setkeyBasedRecordCountAnomaly(String keyBasedRecordCountAnomaly) {
	this.keyBasedRecordCountAnomaly = keyBasedRecordCountAnomaly;
	}
	public Long getIdData() {
		return idData;
	}
	public void setIdData(Long idData) {
		this.idData = idData;
	}
	public String getDuplicateCheck() {
		return duplicateCheck;
	}
	public void setDuplicateCheck(String duplicateCheck) {
		this.duplicateCheck = duplicateCheck;
	}
	public Date getIncrementalTimestamp() {
		return incrementalTimestamp;
	}
	public void setIncrementalTimestamp(Date incrementalTimestamp) {
		this.incrementalTimestamp = incrementalTimestamp;
	}

	public String getDefaultCheck() {
		return defaultCheck;
	}
	public void setDefaultCheck(String defaultCheck) {
		this.defaultCheck = defaultCheck;
	}


	public String getBadData() {
		return badData;
	}
	public void setBadData(String badData) {
		this.badData = badData;
	}

	public Long getIdLeftData() {
		return idLeftData;
	}
	public void setIdLeftData(Long idLeftData) {
		this.idLeftData = idLeftData;
	}
	public String getdGroupNullCheck() {
		return dGroupNullCheck;
	}
	public void setdGroupNullCheck(String dGroupNullCheck) {
		this.dGroupNullCheck = dGroupNullCheck;
	}
	public String getdGroupDateRuleCheck() {
		return dGroupDateRuleCheck;
	}
	public void setdGroupDateRuleCheck(String dGroupDateRuleCheck) {
		this.dGroupDateRuleCheck = dGroupDateRuleCheck;
	}
	public String getFileMonitoringType() {
		return fileMonitoringType;
	}
	public void setFileMonitoringType(String fileMonitoringType) {
		this.fileMonitoringType = fileMonitoringType;
	}
	public Long getRollTargetSchemaId() {
		return rollTargetSchemaId;
	}
	public void setRollTargetSchemaId(Long rollTargetSchemaId) {
		this.rollTargetSchemaId = rollTargetSchemaId;
	}

	public int getThresholdsApplyOption() {
		return thresholdsApplyOption;
	}
	public void setThresholdsApplyOption(int thresholdsApplyOption) {
		this.thresholdsApplyOption = thresholdsApplyOption;
	}
	public String getContinuousFileMonitoring() {
		return continuousFileMonitoring;
	}
	public void setContinuousFileMonitoring(String continuousFileMonitoring) {
		this.continuousFileMonitoring = continuousFileMonitoring;
	}
	public String getRollType() {
		return rollType;
	}
	public void setRollType(String rollType) {
		this.rollType = rollType;
	}

	public int getApproveBy() {
		return approveBy;
	}

	public void setApproveBy(int approveBy) {
		this.approveBy = approveBy;
	}

	public String getApproverName() {
		return approverName;
	}

	public void setApproverName(String approverName) {
		this.approverName = approverName;
	}

	public int getApproveStatus() {
		return approveStatus;
	}

	public void setApproveStatus(int approveStatus) {
		this.approveStatus = approveStatus;
	}

	public String getApproveDate() {
		return approveDate;
	}

	public void setApproveDate(String approveDate) {
		this.approveDate = approveDate;
	}

	public int getStagingApproveStatus() {
		return stagingApproveStatus;
	}

	public void setStagingApproveStatus(int stagingApproveStatus) {
		this.stagingApproveStatus = stagingApproveStatus;
	}

	public String getSubcribedEmailId() {
		return subcribedEmailId;
	}

	public void setSubcribedEmailId(String subcribedEmailId) {
		this.subcribedEmailId = subcribedEmailId;
	}

	public String getApproveComments() {
		return approveComments;
	}

	public void setApproveComments(String approveComments) {
		this.approveComments = approveComments;
	}
	
	public String getDefaultPatternCheck() {
		return defaultPatternCheck;
	}

	public void setDefaultPatternCheck(String defaultPatternCheck) {
		this.defaultPatternCheck = defaultPatternCheck;
	}
	
}