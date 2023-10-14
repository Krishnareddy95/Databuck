package com.databuck.dto;

import io.swagger.annotations.ApiModelProperty;

public class CreateTemplateResponse {

	@ApiModelProperty(notes = "Data Id")
	private Long idData;
	@ApiModelProperty(notes = "Fail")
	private String fail;
	@ApiModelProperty(notes = "Success")
	private String success;
	@ApiModelProperty(notes = "Is Update")
	private String isUpdate
	;
	public Long getIdData() {
		return idData;
	}
	public void setIdData(Long idData) {
		this.idData = idData;
	}
	public String getFail() {
		return fail;
	}
	public void setFail(String fail) {
		this.fail = fail;
	}
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	public String getIsUpdate() {
		return isUpdate;
	}
	public void setIsUpdate(String isUpdate) {
		this.isUpdate = isUpdate;
	}
	
	
	
}
