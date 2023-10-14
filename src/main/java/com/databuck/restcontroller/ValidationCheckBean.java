package com.databuck.restcontroller;

import io.swagger.annotations.ApiModelProperty;

public class ValidationCheckBean {
	@ApiModelProperty(notes = "App ID")
	private String idApp;
	@ApiModelProperty(notes = "Validation Check Name")
	private String validationCheckName;
	@ApiModelProperty(notes = "Validation Check Type")
	private String validationCheckType;
	@ApiModelProperty(notes = "Description")
	private String description;
	@ApiModelProperty(notes = "Data Id")
	private String idData;
	@ApiModelProperty(notes = "File Name Validation")
	private String fileNameValidation;
	@ApiModelProperty(notes = "Column Order Validation")
	private String colOrderValidation;
	@ApiModelProperty(notes = "Non Null Check")
	private String nonNullCheck;
	@ApiModelProperty(notes = "Numberical Stat Check")
	private String numericalStatCheck;
	@ApiModelProperty(notes = "String Stat Check")
	private String stringStatCheck;
	@ApiModelProperty(notes = "Record Anomaly Check")
	private String recordAnomalyCheck;
	@ApiModelProperty(notes = "Data Drift Check")
	private String dataDriftCheck;
	@ApiModelProperty(notes = "Update Frequency")
	private String updateFrequency;
	@ApiModelProperty(notes = "Time Series")
	private String timeSeries;
	@ApiModelProperty(notes = "Record Count Anomaly Check")
	private String recordCountAnomalyCheck;
	@ApiModelProperty(notes = "Record Count Anomaly Threshold")
	private String recordCountAnomalyThreshold;
	@ApiModelProperty(notes = "Key Group Record Count Anomaly Check")
	private String keyGroupRecordCountAnomalyCheck;
	@ApiModelProperty(notes = "Key Group Record Count Anomaly Threshold")
	private String keyGroupRecordCountAnomalyThreshold;
	@ApiModelProperty(notes = "Out of Norm Check")
	private String outOfNormCheck;
	@ApiModelProperty(notes = "Apply Rules")
	private String applyRules;
	@ApiModelProperty(notes = "Apply Derived Columns")
	private String applyDerivedColumns;
	@ApiModelProperty(notes = "Group Equality Check")
	private String groupEqualityCheck;
	@ApiModelProperty(notes = "Group Equality Threshold")
	private String groupEqualityThreshold;
	@ApiModelProperty(notes = "Historic Start Date")
	private String historicStartDate;
	@ApiModelProperty(notes = "Historic End Date")
	private String historicEndDate;
	@ApiModelProperty(notes = "Historic Date Format")
	private String historicDateFormat;
	@ApiModelProperty(notes = "Non Null Threshold")
	private String nonNullThreshold;
	@ApiModelProperty(notes = "Numerical Stat Threshold")
	private String numericalStatThreshold;
	@ApiModelProperty(notes = "String Stat Threshold")
	private String stringStatThreshold;
	@ApiModelProperty(notes = "Record Anomaly Threshold")
	private String recordAnomalyThreshold;
	@ApiModelProperty(notes = "Duplicate Row Id")
	private String duplicateRowId;
	@ApiModelProperty(notes = "Duplicate Row Id Threshold")
	private String duplicateRowIdThreshold;
	@ApiModelProperty(notes = "Duplicate Row All")
	private String duplicateRowAll;	
	@ApiModelProperty(notes = "Duplicate Row All Threshold")
	private String duplicateRowAllThreshold;
	@ApiModelProperty(notes = "Duplicate File Check")
	private String duplicateFileCheck;
	@ApiModelProperty(notes = "Data Drift Threshold")
	private String dataDriftThreshold;
	public String getDataDriftThreshold() {
		return dataDriftThreshold;
	}
	public void setDataDriftThreshold(String dataDriftThreshold) {
		this.dataDriftThreshold = dataDriftThreshold;
	}
	public String getDuplicateFileCheck() {
		return duplicateFileCheck;
	}
	public void setDuplicateFileCheck(String duplicateFileCheck) {
		this.duplicateFileCheck = duplicateFileCheck;
	}
	public String getRecordAnomalyThreshold() {
		return recordAnomalyThreshold;
	}
	public void setRecordAnomalyThreshold(String recordAnomalyThreshold) {
		this.recordAnomalyThreshold = recordAnomalyThreshold;
	}
	public String getDuplicateRowId() {
		return duplicateRowId;
	}
	public void setDuplicateRowId(String duplicateRowId) {
		this.duplicateRowId = duplicateRowId;
	}
	public String getDuplicateRowIdThreshold() {
		return duplicateRowIdThreshold;
	}
	public void setDuplicateRowIdThreshold(String duplicateRowIdThreshold) {
		this.duplicateRowIdThreshold = duplicateRowIdThreshold;
	}
	public String getDuplicateRowAll() {
		return duplicateRowAll;
	}
	public void setDuplicateRowAll(String duplicateRowAll) {
		this.duplicateRowAll = duplicateRowAll;
	}
	public String getDuplicateRowAllThreshold() {
		return duplicateRowAllThreshold;
	}
	public void setDuplicateRowAllThreshold(String duplicateRowAllThreshold) {
		this.duplicateRowAllThreshold = duplicateRowAllThreshold;
	}
	
	public String getValidationCheckName() {
		return validationCheckName;
	}
	public void setValidationCheckName(String validationCheckName) {
		this.validationCheckName = validationCheckName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getIdData() {
		return idData;
	}
	public void setIdData(String idData) {
		this.idData = idData;
	}
	public String getFileNameValidation() {
		return fileNameValidation;
	}
	public void setFileNameValidation(String fileNameValidation) {
		this.fileNameValidation = fileNameValidation;
	}
	public String getColOrderValidation() {
		return colOrderValidation;
	}
	public void setColOrderValidation(String colOrderValidation) {
		this.colOrderValidation = colOrderValidation;
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
	public String getDataDriftCheck() {
		return dataDriftCheck;
	}
	public void setDataDriftCheck(String dataDriftCheck) {
		this.dataDriftCheck = dataDriftCheck;
	}
	public String getUpdateFrequency() {
		return updateFrequency;
	}
	public void setUpdateFrequency(String updateFrequency) {
		this.updateFrequency = updateFrequency;
	}
	public String getTimeSeries() {
		return timeSeries;
	}
	public void setTimeSeries(String timeSeries) {
		this.timeSeries = timeSeries;
	}
	public String getRecordCountAnomalyCheck() {
		return recordCountAnomalyCheck;
	}
	public void setRecordCountAnomalyCheck(String recordCountAnomalyCheck) {
		this.recordCountAnomalyCheck = recordCountAnomalyCheck;
	}
	public String getRecordCountAnomalyThreshold() {
		return recordCountAnomalyThreshold;
	}
	public void setRecordCountAnomalyThreshold(String recordCountAnomalyThreshold) {
		this.recordCountAnomalyThreshold = recordCountAnomalyThreshold;
	}
	public String getKeyGroupRecordCountAnomalyCheck() {
		return keyGroupRecordCountAnomalyCheck;
	}
	public void setKeyGroupRecordCountAnomalyCheck(String keyGroupRecordCountAnomalyCheck) {
		this.keyGroupRecordCountAnomalyCheck = keyGroupRecordCountAnomalyCheck;
	}
	public String getKeyGroupRecordCountAnomalyThreshold() {
		return keyGroupRecordCountAnomalyThreshold;
	}
	public void setKeyGroupRecordCountAnomalyThreshold(String keyGroupRecordCountAnomalyThreshold) {
		this.keyGroupRecordCountAnomalyThreshold = keyGroupRecordCountAnomalyThreshold;
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
	public String getGroupEqualityCheck() {
		return groupEqualityCheck;
	}
	public void setGroupEqualityCheck(String groupEqualityCheck) {
		this.groupEqualityCheck = groupEqualityCheck;
	}
	public String getGroupEqualityThreshold() {
		return groupEqualityThreshold;
	}
	public void setGroupEqualityThreshold(String groupEqualityThreshold) {
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
	public String getValidationCheckType() {
		return validationCheckType;
	}
	public void setValidationCheckType(String validationCheckType) {
		this.validationCheckType = validationCheckType;
	}
	public String getNonNullThreshold() {
		return nonNullThreshold;
	}
	public void setNonNullThreshold(String nonNullThreshold) {
		this.nonNullThreshold = nonNullThreshold;
	}
	public String getNumericalStatThreshold() {
		return numericalStatThreshold;
	}
	public void setNumericalStatThreshold(String numericalStatThreshold) {
		this.numericalStatThreshold = numericalStatThreshold;
	}
	public String getStringStatThreshold() {
		return stringStatThreshold;
	}
	public void setStringStatThreshold(String stringStatThreshold) {
		this.stringStatThreshold = stringStatThreshold;
	}
	public String getIdApp() {
		return idApp;
	}
	public void setIdApp(String idApp) {
		this.idApp = idApp;
	}
	
}
