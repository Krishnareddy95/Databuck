package com.databuck.dto;

import io.swagger.annotations.ApiModelProperty;

public class GetMetadataResponseDto {

	@ApiModelProperty(notes = "Id")
	private long idData;
	@ApiModelProperty(notes = "Column Id")
	private long IdColumn;
	@ApiModelProperty(notes = "Column Name")
	private String columnName;
	@ApiModelProperty(notes = "Display Name")
	private String displayName;
	@ApiModelProperty(notes = "Primary Key")
	private String primaryKey;
	@ApiModelProperty(notes = "Non Null")
	private String NonNull;
	@ApiModelProperty(notes = "KBE")
	private String KBE;
	@ApiModelProperty(notes = "Dgroup")
	private String dgroup;
	@ApiModelProperty(notes = "Null Count Threshold")
	private Double nullCountThreshold;
	@ApiModelProperty(notes = "Hash Value")
	private String hashValue;
	@ApiModelProperty(notes = "Numerical Stat")
	private String numericalStat;
	@ApiModelProperty(notes = "Numerical Threshold")
	private Double numericalThreshold;
	@ApiModelProperty(notes = "String Stat")
	private String stringStat;
	@ApiModelProperty(notes = "String Stat Threshold")
	private Double stringStatThreshold;
	@ApiModelProperty(notes = "Duplicate Key")
	private String dupkey;
	@ApiModelProperty(notes = "Measurement")
	private String measurement;
	@ApiModelProperty(notes = "Incremental Column")
	private String incrementalCol;
	@ApiModelProperty(notes = "Record Anomaly")
	private String recordAnomaly;
	@ApiModelProperty(notes = "Record Anomaly Threshold")
	private Double recordAnomalyThreshold;
	@ApiModelProperty(notes = "Start Date")
	private String startDate;
	@ApiModelProperty(notes = "End Date")
	private String endDate;
	@ApiModelProperty(notes = "Timeliness Key")
	private String timelinessKey;
	@ApiModelProperty(notes = "Default Check")
	private String defaultCheck;
	@ApiModelProperty(notes = "Default Values")
	private String defaultValues;
	@ApiModelProperty(notes = "Blend")
	private String blend;
	@ApiModelProperty(notes = "Data Drift")
	private String dataDrift;
	@ApiModelProperty(notes = "Data Drift Threshold")
	private Double dataDriftThreshold;
	@ApiModelProperty(notes = "Out of Norm Stat")
	private String outOfNormStat;
	@ApiModelProperty(notes = "Out of Norm Stat Threshold")
	private Double outOfNormStatThreshold;
	@ApiModelProperty(notes = "is Masked")
	private String isMasked;
	@ApiModelProperty(notes = "Partition By")
	private String partitionBy;
	@ApiModelProperty(notes = "Patterns")
	private String patterns;
	@ApiModelProperty(notes = "Pattern Check")
	private String patternCheck;
	@ApiModelProperty(notes = "Pattern Check Threshold")
	private Double patternCheckThreshold;
	@ApiModelProperty(notes = "Default Pattern Check")
	private String defaultPatternCheck;
	@ApiModelProperty(notes = "Date Rule")
	private String dateRule;
	@ApiModelProperty(notes = "Date Format")
	private String dateFormat;
	@ApiModelProperty(notes = "Length Check")
	private String lengthCheck;
	@ApiModelProperty(notes = "Max Length Check")
	private String maxLengthCheck;
	@ApiModelProperty(notes = "Length Value")
	private String lengthValue;
	@ApiModelProperty(notes = "Length Threshold")
	private Double lengthThreshold;
	@ApiModelProperty(notes = "Bad Data")
	private String badData;
	@ApiModelProperty(notes = "Bad Data Threshold")
	private Double badDataThreshold;
	@ApiModelProperty(notes = "Format")
	private String format;
	
	public long getIdData() {
		return idData;
	}
	public void setIdData(long idData) {
		this.idData = idData;
	}
	public long getIdColumn() {
		return IdColumn;
	}
	public void setIdColumn(long idColumn) {
		IdColumn = idColumn;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getPrimaryKey() {
		return primaryKey;
	}
	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}
	public String getNonNull() {
		return NonNull;
	}
	public void setNonNull(String nonNull) {
		NonNull = nonNull;
	}
	public String getKBE() {
		return KBE;
	}
	public void setKBE(String kBE) {
		KBE = kBE;
	}
	public String getDgroup() {
		return dgroup;
	}
	public void setDgroup(String dgroup) {
		this.dgroup = dgroup;
	}
	public Double getNullCountThreshold() {
		return nullCountThreshold;
	}
	public void setNullCountThreshold(Double nullCountThreshold) {
		this.nullCountThreshold = nullCountThreshold;
	}
	public String getHashValue() {
		return hashValue;
	}
	public void setHashValue(String hashValue) {
		this.hashValue = hashValue;
	}
	public String getNumericalStat() {
		return numericalStat;
	}
	public void setNumericalStat(String numericalStat) {
		this.numericalStat = numericalStat;
	}
	public Double getNumericalThreshold() {
		return numericalThreshold;
	}
	public void setNumericalThreshold(Double numericalThreshold) {
		this.numericalThreshold = numericalThreshold;
	}
	public String getStringStat() {
		return stringStat;
	}
	public void setStringStat(String stringStat) {
		this.stringStat = stringStat;
	}
	public Double getStringStatThreshold() {
		return stringStatThreshold;
	}
	public void setStringStatThreshold(Double stringStatThreshold) {
		this.stringStatThreshold = stringStatThreshold;
	}
	public String getDupkey() {
		return dupkey;
	}
	public void setDupkey(String dupkey) {
		this.dupkey = dupkey;
	}
	public String getMeasurement() {
		return measurement;
	}
	public void setMeasurement(String measurement) {
		this.measurement = measurement;
	}
	public String getIncrementalCol() {
		return incrementalCol;
	}
	public void setIncrementalCol(String incrementalCol) {
		this.incrementalCol = incrementalCol;
	}
	public String getRecordAnomaly() {
		return recordAnomaly;
	}
	public void setRecordAnomaly(String recordAnomaly) {
		this.recordAnomaly = recordAnomaly;
	}
	public Double getRecordAnomalyThreshold() {
		return recordAnomalyThreshold;
	}
	public void setRecordAnomalyThreshold(Double recordAnomalyThreshold) {
		this.recordAnomalyThreshold = recordAnomalyThreshold;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getTimelinessKey() {
		return timelinessKey;
	}
	public void setTimelinessKey(String timelinessKey) {
		this.timelinessKey = timelinessKey;
	}
	public String getDefaultCheck() {
		return defaultCheck;
	}
	public void setDefaultCheck(String defaultCheck) {
		this.defaultCheck = defaultCheck;
	}
	public String getDefaultValues() {
		return defaultValues;
	}
	public void setDefaultValues(String defaultValues) {
		this.defaultValues = defaultValues;
	}
	public String getBlend() {
		return blend;
	}
	public void setBlend(String blend) {
		this.blend = blend;
	}
	public String getDataDrift() {
		return dataDrift;
	}
	public void setDataDrift(String dataDrift) {
		this.dataDrift = dataDrift;
	}
	public Double getDataDriftThreshold() {
		return dataDriftThreshold;
	}
	public void setDataDriftThreshold(Double dataDriftThreshold) {
		this.dataDriftThreshold = dataDriftThreshold;
	}
	public String getOutOfNormStat() {
		return outOfNormStat;
	}
	public void setOutOfNormStat(String outOfNormStat) {
		this.outOfNormStat = outOfNormStat;
	}
	public Double getOutOfNormStatThreshold() {
		return outOfNormStatThreshold;
	}
	public void setOutOfNormStatThreshold(Double outOfNormStatThreshold) {
		this.outOfNormStatThreshold = outOfNormStatThreshold;
	}
	public String getIsMasked() {
		return isMasked;
	}
	public void setIsMasked(String isMasked) {
		this.isMasked = isMasked;
	}
	public String getPartitionBy() {
		return partitionBy;
	}
	public void setPartitionBy(String partitionBy) {
		this.partitionBy = partitionBy;
	}
	public String getPatterns() {
		return patterns;
	}
	public void setPatterns(String patterns) {
		this.patterns = patterns;
	}
	public String getPatternCheck() {
		return patternCheck;
	}
	public void setPatternCheck(String patternCheck) {
		this.patternCheck = patternCheck;
	}
	public Double getPatternCheckThreshold() {
		return patternCheckThreshold;
	}
	public void setPatternCheckThreshold(Double patternCheckThreshold) {
		this.patternCheckThreshold = patternCheckThreshold;
	}
	public String getDateRule() {
		return dateRule;
	}
	public void setDateRule(String dateRule) {
		this.dateRule = dateRule;
	}
	public String getDateFormat() {
		return dateFormat;
	}
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
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
	public String getLengthValue() {
		return lengthValue;
	}
	public void setLengthValue(String lengthValue) {
		this.lengthValue = lengthValue;
	}
	public Double getLengthThreshold() {
		return lengthThreshold;
	}
	public void setLengthThreshold(Double lengthThreshold) {
		this.lengthThreshold = lengthThreshold;
	}
	public String getBadData() {
		return badData;
	}
	public void setBadData(String badData) {
		this.badData = badData;
	}
	public Double getBadDataThreshold() {
		return badDataThreshold;
	}
	public void setBadDataThreshold(Double badDataThreshold) {
		this.badDataThreshold = badDataThreshold;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getDefaultPatternCheck() {
		return defaultPatternCheck;
	}
	public void setDefaultPatternCheck(String defaultPatternCheck) {
		this.defaultPatternCheck = defaultPatternCheck;
	}
	
}
