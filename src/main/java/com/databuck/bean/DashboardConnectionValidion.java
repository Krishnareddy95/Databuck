package com.databuck.bean;

public class DashboardConnectionValidion {

	private Long conAppId;
	private long domainId;
	private long projectId;
	private long connectionId;
	private long idApp;
	private String validationName;
	private String templateName;
	private String datasource;
	private String source;
	private String fileName;

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

	public long getConnectionId() {
		return connectionId;
	}

	public void setConnectionId(long connectionId) {
		this.connectionId = connectionId;
	}

	public long getIdApp() {
		return idApp;
	}

	public void setIdApp(long idApp) {
		this.idApp = idApp;
	}

	public String getValidationName() {
		return validationName;
	}

	public void setValidationName(String validationName) {
		this.validationName = validationName;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
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

	public Long getConAppId() {
		return conAppId;
	}

	public void setConAppId(Long conAppId) {
		this.conAppId = conAppId;
	}

}
