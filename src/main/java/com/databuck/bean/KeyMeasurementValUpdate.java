package com.databuck.bean;

import org.springframework.web.bind.annotation.RequestParam;

public class KeyMeasurementValUpdate {
	private Long idApp;
	private Long idData;
	private Long rightSourceId;
	private String expression;
	private String matchingRuleAutomatic;
	private Long leftSourceId;
	private String matchType;
	private double absoluteThresholdId;
	private double unMatchedAnomalyThreshold;
	private String groupbyid;
	private String measurementid;
	private String dateFormat;
	private String incrementalMatching;
	private String rightSliceEnd;
	private String recordCount;
	private String primaryKey;

	public Long getIdApp() {
		return idApp;
	}

	public void setIdApp(Long idApp) {
		this.idApp = idApp;
	}

	public Long getIdData() {
		return idData;
	}

	public void setIdData(Long idData) {
		this.idData = idData;
	}

	public Long getRightSourceId() {
		return rightSourceId;
	}

	public void setRightSourceId(Long rightSourceId) {
		this.rightSourceId = rightSourceId;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getMatchingRuleAutomatic() {
		return matchingRuleAutomatic;
	}

	public void setMatchingRuleAutomatic(String matchingRuleAutomatic) {
		this.matchingRuleAutomatic = matchingRuleAutomatic;
	}

	public Long getLeftSourceId() {
		return leftSourceId;
	}

	public void setLeftSourceId(Long leftSourceId) {
		this.leftSourceId = leftSourceId;
	}

	public String getMatchType() {
		return matchType;
	}

	public void setMatchType(String matchType) {
		this.matchType = matchType;
	}

	public double getAbsoluteThresholdId() {
		return absoluteThresholdId;
	}

	public void setAbsoluteThresholdId(double absoluteThresholdId) {
		this.absoluteThresholdId = absoluteThresholdId;
	}

	public double getUnMatchedAnomalyThreshold() {
		return unMatchedAnomalyThreshold;
	}

	public void setUnMatchedAnomalyThreshold(double unMatchedAnomalyThreshold) {
		this.unMatchedAnomalyThreshold = unMatchedAnomalyThreshold;
	}

	public String getGroupbyid() {
		return groupbyid;
	}

	public void setGroupbyid(String groupbyid) {
		this.groupbyid = groupbyid;
	}

	public String getMeasurementid() {
		return measurementid;
	}

	public void setMeasurementid(String measurementid) {
		this.measurementid = measurementid;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String getIncrementalMatching() {
		return incrementalMatching;
	}

	public void setIncrementalMatching(String incrementalMatching) {
		this.incrementalMatching = incrementalMatching;
	}

	public String getRightSliceEnd() {
		return rightSliceEnd;
	}

	public void setRightSliceEnd(String rightSliceEnd) {
		this.rightSliceEnd = rightSliceEnd;
	}

	public String getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(String recordCount) {
		this.recordCount = recordCount;
	}

	public String getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}

}
