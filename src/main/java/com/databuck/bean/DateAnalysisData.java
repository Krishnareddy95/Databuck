package com.databuck.bean;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class DateAnalysisData {
	Date minDate;
	public Date getMinDate() {
		return minDate;
	}
	public void setMinDate(Date minDate) {
		this.minDate = minDate;
	}
	public Date getMaxDate() {
		return maxDate;
	}
	public void setMaxDate(Date maxDate) {
		this.maxDate = maxDate;
	}

	Date maxDate;
	String percentOfNullValues;
	public String getPercentOfNullValues() {
		return percentOfNullValues;
	}
	public void setPercentOfNullValues(String percentOfNullValues) {
		this.percentOfNullValues = percentOfNullValues;
	}

	String key;
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getSampleData() {
		return sampleData;
	}
	public void setSampleData(String sampleData) {
		this.sampleData = sampleData;
	}
	String sampleData;
	Map<Date, Integer> dataMap;
	public Map<Date, Integer> getDataMap() {
		return dataMap;
	}
	public void setDataMap(Map<Date, Integer> dataMap) {
		this.dataMap = dataMap;
	}
}
