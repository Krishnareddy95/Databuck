package com.databuck.bean;

import java.util.List;

public class AbsaDownloadCSVRequest {
	private String tableName;
	private String idApp;
	private List<String> tableNickName;
	private String reportFromDate;
	private String reportToDate;
	private String reportRun;
	private String fileSelected;
	private String directCsvPath;
	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getIdApp() {
		return idApp;
	}
	public void setIdApp(String idApp) {
		this.idApp = idApp;
	}
	public List<String> getTableNickName() {
		return tableNickName;
	}
	public void setTableNickName(List<String> tableNickName) {
		this.tableNickName = tableNickName;
	}
	public String getReportFromDate() {
		return reportFromDate;
	}
	public void setReportFromDate(String reportFromDate) {
		this.reportFromDate = reportFromDate;
	}
	public String getReportToDate() {
		return reportToDate;
	}
	public void setReportToDate(String reportToDate) {
		this.reportToDate = reportToDate;
	}
	public String getReportRun() {
		return reportRun;
	}
	public void setReportRun(String reportRun) {
		this.reportRun = reportRun;
	}
	public String getFileSelected() {
		return fileSelected;
	}
	public void setFileSelected(String fileSelected) {
		this.fileSelected = fileSelected;
	}
	public String getDirectCsvPath() {
		return directCsvPath;
	}
	public void setDirectCsvPath(String directCsvPath) {
		this.directCsvPath = directCsvPath;
	}

}
