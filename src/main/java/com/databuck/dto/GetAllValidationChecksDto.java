package com.databuck.dto;

import io.swagger.annotations.ApiModelProperty;

public class GetAllValidationChecksDto {

	@ApiModelProperty(notes = "App Id")
	private Long idApp;
	@ApiModelProperty(notes = "Validation Name Check")
	private String validationCheckName;
	@ApiModelProperty(notes = "Data Id")
	private Long idData;
	@ApiModelProperty(notes = "Template Name")
	private String DataTemplateName;
	@ApiModelProperty(notes = "Created At")
	private String createdAt;
	@ApiModelProperty(notes = "App Type")
	private String appType;
	@ApiModelProperty(notes = "Created By User")
	private String createdByUser;
	@ApiModelProperty(notes = "Active")
	private String active;
	
	
	public Long getIdApp() {
		return idApp;
	}
	public void setIdApp(Long idApp) {
		this.idApp = idApp;
	}
	public String getValidationCheckName() {
		return validationCheckName;
	}
	public void setValidationCheckName(String validationCheckName) {
		this.validationCheckName = validationCheckName;
	}
	public Long getIdData() {
		return idData;
	}
	public void setIdData(Long idData) {
		this.idData = idData;
	}
	public String getDataTemplateName() {
		return DataTemplateName;
	}
	public void setDataTemplateName(String dataTemplateName) {
		DataTemplateName = dataTemplateName;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public String getAppType() {
		return appType;
	}
	public void setAppType(String appType) {
		this.appType = appType;
	}
	public String getCreatedByUser() {
		return createdByUser;
	}
	public void setCreatedByUser(String createdByUser) {
		this.createdByUser = createdByUser;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	
	
}
