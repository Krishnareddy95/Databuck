package com.databuck.bean;

public class listModelGovernance {
	private long idModel;
	private long idApp;
	private String modelGovernanceType;
	private String modelIdCol;
	private String decileCol;
	private double expectedPercentage;
	private double thresholdPercentage;
	private String leftSourceSliceStart;
	private String leftSourceSliceEnd;
	private String rightSourceSliceStart;
	private String rightSourceSliceEnd;
	public String getLeftSourceSliceStart() {
		return leftSourceSliceStart;
	}
	public void setLeftSourceSliceStart(String leftSourceSliceStart) {
		this.leftSourceSliceStart = leftSourceSliceStart;
	}
	public String getLeftSourceSliceEnd() {
		return leftSourceSliceEnd;
	}
	public void setLeftSourceSliceEnd(String leftSourceSliceEnd) {
		this.leftSourceSliceEnd = leftSourceSliceEnd;
	}
	public String getRightSourceSliceStart() {
		return rightSourceSliceStart;
	}
	public void setRightSourceSliceStart(String rightSourceSliceStart) {
		this.rightSourceSliceStart = rightSourceSliceStart;
	}
	public String getRightSourceSliceEnd() {
		return rightSourceSliceEnd;
	}
	public void setRightSourceSliceEnd(String rightSourceSliceEnd) {
		this.rightSourceSliceEnd = rightSourceSliceEnd;
	}
	public String getMatchingExpression() {
		return matchingExpression;
	}
	public void setMatchingExpression(String matchingExpression) {
		this.matchingExpression = matchingExpression;
	}
	public String getMeasurementExpression() {
		return measurementExpression;
	}
	public void setMeasurementExpression(String measurementExpression) {
		this.measurementExpression = measurementExpression;
	}
	private String matchingExpression;
	private String measurementExpression;
	public long getIdModel() {
		return idModel;
	}
	public void setIdModel(long idModel) {
		this.idModel = idModel;
	}
	public long getIdApp() {
		return idApp;
	}
	public void setIdApp(long idApp) {
		this.idApp = idApp;
	}
	public String getModelGovernanceType() {
		return modelGovernanceType;
	}
	public void setModelGovernanceType(String modelGovernanceType) {
		this.modelGovernanceType = modelGovernanceType;
	}
	public String getModelIdCol() {
		return modelIdCol;
	}
	public void setModelIdCol(String modelIdCol) {
		this.modelIdCol = modelIdCol;
	}
	public String getDecileCol() {
		return decileCol;
	}
	public void setDecileCol(String decileCol) {
		this.decileCol = decileCol;
	}
	public double getExpectedPercentage() {
		return expectedPercentage;
	}
	public void setExpectedPercentage(double expectedPercentage) {
		this.expectedPercentage = expectedPercentage;
	}
	public double getThresholdPercentage() {
		return thresholdPercentage;
	}
	public void setThresholdPercentage(double thresholdPercentage) {
		this.thresholdPercentage = thresholdPercentage;
	}
}
