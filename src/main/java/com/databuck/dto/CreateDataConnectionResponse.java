package com.databuck.dto;

import io.swagger.annotations.ApiModelProperty;

public class CreateDataConnectionResponse {
	
	@ApiModelProperty(notes = "Fail")
	private String fail;
	@ApiModelProperty(notes = "Id Data Schema")
	private Long idDataSchema;
	@ApiModelProperty(notes = "Success")
	private String success;
	
	public String getFail() {
		return fail;
	}
	public void setFail(String fail) {
		this.fail = fail;
	}
	public Long getIdDataSchema() {
		return idDataSchema;
	}
	public void setIdDataSchema(Long idDataSchema) {
		this.idDataSchema = idDataSchema;
	}
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	
	
	
}
