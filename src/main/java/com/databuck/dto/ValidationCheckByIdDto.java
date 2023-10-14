package com.databuck.dto;

import io.swagger.annotations.ApiModelProperty;

public class ValidationCheckByIdDto {
	
	@ApiModelProperty(notes = "App Id")
	private long idApp;
	@ApiModelProperty(notes = "Name")
	private String name;
	@ApiModelProperty(notes = "App Type")
	private String appType;
	@ApiModelProperty(notes = "Data Id")
	private Long idData;
	@ApiModelProperty(notes = "Right Data Id")
	private int idRightData;
	@ApiModelProperty(notes = "Left Data Id")
	private Long idLeftData;
	@ApiModelProperty(notes = "File Name Validation")
	private String fileNameValidation;
	@ApiModelProperty(notes = "Entity Column")
	private String entityColumn;
	@ApiModelProperty(notes = "Column Order Validation")
	private String colOrderValidation;
	@ApiModelProperty(notes = "Numberical Stat Check")
	private String numericalStatCheck;
	@ApiModelProperty(notes = "String Stat Check")
	private String stringStatCheck;
	@ApiModelProperty(notes = "Timeliness Key Check")
	private String timelinessKeyCheck;
	@ApiModelProperty(notes = "Record Anomaly Check")
	private String recordAnomalyCheck;
	@ApiModelProperty(notes = "Non Null Check")
	private String nonNullCheck;
	@ApiModelProperty(notes = "Data Drift Check")
	private String dataDriftCheck;
	@ApiModelProperty(notes = "Record Count Anomaly")
	private String recordCountAnomaly;
	@ApiModelProperty(notes = "Record Count Anomaly Threshold")
	private Double recordCountAnomalyThreshold;
	@ApiModelProperty(notes = "Out of Norm Check")
	private String outOfNormCheck;
	@ApiModelProperty(notes = "Apply Rules")
	private String applyRules;
	@ApiModelProperty(notes = "Apply Derived Columns")
	private String applyDerivedColumns;
	@ApiModelProperty(notes = "Key Group Record Count Anomaly")
	private String keyGroupRecordCountAnomaly;
	@ApiModelProperty(notes = "Update Frquency")
	private String updateFrequency;
	@ApiModelProperty(notes = "Frequency Days")
	private int frequencyDays;
	@ApiModelProperty(notes = "Icremental Matching")
	private String incrementalMatching;
	@ApiModelProperty(notes = "Build Historic Finger Print")
	private String buildHistoricFingerPrint;
	@ApiModelProperty(notes = "Historic Start Date")
	private String historicStartDate;
	@ApiModelProperty(notes = "Historic End Date")
	private String historicEndDate;
	@ApiModelProperty(notes = "Historic Date Format")
	private String historicDateFormat;
	@ApiModelProperty(notes = "CSV Directory")
	private String csvDir;
	@ApiModelProperty(notes = "Group Equality")
	private String groupEquality;
	@ApiModelProperty(notes = "Group Equality Threshold")
	private Double groupEqualityThreshold;
	@ApiModelProperty(notes = "Prefix1")
	private String prefix1;
	@ApiModelProperty(notes = "Prefix2")
	private String prefix2;
	@ApiModelProperty(notes = "Time Series")
	private String timeSeries;
	@ApiModelProperty(notes = "Matching Threshold")
	private Double matchingThreshold;
	@ApiModelProperty(notes = "Incremental Timestamp")
	private String incrementalTimestamp;
	@ApiModelProperty(notes = "Default Check")
	private String defaultCheck;
	@ApiModelProperty(notes = "Pattern Check")
	private String patternCheck;
	@ApiModelProperty(notes = "Bad Data")
	private String badData;
	@ApiModelProperty(notes = "Length Check")
	private String lengthCheck;
	@ApiModelProperty(notes = "Max Length Check")
	private String maxLengthCheck;
	@ApiModelProperty(notes = "Date Rule Check")
	private String dateRuleCheck;
	@ApiModelProperty(notes = "Dgroup Null Check")
	private String dGroupNullCheck;
	@ApiModelProperty(notes = "Dgroup Date Rule Check")
	private String dGroupDateRuleCheck;
	@ApiModelProperty(notes = "File Monitoring Type")
	private String fileMonitoringType;
	@ApiModelProperty(notes = "Dgroup Data Drift Check")
	private String dGroupDataDriftCheck;
	@ApiModelProperty(notes = "Roll Target Schema Id")
	private Long rollTargetSchemaId;
	@ApiModelProperty(notes = "Threshold Apply Option")
	private int thresholdsApplyOption;
	

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAppType() {
		return appType;
	}
	public void setAppType(String appType) {
		this.appType = appType;
	}
	public String getFileNameValidation() {
		return fileNameValidation;
	}
	public void setFileNameValidation(String fileNameValidation) {
		this.fileNameValidation = fileNameValidation;
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
	public String getTimelinessKeyCheck() {
		return timelinessKeyCheck;
	}
	public void setTimelinessKeyCheck(String timelinessKeyCheck) {
		this.timelinessKeyCheck = timelinessKeyCheck;
	}
	public String getRecordAnomalyCheck() {
		return recordAnomalyCheck;
	}
	public void setRecordAnomalyCheck(String recordAnomalyCheck) {
		this.recordAnomalyCheck = recordAnomalyCheck;
	}
	public String getNonNullCheck() {
		return nonNullCheck;
	}
	public void setNonNullCheck(String nonNullCheck) {
		this.nonNullCheck = nonNullCheck;
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
	public String getUpdateFrequency() {
		return updateFrequency;
	}
	public void setUpdateFrequency(String updateFrequency) {
		this.updateFrequency = updateFrequency;
	}
	public String getIncrementalMatching() {
		return incrementalMatching;
	}
	public void setIncrementalMatching(String incrementalMatching) {
		this.incrementalMatching = incrementalMatching;
	}
	public String getBuildHistoricFingerPrint() {
		return buildHistoricFingerPrint;
	}
	public void setBuildHistoricFingerPrint(String buildHistoricFingerPrint) {
		this.buildHistoricFingerPrint = buildHistoricFingerPrint;
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
	public String getTimeSeries() {
		return timeSeries;
	}
	public void setTimeSeries(String timeSeries) {
		this.timeSeries = timeSeries;
	}
	public String getIncrementalTimestamp() {
		return incrementalTimestamp;
	}
	public void setIncrementalTimestamp(String incrementalTimestamp) {
		this.incrementalTimestamp = incrementalTimestamp;
	}
	public String getDefaultCheck() {
		return defaultCheck;
	}
	public void setDefaultCheck(String defaultCheck) {
		this.defaultCheck = defaultCheck;
	}
	public String getPatternCheck() {
		return patternCheck;
	}
	public void setPatternCheck(String patternCheck) {
		this.patternCheck = patternCheck;
	}
	public String getBadData() {
		return badData;
	}
	public void setBadData(String badData) {
		this.badData = badData;
	}
	public String getLengthCheck() {
		return lengthCheck;
	}
	public void setLengthCheck(String lengthCheck) {
		this.lengthCheck = lengthCheck;
	}
	public String getMaxLengthCheck() {
		return maxLengthCheck;
	}
	public void setMaxLengthCheck(String maxLengthCheck) {
		this.maxLengthCheck = maxLengthCheck;
	}
	public String getDateRuleCheck() {
		return dateRuleCheck;
	}
	public void setDateRuleCheck(String dateRuleCheck) {
		this.dateRuleCheck = dateRuleCheck;
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
	public String getdGroupDataDriftCheck() {
		return dGroupDataDriftCheck;
	}
	public void setdGroupDataDriftCheck(String dGroupDataDriftCheck) {
		this.dGroupDataDriftCheck = dGroupDataDriftCheck;
	}
	public long getIdApp() {
		return idApp;
	}
	public void setIdApp(long idApp) {
		this.idApp = idApp;
	}
	public Long getIdData() {
		return idData;
	}
	public void setIdData(Long idData) {
		this.idData = idData;
	}
	public int getIdRightData() {
		return idRightData;
	}
	public void setIdRightData(int idRightData) {
		this.idRightData = idRightData;
	}
	public Long getIdLeftData() {
		return idLeftData;
	}
	public void setIdLeftData(Long idLeftData) {
		this.idLeftData = idLeftData;
	}
	public Double getRecordCountAnomalyThreshold() {
		return recordCountAnomalyThreshold;
	}
	public void setRecordCountAnomalyThreshold(Double recordCountAnomalyThreshold) {
		this.recordCountAnomalyThreshold = recordCountAnomalyThreshold;
	}
	public int getFrequencyDays() {
		return frequencyDays;
	}
	public void setFrequencyDays(int frequencyDays) {
		this.frequencyDays = frequencyDays;
	}
	public Double getGroupEqualityThreshold() {
		return groupEqualityThreshold;
	}
	public void setGroupEqualityThreshold(Double groupEqualityThreshold) {
		this.groupEqualityThreshold = groupEqualityThreshold;
	}
	public Double getMatchingThreshold() {
		return matchingThreshold;
	}
	public void setMatchingThreshold(Double matchingThreshold) {
		this.matchingThreshold = matchingThreshold;
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
}
