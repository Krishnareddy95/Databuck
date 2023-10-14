package com.databuck.bean;

public class ReportUIFailedAsset {

	private String date;
	private long validationId;
	private Double validationDQI;
	private long connectionId;
	private String displayName;
	private String datasource;
	private String source;
	private String fileName;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public long getValidationId() {
		return validationId;
	}

	public void setValidationId(long validationId) {
		this.validationId = validationId;
	}

	public Double getValidationDQI() {
		return validationDQI;
	}

	public void setValidationDQI(Double validationDQI) {
		this.validationDQI = validationDQI;
	}

	public long getConnectionId() {
		return connectionId;
	}

	public void setConnectionId(long connectionId) {
		this.connectionId = connectionId;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

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

}
