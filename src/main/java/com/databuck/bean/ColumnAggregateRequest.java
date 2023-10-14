package com.databuck.bean;

public class ColumnAggregateRequest {

	private String idApp;
	private String microsegmentCols;
	private String type;
	private String filterColumn;
	private String filterValues;
	private String saveDataToCsvFile;
	private String fileLocation;

	public String getIdApp() {
		return idApp;
	}

	public void setIdApp(String idApp) {
		this.idApp = idApp;
	}

	public String getMicrosegmentCols() {
		return microsegmentCols;
	}

	public void setMicrosegmentCols(String microsegmentCols) {
		this.microsegmentCols = microsegmentCols;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFilterColumn() {
		return filterColumn;
	}

	public void setFilterColumn(String filterColumn) {
		this.filterColumn = filterColumn;
	}

	public String getFilterValues() {
		return filterValues;
	}

	public void setFilterValues(String filterValues) {
		this.filterValues = filterValues;
	}

	public String getSaveDataToCsvFile() {
		return saveDataToCsvFile;
	}

	public void setSaveDataToCsvFile(String saveDataToCsvFile) {
		this.saveDataToCsvFile = saveDataToCsvFile;
	}

	public String getFileLocation() {
		return fileLocation;
	}

	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}

}
