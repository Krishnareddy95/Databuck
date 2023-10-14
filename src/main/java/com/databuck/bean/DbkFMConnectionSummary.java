package com.databuck.bean;

public class DbkFMConnectionSummary {
    private Long validationId;
    private String validationName;
	private Long connectionId;
    private String connectionName;
    private String connectionType;
    private String executionDate;
    private Integer expectedFileCount;
    private Integer arrivedFileCount;
    private Integer duplicateFileCount;
    private Integer newFileCount;
    private Integer missingFileCount;
    private String ingestionStatus;
	public Long getValidationId() {
		return validationId;
	}
	public void setValidationId(Long validationId) {
		this.validationId = validationId;
	}
	public String getValidationName() {
		return validationName;
	}
	public void setValidationName(String validationName) {
		this.validationName = validationName;
	}
	public Long getConnectionId() {
		return connectionId;
	}
	public void setConnectionId(Long connectionId) {
		this.connectionId = connectionId;
	}
	public String getConnectionName() {
		return connectionName;
	}
	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}
	public String getConnectionType() {
		return connectionType;
	}
	public void setConnectionType(String connectionType) {
		this.connectionType = connectionType;
	}
	public String getExecutionDate() {
		return executionDate;
	}
	public void setExecutionDate(String executionDate) {
		this.executionDate = executionDate;
	}
	public Integer getExpectedFileCount() {
		return expectedFileCount;
	}
	public void setExpectedFileCount(Integer expectedFileCount) {
		this.expectedFileCount = expectedFileCount;
	}
	public Integer getArrivedFileCount() {
		return arrivedFileCount;
	}
	public void setArrivedFileCount(Integer arrivedFileCount) {
		this.arrivedFileCount = arrivedFileCount;
	}
	public Integer getDuplicateFileCount() {
		return duplicateFileCount;
	}
	public void setDuplicateFileCount(Integer duplicateFileCount) {
		this.duplicateFileCount = duplicateFileCount;
	}
	public Integer getNewFileCount() {
		return newFileCount;
	}
	public void setNewFileCount(Integer newFileCount) {
		this.newFileCount = newFileCount;
	}
	public Integer getMissingFileCount() {
		return missingFileCount;
	}
	public void setMissingFileCount(Integer missingFileCount) {
		this.missingFileCount = missingFileCount;
	}
	public String getIngestionStatus() {
		return ingestionStatus;
	}
	public void setIngestionStatus(String ingestionStatus) {
		this.ingestionStatus = ingestionStatus;
	}
    
}
