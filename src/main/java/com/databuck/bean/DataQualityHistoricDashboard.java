package com.databuck.bean;

import java.util.Date;

import net.sourceforge.jtds.jdbc.DateTime;


public class DataQualityHistoricDashboard {
	private Long id;
	private Long idApp;
	private String validationCheckName;
	private Date date;
	private Long run;
	private DateTime createdAt;
	private Double aggregateDQI;
	private String fileContentValidationStatus;
	private String columnOrderValidationStatus;
	private Double absoluteRCDQI;
	private String absoluteRCStatus;
	private Long absoluteRCRecordCount;
	private Long absoluteRCAverageRecordCount;
	private Double aggregateRCDQI;
	private String aggregateRCStatus;
	private Long aggregateRCRecordCount;
	private Double nullCountDQI;
	private String nullCountStatus;
	private Long nullCountColumns;
	private Long nullCountColumnsFailed;
	private Double primaryKeyDQI;
	private String primaryKeyStatus;
	private Long primaryKeyDuplicates;
	private Double userSelectedDQI;
	private String userSelectedStatus;
	private Long userSelectedDuplicates;
	private Double numericalDQI;
	private String numericalStatus;
	private Long numericalColumns;
	private Long numericalRecordsFailed;
	private Double stringDQI;
	private String stringStatus;
	private Long stringColumns;
	private Long stringRecordsFailed;
	private Double recordAnomalyDQI;
	private String recordAnomalyStatus;
	private Long recordAnomalyRecords;
	private Long recordAnomalyRecordsFailed;
	private String ruleType;
	private Double ruleDQI;
	private Double dataDriftDQI;
	private String dataDriftStatus;
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getIdApp() {
		return idApp;
	}

	public void setIdApp(Long idApp) {
		this.idApp = idApp;
	}

	public String getValidationCheckName() {
		return validationCheckName;
	}

	public void setValidationCheckName(String validationCheckName) {
		this.validationCheckName = validationCheckName;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	

	public DateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(DateTime createdAt) {
		this.createdAt = createdAt;
	}
	
	public Long getRun() {
		return run;
	}

	public void setRun(Long run) {
		this.run = run;
	}

	public Double getAggregateDQI() {
		return aggregateDQI;
	}

	public void setAggregateDQI(Double aggregateDQI) {
		this.aggregateDQI = aggregateDQI;
	}

	public String getFileContentValidationStatus() {
		return fileContentValidationStatus;
	}

	public void setFileContentValidationStatus(String fileContentValidationStatus) {
		this.fileContentValidationStatus = fileContentValidationStatus;
	}

	public String getColumnOrderValidationStatus() {
		return columnOrderValidationStatus;
	}

	public void setColumnOrderValidationStatus(String columnOrderValidationStatus) {
		this.columnOrderValidationStatus = columnOrderValidationStatus;
	}

	public Double getAbsoluteRCDQI() {
		return absoluteRCDQI;
	}

	public void setAbsoluteRCDQI(Double absoluteRCDQI) {
		this.absoluteRCDQI = absoluteRCDQI;
	}

	public String getAbsoluteRCStatus() {
		return absoluteRCStatus;
	}

	public void setAbsoluteRCStatus(String absoluteRCStatus) {
		this.absoluteRCStatus = absoluteRCStatus;
	}

	public Long getAbsoluteRCRecordCount() {
		return absoluteRCRecordCount;
	}

	public void setAbsoluteRCRecordCount(Long absoluteRCRecordCount) {
		this.absoluteRCRecordCount = absoluteRCRecordCount;
	}

	public Long getAbsoluteRCAverageRecordCount() {
		return absoluteRCAverageRecordCount;
	}

	public void setAbsoluteRCAverageRecordCount(Long absoluteRCAverageRecordCount) {
		this.absoluteRCAverageRecordCount = absoluteRCAverageRecordCount;
	}

	public Double getAggregateRCDQI() {
		return aggregateRCDQI;
	}

	public void setAggregateRCDQI(Double aggregateRCDQI) {
		this.aggregateRCDQI = aggregateRCDQI;
	}

	public String getAggregateRCStatus() {
		return aggregateRCStatus;
	}

	public void setAggregateRCStatus(String aggregateRCStatus) {
		this.aggregateRCStatus = aggregateRCStatus;
	}

	public Long getAggregateRCRecordCount() {
		return aggregateRCRecordCount;
	}

	public void setAggregateRCRecordCount(Long aggregateRCRecordCount) {
		this.aggregateRCRecordCount = aggregateRCRecordCount;
	}

	public Double getNullCountDQI() {
		return nullCountDQI;
	}

	public void setNullCountDQI(Double nullCountDQI) {
		this.nullCountDQI = nullCountDQI;
	}

	public String getNullCountStatus() {
		return nullCountStatus;
	}

	public void setNullCountStatus(String nullCountStatus) {
		this.nullCountStatus = nullCountStatus;
	}

	public Long getNullCountColumns() {
		return nullCountColumns;
	}

	public void setNullCountColumns(Long nullCountColumns) {
		this.nullCountColumns = nullCountColumns;
	}

	public Long getNullCountColumnsFailed() {
		return nullCountColumnsFailed;
	}

	public void setNullCountColumnsFailed(Long nullCountColumnsFailed) {
		this.nullCountColumnsFailed = nullCountColumnsFailed;
	}

	public Double getPrimaryKeyDQI() {
		return primaryKeyDQI;
	}

	public void setPrimaryKeyDQI(Double primaryKeyDQI) {
		this.primaryKeyDQI = primaryKeyDQI;
	}

	public String getPrimaryKeyStatus() {
		return primaryKeyStatus;
	}

	public void setPrimaryKeyStatus(String primaryKeyStatus) {
		this.primaryKeyStatus = primaryKeyStatus;
	}

	public Long getPrimaryKeyDuplicates() {
		return primaryKeyDuplicates;
	}

	public void setPrimaryKeyDuplicates(Long primaryKeyDuplicates) {
		this.primaryKeyDuplicates = primaryKeyDuplicates;
	}

	public Double getUserSelectedDQI() {
		return userSelectedDQI;
	}

	public void setUserSelectedDQI(Double userSelectedDQI) {
		this.userSelectedDQI = userSelectedDQI;
	}

	public String getUserSelectedStatus() {
		return userSelectedStatus;
	}

	public void setUserSelectedStatus(String userSelectedStatus) {
		this.userSelectedStatus = userSelectedStatus;
	}

	public Long getUserSelectedDuplicates() {
		return userSelectedDuplicates;
	}

	public void setUserSelectedDuplicates(Long userSelectedDuplicates) {
		this.userSelectedDuplicates = userSelectedDuplicates;
	}

	public Double getNumericalDQI() {
		return numericalDQI;
	}

	public void setNumericalDQI(Double numericalDQI) {
		this.numericalDQI = numericalDQI;
	}

	public String getNumericalStatus() {
		return numericalStatus;
	}

	public void setNumericalStatus(String numericalStatus) {
		this.numericalStatus = numericalStatus;
	}

	public Long getNumericalColumns() {
		return numericalColumns;
	}

	public void setNumericalColumns(Long numericalColumns) {
		this.numericalColumns = numericalColumns;
	}

	public Long getNumericalRecordsFailed() {
		return numericalRecordsFailed;
	}

	public void setNumericalRecordsFailed(Long numericalRecordsFailed) {
		this.numericalRecordsFailed = numericalRecordsFailed;
	}

	public Double getStringDQI() {
		return stringDQI;
	}

	public void setStringDQI(Double stringDQI) {
		this.stringDQI = stringDQI;
	}

	public String getStringStatus() {
		return stringStatus;
	}

	public void setStringStatus(String stringStatus) {
		this.stringStatus = stringStatus;
	}

	public Long getStringColumns() {
		return stringColumns;
	}

	public void setStringColumns(Long stringColumns) {
		this.stringColumns = stringColumns;
	}

	public Long getStringRecordsFailed() {
		return stringRecordsFailed;
	}

	public void setStringRecordsFailed(Long stringRecordsFailed) {
		this.stringRecordsFailed = stringRecordsFailed;
	}

	public Double getRecordAnomalyDQI() {
		return recordAnomalyDQI;
	}

	public void setRecordAnomalyDQI(Double recordAnomalyDQI) {
		this.recordAnomalyDQI = recordAnomalyDQI;
	}

	public String getRecordAnomalyStatus() {
		return recordAnomalyStatus;
	}

	public void setRecordAnomalyStatus(String recordAnomalyStatus) {
		this.recordAnomalyStatus = recordAnomalyStatus;
	}

	public Long getRecordAnomalyRecords() {
		return recordAnomalyRecords;
	}

	public void setRecordAnomalyRecords(Long recordAnomalyRecords) {
		this.recordAnomalyRecords = recordAnomalyRecords;
	}

	public Long getRecordAnomalyRecordsFailed() {
		return recordAnomalyRecordsFailed;
	}

	public void setRecordAnomalyRecordsFailed(Long recordAnomalyRecordsFailed) {
		this.recordAnomalyRecordsFailed = recordAnomalyRecordsFailed;
	}

	public String getRuleType() {
		return ruleType;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}

	public Double getRuleDQI() {
		return ruleDQI;
	}

	public void setRuleDQI(Double ruleDQI) {
		this.ruleDQI = ruleDQI;
	}

	public Double getDataDriftDQI() {
		return dataDriftDQI;
	}

	public void setDataDriftDQI(Double dataDriftDQI) {
		this.dataDriftDQI = dataDriftDQI;
	}

	public String getDataDriftStatus() {
		return dataDriftStatus;
	}

	public void setDataDriftStatus(String dataDriftStatus) {
		this.dataDriftStatus = dataDriftStatus;
	}
}
