package com.databuck.dto;

import io.swagger.annotations.ApiModelProperty;

public class ConnectionInfoDto {

	@ApiModelProperty(notes = "Connection Id")
	private long connectionId;
	@ApiModelProperty(notes = "Connection Name")
	private String connectionName;
	@ApiModelProperty(notes = "Connection Type")
	private String connectionType;
	@ApiModelProperty(notes = "Ip Address")
	private String ipAddress;
	@ApiModelProperty(notes = "Port")
	private String port;
	@ApiModelProperty(notes = "Database Schema")
	private String databaseSchema;
	
	
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
	public String getConnectionType() {
		return connectionType;
	}
	public void setConnectionType(String connectionType) {
		this.connectionType = connectionType;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getDatabaseSchema() {
		return databaseSchema;
	}
	public void setDatabaseSchema(String databaseSchema) {
		this.databaseSchema = databaseSchema;
	}
}
