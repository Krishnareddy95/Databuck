package com.databuck.dto;

import io.swagger.annotations.ApiModelProperty;

public class ConnectionforProjectDto {

	@ApiModelProperty(notes = "Schema Id")
	private long idDataSchema;
	@ApiModelProperty(notes = "Schema Name")
	private String schemaName;
	@ApiModelProperty(notes = "Schema Type")
	private String schemaType;
	@ApiModelProperty(notes = "Ip Address")
	private String ipAddress;
	@ApiModelProperty(notes = "Database Schema")
	private String databaseSchema;
	@ApiModelProperty(notes = "Port")
	private String port;
	@ApiModelProperty(notes = "is Active")
	private String isActive;
	
	public long getIdDataSchema() {
		return idDataSchema;
	}
	public void setIdDataSchema(long idDataSchema) {
		this.idDataSchema = idDataSchema;
	}
	public String getSchemaName() {
		return schemaName;
	}
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}
	public String getSchemaType() {
		return schemaType;
	}
	public void setSchemaType(String schemaType) {
		this.schemaType = schemaType;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getDatabaseSchema() {
		return databaseSchema;
	}
	public void setDatabaseSchema(String databaseSchema) {
		this.databaseSchema = databaseSchema;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getIsActive() {
		return isActive;
	}
	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}
}
