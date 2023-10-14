package com.databuck.dto;

import io.swagger.annotations.ApiModelProperty;

public class DashboardResultByIdResponse {

	@ApiModelProperty(notes = "Status")
	private String fail;
	@ApiModelProperty(notes = "Result")
	private DashboardResultByIdDto result;
	public String getFail() {
		return fail;
	}
	public void setFail(String fail) {
		this.fail = fail;
	}
	public DashboardResultByIdDto getResult() {
		return result;
	}
	public void setResult(DashboardResultByIdDto result) {
		this.result = result;
	}
	
	
}
