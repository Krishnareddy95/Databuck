package com.databuck.dto;

import java.util.ArrayList;

import io.swagger.annotations.ApiModelProperty;

public class GetAllValidationChecksResponse {

	@ApiModelProperty(notes = "Result")
	private ArrayList<GetAllValidationChecksDto> result;
	@ApiModelProperty(notes = "Status")
	private String fail;

	public ArrayList<GetAllValidationChecksDto> getResult() {
		return result;
	}

	public void setResult(ArrayList<GetAllValidationChecksDto> result) {
		this.result = result;
	}

	public String getFail() {
		return fail;
	}

	public void setFail(String fail) {
		this.fail = fail;
	}
	

}
