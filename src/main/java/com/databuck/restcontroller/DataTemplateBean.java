package com.databuck.restcontroller;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;


public class DataTemplateBean {
	
	@ApiModelProperty(notes = "Data Template Name")
	private String dataTemplateName;
	@ApiModelProperty(notes = "Description")
	private String description;
	@ApiModelProperty(notes = "Id Data Schema")
	private long idDataSchema;
	@ApiModelProperty(notes = "Table Name")
	private String tableName;
	@ApiModelProperty(notes = "Data Location")
	private String dataLocation;
	@ApiModelProperty(notes = "Auto")
	private String auto;
	@ApiModelProperty(notes = "Data Format")
	private String dataFormat;
	@ApiModelProperty(notes = "Host URI")
	private String hostURI;
	@ApiModelProperty(notes = "Folder")
	private String folder;
	@ApiModelProperty(notes = "User Login")
	private String userLogin;
	@ApiModelProperty(notes = "Password")
	private String password;
	@ApiModelProperty(notes = "Bucket Name")
	private String bucketName;
	@ApiModelProperty(notes = "Key")
	private String key;
	@ApiModelProperty(notes = "Access Key")
	private String accessKey;
	@ApiModelProperty(notes = "Secret Key")
	private String secretKey;
	@ApiModelProperty(notes = "Project Id")
	private Long projectId;
	@ApiModelProperty(notes = "Domain Id")
	private Integer domainId;
	
	public Integer getDomainId() {
		return domainId;
	}

	public void setDomainId(Integer domainId) {
		this.domainId = domainId;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	
	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getDataFormat() {
		return dataFormat;
	}

	public void setDataFormat(String dataFormat) {
		this.dataFormat = dataFormat;
	}

	public String getHostURI() {
		return hostURI;
	}

	public void setHostURI(String hostURI) {
		this.hostURI = hostURI;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	private List<ListDataDefinitionBean> columns;
	public String getDataTemplateName() {
		return dataTemplateName;
	}

	public void setDataTemplateName(String dataTemplateName) {
		this.dataTemplateName = dataTemplateName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getIdDataSchema() {
		return idDataSchema;
	}

	public void setIdDataSchema(long idDataSchema) {
		this.idDataSchema = idDataSchema;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getDataLocation() {
		return dataLocation;
	}

	public void setDataLocation(String dataLocation) {
		this.dataLocation = dataLocation;
	}

	public String getAuto() {
		return auto;
	}

	public void setAuto(String auto) {
		this.auto = auto;
	}

	public List<ListDataDefinitionBean> getColumns() {
		return columns;
	}

	public void setColumns(List<ListDataDefinitionBean> columns) {
		this.columns = columns;
	}
}
