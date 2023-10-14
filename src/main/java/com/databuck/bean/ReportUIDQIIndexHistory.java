package com.databuck.bean;

import java.util.Map;

public class ReportUIDQIIndexHistory {

	private long domainId;
	private long projectId;
	private String indexName;
	private Map<String, Double> indexHistory;

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

	public String getIndexName() {
		return indexName;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	public Map<String, Double> getIndexHistory() {
		return indexHistory;
	}

	public void setIndexHistory(Map<String, Double> indexHistory) {
		this.indexHistory = indexHistory;
	}

}
