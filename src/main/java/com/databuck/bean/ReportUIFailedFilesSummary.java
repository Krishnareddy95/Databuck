package com.databuck.bean;

public class ReportUIFailedFilesSummary {
	private long idApp;
	private String datasource;
	private String source;
	private String fileName;
	private String overallStatus;
	private String essentialCheckStatus;
	private String advancedCheckStatus;

	public long getIdApp() {
		return idApp;
	}

	public void setIdApp(long idApp) {
		this.idApp = idApp;
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

	public String getOverallStatus() {
		return overallStatus;
	}

	public void setOverallStatus(String overallStatus) {
		this.overallStatus = overallStatus;
	}

	public String getEssentialCheckStatus() {
		return essentialCheckStatus;
	}

	public void setEssentialCheckStatus(String essentialCheckStatus) {
		this.essentialCheckStatus = essentialCheckStatus;
	}

	public String getAdvancedCheckStatus() {
		return advancedCheckStatus;
	}

	public void setAdvancedCheckStatus(String advancedCheckStatus) {
		this.advancedCheckStatus = advancedCheckStatus;
	}

}
