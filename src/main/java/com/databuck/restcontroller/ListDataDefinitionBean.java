package com.databuck.restcontroller;

public class ListDataDefinitionBean {
	private String columnName;
	private String columnType;
	private String primaryCheck;
	private String subsegmentCheck;
	private String lastReadTimeCheck;
	private String doNotDisplayCheck;
	private String partitionCheck;
	private String nullCheck;
	private String duplicateCheck;
	private String numericalCheck;
	private String textCheck;
	private String dataDriftCheck;
	private String recordAnomalyCheck;
	private String nullThreshold;
	private Double numFingerprintThreshold;
	private Double textFingerprintThreshold;
	private Double dataDriftThreshold;
	private Double recordAnomalyThreshold;
	private String matchValueCheck;
	private String defaultCheck;
	private String defaultValues;
	private String badData;
	private String dateFormat;
	
	/*//24_DEC_2018 (12.43pm)
	private String stringSizeCheck;
	private String stringSizeValue;
	
	public String getStringSizeCheck() {
		return stringSizeCheck;
	}

	public void setStringSizeCheck(String stringSizeCheck) {
		this.stringSizeCheck = stringSizeCheck;
	}

	public String getStringSizeValue() {
		return stringSizeValue;
	}

	public void setStringSizeValue(String stringSizeValue) {
		this.stringSizeValue = stringSizeValue;
	}
*/
	

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String getDefaultValues() {
		return defaultValues;
	}

	public void setDefaultValues(String defaultValues) {
		this.defaultValues = defaultValues;
	}

	public String getDefaultCheck() {
		return defaultCheck;
	}

	public void setDefaultCheck(String defaultCheck) {
		this.defaultCheck = defaultCheck;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getColumnType() {
		return columnType;
	}

	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}

	public String getPrimaryCheck() {
		return primaryCheck;
	}

	public void setPrimaryCheck(String primaryCheck) {
		this.primaryCheck = primaryCheck;
	}

	public String getSubsegmentCheck() {
		return subsegmentCheck;
	}

	public void setSubsegmentCheck(String subsegmentCheck) {
		this.subsegmentCheck = subsegmentCheck;
	}

	public String getLastReadTimeCheck() {
		return lastReadTimeCheck;
	}

	public void setLastReadTimeCheck(String lastReadTimeCheck) {
		this.lastReadTimeCheck = lastReadTimeCheck;
	}

	public String getDoNotDisplayCheck() {
		return doNotDisplayCheck;
	}

	public void setDoNotDisplayCheck(String doNotDisplayCheck) {
		this.doNotDisplayCheck = doNotDisplayCheck;
	}

	public String getPartitionCheck() {
		return partitionCheck;
	}

	public void setPartitionCheck(String partitionCheck) {
		this.partitionCheck = partitionCheck;
	}

	public String getNullCheck() {
		return nullCheck;
	}

	public void setNullCheck(String nullCheck) {
		this.nullCheck = nullCheck;
	}

	public String getDuplicateCheck() {
		return duplicateCheck;
	}

	public void setDuplicateCheck(String duplicateCheck) {
		this.duplicateCheck = duplicateCheck;
	}

	public String getNumericalCheck() {
		return numericalCheck;
	}

	public void setNumericalCheck(String numericalCheck) {
		this.numericalCheck = numericalCheck;
	}

	public String getTextCheck() {
		return textCheck;
	}

	public void setTextCheck(String textCheck) {
		this.textCheck = textCheck;
	}

	public String getDataDriftCheck() {
		return dataDriftCheck;
	}

	public void setDataDriftCheck(String dataDriftCheck) {
		this.dataDriftCheck = dataDriftCheck;
	}

	public String getRecordAnomalyCheck() {
		return recordAnomalyCheck;
	}

	public void setRecordAnomalyCheck(String recordAnomalyCheck) {
		this.recordAnomalyCheck = recordAnomalyCheck;
	}

	public String getNullThreshold() {
		return nullThreshold;
	}

	public void setNullThreshold(String nullThreshold) {
		this.nullThreshold = nullThreshold;
	}

	public Double getNumFingerprintThreshold() {
		return numFingerprintThreshold;
	}

	public void setNumFingerprintThreshold(Double numFingerprintThreshold) {
		this.numFingerprintThreshold = numFingerprintThreshold;
	}

	public Double getTextFingerprintThreshold() {
		return textFingerprintThreshold;
	}

	public void setTextFingerprintThreshold(Double textFingerprintThreshold) {
		this.textFingerprintThreshold = textFingerprintThreshold;
	}

	public Double getDataDriftThreshold() {
		return dataDriftThreshold;
	}

	public void setDataDriftThreshold(Double dataDriftThreshold) {
		this.dataDriftThreshold = dataDriftThreshold;
	}

	public Double getRecordAnomalyThreshold() {
		return recordAnomalyThreshold;
	}

	public void setRecordAnomalyThreshold(Double recordAnomalyThreshold) {
		this.recordAnomalyThreshold = recordAnomalyThreshold;
	}

	public String getMatchValueCheck() {
		return matchValueCheck;
	}

	public void setMatchValueCheck(String matchValueCheck) {
		this.matchValueCheck = matchValueCheck;
	}

	public String getBadData() {
		return badData;
	}

	public void setbadData(String badData) {
		this.badData = badData;
	}
}
