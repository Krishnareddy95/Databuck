package com.databuck.dto;

import io.swagger.annotations.ApiModelProperty;

public class DataconnectionDto {

	@ApiModelProperty(notes = "Schema Data Id")
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
	@ApiModelProperty(notes = "Service Name")
	private String serviceName;
	@ApiModelProperty(notes = "Domain")
	private String domain;
	@ApiModelProperty(notes = "Principal")
	private String principal;
	@ApiModelProperty(notes = "GSS JAAS")
	private String gss_jaas;
	@ApiModelProperty(notes = "KRB5 Conf")
	private String krb5conf;
	@ApiModelProperty(notes = "Is Active")
	private String isActive;
	@ApiModelProperty(notes = "Status")
	private String fail;
	
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
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getPrincipal() {
		return principal;
	}
	public void setPrincipal(String principal) {
		this.principal = principal;
	}
	public String getGss_jaas() {
		return gss_jaas;
	}
	public void setGss_jaas(String gss_jaas) {
		this.gss_jaas = gss_jaas;
	}
	public String getKrb5conf() {
		return krb5conf;
	}
	public void setKrb5conf(String krb5conf) {
		this.krb5conf = krb5conf;
	}
	public String getIsActive() {
		return isActive;
	}
	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}
	public String getFail() {
		return fail;
	}
	public void setFail(String fail) {
		this.fail = fail;
	}
	
}
