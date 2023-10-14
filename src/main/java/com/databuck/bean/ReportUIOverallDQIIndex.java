package com.databuck.bean;

public class ReportUIOverallDQIIndex {

	private long domainId;
	private long projectId;
	private double overallDqi;
	private double completenessDqi;
	private double accuracyDqi;
	private double consistencyDqi;
	private double validityDqi;
	private double timelinessDqi;

	public long getDomainId() {
		return domainId;
	}

	public void setDomainId(long domainId) {
		this.domainId = domainId;
	}

	public long getProjectId() {
		return projectId;
	}

	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}

	public double getOverallDqi() {
		return overallDqi;
	}

	public void setOverallDqi(double overallDqi) {
		this.overallDqi = overallDqi;
	}

	public double getCompletenessDqi() {
		return completenessDqi;
	}

	public void setCompletenessDqi(double completenessDqi) {
		this.completenessDqi = completenessDqi;
	}

	public double getAccuracyDqi() {
		return accuracyDqi;
	}

	public void setAccuracyDqi(double accuracyDqi) {
		this.accuracyDqi = accuracyDqi;
	}

	public double getConsistencyDqi() {
		return consistencyDqi;
	}

	public void setConsistencyDqi(double consistencyDqi) {
		this.consistencyDqi = consistencyDqi;
	}

	public double getValidityDqi() {
		return validityDqi;
	}

	public void setValidityDqi(double validityDqi) {
		this.validityDqi = validityDqi;
	}

	public double getTimelinessDqi() {
		return timelinessDqi;
	}

	public void setTimelinessDqi(double timelinessDqi) {
		this.timelinessDqi = timelinessDqi;
	}

}
