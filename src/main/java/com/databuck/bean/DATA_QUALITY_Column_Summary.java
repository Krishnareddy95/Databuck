package com.databuck.bean;

public class DATA_QUALITY_Column_Summary {
	private String date;
	private int run;
	private String dataDriftStatus;
	private long dayOfYear;
	private String month;
	private long dayOfMonth;
	private String dayOfWeek;
	private long hourOfDay;
	private String colName;
	private int count;
	private Double min;
	private Double max;
	private int cardinality;
	private Double std_Dev;
	private Double mean;
	
	private int null_Value;
	private int record_Count;
	private Double null_Percentage;
	private Double null_Threshold;
	private String status;
	private Double stringCardinalityAvg;
	private Double stringCardinalityStdDev;
	private Double strCardinalityDeviation;
	private Double string_Threshold;
	private String string_Status;
	
	private Double numMeanAvg;
	private Double numMeanStdDev;
	private Double numMeanDeviation;
	private Double numMeanThreshold;
	private String numMeanStatus;
	private Double numSDAvg;
	private Double numSDStdDev;
	private Double numSDDeviation;
	private Double numSDThreshold;
	private String numSDStatus;
	private String outOfNormStatStatus; 
	private String dGroupVal;
	private String sumOfNumStat;
	private String dGroupCol;
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public int getRun() {
		return run;
	}
	public void setRun(int run) {
		this.run = run;
	}
	public String getDataDriftStatus() {
		return dataDriftStatus;
	}
	public void setDataDriftStatus(String dataDriftStatus) {
		this.dataDriftStatus = dataDriftStatus;
	}
	public long getDayOfYear() {
		return dayOfYear;
	}
	public void setDayOfYear(long dayOfYear) {
		this.dayOfYear = dayOfYear;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public long getDayOfMonth() {
		return dayOfMonth;
	}
	public void setDayOfMonth(long dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}
	public String getDayOfWeek() {
		return dayOfWeek;
	}
	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
	public long getHourOfDay() {
		return hourOfDay;
	}
	public void setHourOfDay(long hourOfDay) {
		this.hourOfDay = hourOfDay;
	}
	public String getColName() {
		return colName;
	}
	public void setColName(String colName) {
		this.colName = colName;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public Double getMin() {
		return min;
	}
	public void setMin(Double min) {
		this.min = min;
	}
	public Double getMax() {
		return max;
	}
	public void setMax(Double max) {
		this.max = max;
	}
	public int getCardinality() {
		return cardinality;
	}
	public void setCardinality(int cardinality) {
		this.cardinality = cardinality;
	}
	public Double getStd_Dev() {
		return std_Dev;
	}
	public void setStd_Dev(Double std_Dev) {
		this.std_Dev = std_Dev;
	}
	public Double getMean() {
		return mean;
	}
	public void setMean(Double mean) {
		this.mean = mean;
	}
	public int getNull_Value() {
		return null_Value;
	}
	public void setNull_Value(int null_Value) {
		this.null_Value = null_Value;
	}
	public int getRecord_Count() {
		return record_Count;
	}
	public void setRecord_Count(int record_Count) {
		this.record_Count = record_Count;
	}
	public Double getNull_Percentage() {
		return null_Percentage;
	}
	public void setNull_Percentage(Double null_Percentage) {
		this.null_Percentage = null_Percentage;
	}
	public Double getNull_Threshold() {
		return null_Threshold;
	}
	public void setNull_Threshold(Double null_Threshold) {
		this.null_Threshold = null_Threshold;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Double getStringCardinalityAvg() {
		return stringCardinalityAvg;
	}
	public void setStringCardinalityAvg(Double stringCardinalityAvg) {
		this.stringCardinalityAvg = stringCardinalityAvg;
	}
	public Double getStringCardinalityStdDev() {
		return stringCardinalityStdDev;
	}
	public void setStringCardinalityStdDev(Double stringCardinalityStdDev) {
		this.stringCardinalityStdDev = stringCardinalityStdDev;
	}
	public Double getStrCardinalityDeviation() {
		return strCardinalityDeviation;
	}
	public void setStrCardinalityDeviation(Double strCardinalityDeviation) {
		this.strCardinalityDeviation = strCardinalityDeviation;
	}
	public Double getString_Threshold() {
		return string_Threshold;
	}
	public void setString_Threshold(Double string_Threshold) {
		this.string_Threshold = string_Threshold;
	}
	public String getString_Status() {
		return string_Status;
	}
	public void setString_Status(String string_Status) {
		this.string_Status = string_Status;
	}
	public Double getNumMeanAvg() {
		return numMeanAvg;
	}
	public void setNumMeanAvg(Double numMeanAvg) {
		this.numMeanAvg = numMeanAvg;
	}
	public Double getNumMeanStdDev() {
		return numMeanStdDev;
	}
	public void setNumMeanStdDev(Double numMeanStdDev) {
		this.numMeanStdDev = numMeanStdDev;
	}
	public Double getNumMeanDeviation() {
		return numMeanDeviation;
	}
	public void setNumMeanDeviation(Double numMeanDeviation) {
		this.numMeanDeviation = numMeanDeviation;
	}
	public Double getNumMeanThreshold() {
		return numMeanThreshold;
	}
	public void setNumMeanThreshold(Double numMeanThreshold) {
		this.numMeanThreshold = numMeanThreshold;
	}
	public String getNumMeanStatus() {
		return numMeanStatus;
	}
	public void setNumMeanStatus(String numMeanStatus) {
		this.numMeanStatus = numMeanStatus;
	}
	public Double getNumSDAvg() {
		return numSDAvg;
	}
	public void setNumSDAvg(Double numSDAvg) {
		this.numSDAvg = numSDAvg;
	}
	public Double getNumSDStdDev() {
		return numSDStdDev;
	}
	public void setNumSDStdDev(Double numSDStdDev) {
		this.numSDStdDev = numSDStdDev;
	}
	public Double getNumSDDeviation() {
		return numSDDeviation;
	}
	public void setNumSDDeviation(Double numSDDeviation) {
		this.numSDDeviation = numSDDeviation;
	}
	public Double getNumSDThreshold() {
		return numSDThreshold;
	}
	public void setNumSDThreshold(Double numSDThreshold) {
		this.numSDThreshold = numSDThreshold;
	}
	public String getNumSDStatus() {
		return numSDStatus;
	}
	public void setNumSDStatus(String numSDStatus) {
		this.numSDStatus = numSDStatus;
	}
	public String getOutOfNormStatStatus() {
		return outOfNormStatStatus;
	}
	public void setOutOfNormStatStatus(String outOfNormStatStatus) {
		this.outOfNormStatStatus = outOfNormStatStatus;
	}
	public String getdGroupVal() {
		return dGroupVal;
	}
	public void setdGroupVal(String dGroupVal) {
		this.dGroupVal = dGroupVal;
	}
	public String getSumOfNumStat() {
		return sumOfNumStat;
	}
	public void setSumOfNumStat(String sumOfNumStat) {
		this.sumOfNumStat = sumOfNumStat;
	}
	public String getdGroupCol() {
		return dGroupCol;
	}
	public void setdGroupCol(String dGroupCol) {
		this.dGroupCol = dGroupCol;
	}
	
}
