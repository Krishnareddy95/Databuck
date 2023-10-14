package com.databuck.dto;

import java.util.ArrayList;

import io.swagger.annotations.ApiModelProperty;

public class GetValidationCheckByIdResponse {

	@ApiModelProperty(notes = "Result")
	private ArrayList<ValidationCheckByIdDto> result;
	@ApiModelProperty(notes = "Status")
	private String fail;

	public ArrayList<ValidationCheckByIdDto> getResult() {
		return result;
	}

	public void setResult(ArrayList<ValidationCheckByIdDto> result) {
		this.result = result;
	}

	public String getFail() {
		return fail;
	}

	public void setFail(String fail) {
		this.fail = fail;
	}
	


}
