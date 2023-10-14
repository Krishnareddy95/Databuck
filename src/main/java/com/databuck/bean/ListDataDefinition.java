package com.databuck.bean;

import java.io.Serializable;
import java.util.StringJoiner;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ListDataDefinition implements Serializable {
	private String itemid;
	private long idColumn;
	private long idData;
	private String columnName;
	private String displayName;
	private String format;
	private String hashValue;
	private String numericalStat;
	private String stringStat;
	@JsonProperty("KBE")
	private String KBE;
	private String dgroup;
	private String dupkey;
	private String measurement;
	private String blend;
	private int idCol;
	private Double numericalThreshold;
	private Double stringStatThreshold;
	private Double lengthCheckThreshold; // Added for DC-148
	private Double nullCountThreshold;
	private long idDataSchema;
	private String incrementalCol;
	private String primaryKey;
	private String nonNull;
	private String recordAnomaly;
	private String startDate;
	private String endDate;
	private String timelinessKey;
	private String defaultCheck;
	private String defaultValues;
	private Double recordAnomalyThreshold;
	private String dataDrift;
	private Double dataDriftThreshold;
	private String outOfNormStat;
	private Double outOfNormStatThreshold;
	private String isMasked;
	private String partitionBy;
	private String patternCheck;
	private String patterns;
	private String dateRule;
	private String badData;
	private String dateFormat;
	private String correlationcolumn;
	private Double lengthThreshold;
	private Double badDataThreshold;
	private Double patternCheckThreshold;
    private String defaultPatternCheck;
    private String defaultPatterns;
		
	public String getCorrelationcolumn() {
		return correlationcolumn;
	}

	public void setCorrelationcolumn(String correlationcolumn) {
		this.correlationcolumn = correlationcolumn;
	}

	public String getApplyrule() {
		return applyrule;
	}

	public void setApplyRule(String applyrule) {
		this.applyrule = applyrule;
	}
	private String applyrule;
	
	
	//24_DEC_2018 (12.43pm)
		private String lengthCheck;
		private String lengthValue;
		
		// Max Length Check
		private String maxLengthCheck;
		
		
		public String getMaxLengthCheck() {
			return maxLengthCheck;
		}

		public void setMaxLengthCheck(String maxLengthCheck) {
			this.maxLengthCheck = maxLengthCheck;
		}

		public String getLengthCheck() {
			return lengthCheck;
		}

		public void setLengthCheck(String lengthCheck) {
			this.lengthCheck = lengthCheck;
		}

		public String getLengthValue() {
			return lengthValue;
		}

		public void setLengthValue(String lengthValue) {
			this.lengthValue = lengthValue;
		}

	
	public String getDateFormat() {
		return dateFormat;
	}
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}
	
	public String getItemid() {
		return itemid;
	}
	public void setItemid(String itemid) {
		this.itemid = itemid;
	}
	public long getIdColumn() {
		return idColumn;
	}
	public void setIdColumn(long idColumn) {
		this.idColumn = idColumn;
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
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
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
	public String getStringStat() {
		return stringStat;
	}
	public void setStringStat(String stringStat) {
		this.stringStat = stringStat;
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
	public String getBlend() {
		return blend;
	}
	public void setBlend(String blend) {
		this.blend = blend;
	}
	public int getIdCol() {
		return idCol;
	}
	public void setIdCol(int idCol) {
		this.idCol = idCol;
	}
	public Double getNumericalThreshold() {
		return numericalThreshold;
	}
	public void setNumericalThreshold(Double numericalThreshold) {
		this.numericalThreshold = numericalThreshold;
	}
	public Double getLengthCheckThreshold() {
		return lengthCheckThreshold;
	}// Added for DC-148
	public void setLengthCheckThreshold(Double lengthCheckThreshold) {
		this.lengthCheckThreshold = lengthCheckThreshold;
	}
	public Double getStringStatThreshold() {
		return stringStatThreshold;
	}
	public void setStringStatThreshold(Double stringStatThreshold) {
		this.stringStatThreshold = stringStatThreshold;
	}
	public Double getNullCountThreshold() {
		return nullCountThreshold;
	}
	public void setNullCountThreshold(Double nullCountThreshold) {
		this.nullCountThreshold = nullCountThreshold;
	}
	public long getIdDataSchema() {
		return idDataSchema;
	}
	public void setIdDataSchema(long idDataSchema) {
		this.idDataSchema = idDataSchema;
	}
	public String getPatternCheck() {
		return patternCheck;
	}
	public void setPatternCheck(String patternCheck) {   
		this.patternCheck = patternCheck;
	}
	public String getPatterns() {
		return patterns;
	}
	public void setPatterns(String patterns) {
		this.patterns = patterns;
	}
	
	public String getDateRule() {
		return dateRule;
	}
	public void setDateRule(String dateRule) {
		this.dateRule = dateRule;
	}
	
	public String getIncrementalCol() {
		return incrementalCol;
	}
	public void setIncrementalCol(String incrementalCol) {
		this.incrementalCol = incrementalCol;
	}
	public String getPrimaryKey() {
		return primaryKey;
	}
	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}
	public String getNonNull() {
		return nonNull;
	}
	public void setNonNull(String nonNull) {
		this.nonNull = nonNull;
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
	
	public void setRecordAnomalyThreshold(Double recordAnomalyThreshold) {
		this.recordAnomalyThreshold = recordAnomalyThreshold;
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
	public long getIdData() {
		return idData;
	}
	public void setIdData(long idData) {
		this.idData = idData;
	}
	public String getBadData() {
		return badData;
	}
	public void setbadData(String badData) {
		this.badData = badData;
	}

	public Double getLengthThreshold() {
		return lengthThreshold;
	}

	public void setLengthThreshold(Double lengthThreshold) {
		this.lengthThreshold = lengthThreshold;
	}

	public Double getBadDataThreshold() {
		return badDataThreshold;
	}

	public void setBadDataThreshold(Double badDataThreshold) {
		this.badDataThreshold = badDataThreshold;
	}

	public Double getPatternCheckThreshold() {
		return patternCheckThreshold;
	}

	public void setPatternCheckThreshold(Double patternCheckThreshold) {
		this.patternCheckThreshold = patternCheckThreshold;
	}
	
	public String getDefaultPatternCheck() {
		return defaultPatternCheck;
	}

	public void setDefaultPatternCheck(String defaultPatternCheck) {
		this.defaultPatternCheck = defaultPatternCheck;
	}

	public String getDefaultPatterns() {
		return defaultPatterns;
	}

	public void setDefaultPatterns(String defaultPatterns) {
		this.defaultPatterns = defaultPatterns;
	}
	
	
	@Override
	public String toString() {
		return "{\"itemid\":\"" + itemid + "\", \"idColumn\":\"" + idColumn + "\", \"idData\":\"" + idData
				+ "\", \"columnName\":\"" + columnName + "\", \"displayName\":\"" + displayName + "\", \"format\":\""
				+ format + "\", \"hashValue\":\"" + hashValue + "\", \"numericalStat\":\"" + numericalStat
				+ "\", \"stringStat\":\"" + stringStat + "\", \"KBE\":\"" + KBE + "\", \"dgroup\":\"" + dgroup
				+ "\", \"dupkey\":\"" + dupkey + "\", \"measurement\":\"" + measurement + "\", \"blend\":\"" + blend
				+ "\", \"idCol\":\"" + idCol + "\", \"numericalThreshold\":\"" + numericalThreshold
				+ "\", \"stringStatThreshold\":\"" + stringStatThreshold + "\", \"nullCountThreshold\":\""
				+ nullCountThreshold + "\", \"idDataSchema\":\"" + idDataSchema + "\", \"incrementalCol\":\""
				+ incrementalCol + "\", \"primaryKey\":\"" + primaryKey + "\", \"nonNull\":\"" + nonNull
				+ "\", \"recordAnomaly\":\"" + recordAnomaly + "\", \"startDate\":\"" + startDate + "\", \"endDate\":\""
				+ endDate + "\", \"timelinessKey\":\"" + timelinessKey + "\", \"defaultCheck\":\"" + defaultCheck
				+ "\", \"defaultValues\":\"" + defaultValues + "\", \"recordAnomalyThreshold\":\""
				+ recordAnomalyThreshold + "\", \"dataDrift\":\"" + dataDrift + "\", \"dataDriftThreshold\":\""
				+ dataDriftThreshold + "\", \"outOfNormStat\":\"" + outOfNormStat + "\", \"outOfNormStatThreshold\":\""
				+ outOfNormStatThreshold + "\", \"isMasked\":\"" + isMasked + "\", \"partitionBy\":\"" + partitionBy
				+ "\", \"patternCheck\":\"" + patternCheck + "\", \"patterns\":\"" + patterns + "\", \"dateRule\":\""
				+ dateRule + "\", \"badData\":\"" + badData + "\", \"dateFormat\":\"" + dateFormat
				+ "\", \"correlationcolumn\":\"" + correlationcolumn + "\", \"lengthThreshold\":\"" + lengthThreshold
				+ "\", \"badDataThreshold\":\"" + badDataThreshold + "\", \"patternCheckThreshold\":\""
				+ patternCheckThreshold + "\", \"applyrule\":\"" + applyrule + "\", \"lengthCheck\":\"" + lengthCheck
				+ "\", \"lengthValue\":\"" + lengthValue + "\", \"maxLengthCheck\":\"" + maxLengthCheck + "\""
				+ ", \"defaultPatternCheck\":\"" + defaultPatternCheck + "\", \"defaultPatterns\":\"" + defaultPatterns + "\" }";
	}
	

}