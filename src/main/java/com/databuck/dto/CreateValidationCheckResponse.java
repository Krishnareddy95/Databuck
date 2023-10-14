package com.databuck.dto;

import io.swagger.annotations.ApiModelProperty;

public class CreateValidationCheckResponse {
	
	@ApiModelProperty(notes = "Fail")
	private String fail;
	@ApiModelProperty(notes = "Success")
	private String success;
	@ApiModelProperty(notes = "Validation Name")
	private String validationName;
	
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
	public String getValidationName() {
		return validationName;
	}
	public void setValidationName(String validationName) {
		this.validationName = validationName;
	}
	
	
}
