package com.databuck.bean;

public class ColumnProfile_DP {

	private long idData;
	private String execDate;
	private Long run;
	private String table_or_fileName;
	private String columnName;
	private String dataType;
	private Long totalRecordCount;
	private Long missingValue;
	private Double percentageMissing;
	private Long uniqueCount;
	private Long minLength;
	private Long maxLength;
	private String mean;
	private String stdDev;
	private String min;
	private String max;
	private String percentile_99;
	private String percentile_75;
	private String percentile_25;
	private String percentile_1;
	private String projectName;
	private String defaultPatterns;

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public Long getTotalRecordCount() {
		return totalRecordCount;
	}

	public void setTotalRecordCount(Long totalRecordCount) {
		this.totalRecordCount = totalRecordCount;
	}

	public Long getMissingValue() {
		return missingValue;
	}

	public void setMissingValue(Long missingValue) {
		this.missingValue = missingValue;
	}

	public Double getPercentageMissing() {
		return percentageMissing;
	}

	public void setPercentageMissing(Double percentageMissing) {
		this.percentageMissing = percentageMissing;
	}

	public Long getUniqueCount() {
		return uniqueCount;
	}

	public void setUniqueCount(Long uniqueCount) {
		this.uniqueCount = uniqueCount;
	}

	public Long getMinLength() {
		return minLength;
	}

	public void setMinLength(Long minLength) {
		this.minLength = minLength;
	}

	public Long getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(Long maxLength) {
		this.maxLength = maxLength;
	}

	public String getMean() {
		return mean;
	}

	public void setMean(String mean) {
		this.mean = mean;
	}

	public String getStdDev() {
		return stdDev;
	}

	public void setStdDev(String stdDev) {
		this.stdDev = stdDev;
	}

	public String getMin() {
		return min;
	}

	public void setMin(String min) {
		this.min = min;
	}

	public String getMax() {
		return max;
	}

	public void setMax(String max) {
		this.max = max;
	}

	public String getPercentile_99() {
		return percentile_99;
	}

	public void setPercentile_99(String percentile_99) {
		this.percentile_99 = percentile_99;
	}

	public String getPercentile_75() {
		return percentile_75;
	}

	public void setPercentile_75(String percentile_75) {
		this.percentile_75 = percentile_75;
	}

	public String getPercentile_25() {
		return percentile_25;
	}

	public void setPercentile_25(String percentile_25) {
		this.percentile_25 = percentile_25;
	}

	public String getPercentile_1() {
		return percentile_1;
	}

	public void setPercentile_1(String percentile_1) {
		this.percentile_1 = percentile_1;
	}

	public String getExecDate() {
		return execDate;
	}

	public void setExecDate(String execDate) {
		this.execDate = execDate;
	}

	public Long getRun() {
		return run;
	}

	public void setRun(Long run) {
		this.run = run;
	}

	public long getIdData() {
		return idData;
	}

	public void setIdData(long idData) {
		this.idData = idData;
	}

	public String getTable_or_fileName() {
		return table_or_fileName;
	}

	public void setTable_or_fileName(String table_or_fileName) {
		this.table_or_fileName = table_or_fileName;
	}
	
	public String getDefaultPatterns() {
		return defaultPatterns;
	}

	public void setDefaultPatterns(String defaultPatterns) {
		this.defaultPatterns = defaultPatterns;
	}

	@Override
	public String toString() {
		return "" + idData + "," + table_or_fileName + "," + columnName + "," + dataType + "," + totalRecordCount + ","
				+ missingValue + "," + percentageMissing + "," + uniqueCount + "," + minLength + "," + maxLength + ","
				+ mean + "," + stdDev + "," + min + "," + max + "," + percentile_99 + "," + percentile_75 + ","
				+ percentile_25 + "," + percentile_1+ ","+ defaultPatterns;
	}

}
