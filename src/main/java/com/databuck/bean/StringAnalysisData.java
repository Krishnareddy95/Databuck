package com.databuck.bean;

import java.util.List;
import java.util.Map;

public class StringAnalysisData {
	String numberOfUniqueValues;
	public String getNumberOfUniqueValues() {
		return numberOfUniqueValues;
	}
	public void setNumberOfUniqueValues(String numberOfUniqueValues) {
		this.numberOfUniqueValues = numberOfUniqueValues;
	}
	
	String percentOfUniqueValues;
	
	public String getPercentOfUniqueValues() {
		return percentOfUniqueValues;
	}
	public void setPercentOfUniqueValues(String percentOfUniqueValues) {
		this.percentOfUniqueValues = percentOfUniqueValues;
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
	public String getPercentOfNullValues() {
		return percentOfNullValues;
	}
	public void setPercentOfNullValues(String percentOfNullValues) {
		this.percentOfNullValues = percentOfNullValues;
	}
	String minLength;
	String maxLength;
	String percentOfNullValues;
	
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
	Map<String, Integer> dataMap;
	public Map<String, Integer> getDataMap() {
		return dataMap;
	}
	public void setDataMap(Map<String, Integer> dataMap) {
		this.dataMap = dataMap;
	}
}
