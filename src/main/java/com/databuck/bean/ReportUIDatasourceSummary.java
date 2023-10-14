package com.databuck.bean;

import java.util.List;

public class ReportUIDatasourceSummary {
	private String datasource;
	private String source;
	private String fileName;
	private List<ReportUIConnCheckSummary> connectionCheckList;

	public String getDatasource() {
		return datasource;
	}

	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public List<ReportUIConnCheckSummary> getConnectionCheckList() {
		return connectionCheckList;
	}

	public void setConnectionCheckList(List<ReportUIConnCheckSummary> connectionCheckList) {
		this.connectionCheckList = connectionCheckList;
	}

}
