package com.databuck.bean;

import java.util.Map;

public class NumericalAnalysisData {
	String min;
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
	public String getAverage() {
		return average;
	}
	public void setAverage(String average) {
		this.average = average;
	}
	public String getMedian() {
		return median;
	}
	public void setMedian(String median) {
		this.median = median;
	}
	public String getMinLength() {
		return minLength;
	}
	public void setMinLength(String minLength) {
		this.minLength = minLength;
	}
	
	public String getMaxLength() {
		return maxLength;
	}
	public void setMaxLength(String maxLength) {
		this.maxLength = maxLength;
	}
	
	String max;	
	String average;
	String median;
	String nullValues;
	String key;
	String maxLength;
	String minLength;
	
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
	String percentOfUniqueValues;
	public String getPercentOfUniqueValues() {
		return percentOfUniqueValues;
	}
	public void setPercentOfUniqueValues(String percentOfUniqueValues) {
		this.percentOfUniqueValues = percentOfUniqueValues;
	}		
	Map<Double, Integer> dataMap;
	public Map<Double, Integer> getDataMap() {
		return dataMap;
	}
	public void setDataMap(Map<Double, Integer> dataMap) {
		this.dataMap = dataMap;
	}
	String percentOfNullValues;
	public String getPercentOfNullValues() {
		return percentOfNullValues;
	}
	public void setPercentOfNullValues(String percentOfNullValues) {
		this.percentOfNullValues = percentOfNullValues;
	}	
	
}
