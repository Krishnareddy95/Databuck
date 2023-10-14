package com.databuck.bean;

public class FileMonitorMasterDashboard {
	private Long idApp;
	private String date;
	private Long run;
	private String validationCheckName;
	private String fileCountStatus;
	private String fileSizeStatus;
	public Long getIdApp() {
		return idApp;
	}
	public void setIdApp(Long idApp) {
		this.idApp = idApp;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public Long getRun() {
		return run;
	}
	public void setRun(Long run) {
		this.run = run;
	}
	public String getValidationCheckName() {
		return validationCheckName;
	}
	public void setValidationCheckName(String validationCheckName) {
		this.validationCheckName = validationCheckName;
	}
	public String getFileCountStatus() {
		return fileCountStatus;
	}
	public void setFileCountStatus(String fileCountStatus) {
		this.fileCountStatus = fileCountStatus;
	}
	public String getFileSizeStatus() {
		return fileSizeStatus;
	}
	public void setFileSizeStatus(String fileSizeStatus) {
		this.fileSizeStatus = fileSizeStatus;
	}
}
