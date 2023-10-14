package com.databuck.dto;

import java.util.ArrayList;

import io.swagger.annotations.ApiModelProperty;

public class ConnectionDto {

	@ApiModelProperty(notes = "Connection Id")
	private long connectionId;
	@ApiModelProperty(notes = "Connection Name")
	private String connectionName;
	@ApiModelProperty(notes = "Connection Unique Id")
	private String connection_uniqueId;
	@ApiModelProperty(notes = "Connection Job Status")
	private String connectionJobStatus;
	@ApiModelProperty(notes = "Associated Templates")
	private ArrayList<TemplateDto> associatedTemplates;
	
	public long getConnectionId() {
		return connectionId;
	}
	public void setConnectionId(long connectionId) {
		this.connectionId = connectionId;
	}
	public String getConnectionName() {
		return connectionName;
	}
	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}
	public String getConnection_uniqueId() {
		return connection_uniqueId;
	}
	public void setConnection_uniqueId(String connection_uniqueId) {
		this.connection_uniqueId = connection_uniqueId;
	}
	public String getConnectionJobStatus() {
		return connectionJobStatus;
	}
	public void setConnectionJobStatus(String connectionJobStatus) {
		this.connectionJobStatus = connectionJobStatus;
	}
	
	public ArrayList<TemplateDto> getAssociatedTemplates() {
		return associatedTemplates;
	}
	public void setAssociatedTemplates(ArrayList<TemplateDto> associatedTemplates) {
		this.associatedTemplates = associatedTemplates;
	}
}
