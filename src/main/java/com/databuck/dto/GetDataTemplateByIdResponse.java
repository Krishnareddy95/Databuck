package com.databuck.dto;

import java.util.ArrayList;

import io.swagger.annotations.ApiModelProperty;

public class GetDataTemplateByIdResponse {

	@ApiModelProperty(notes = "Result")
	private ArrayList<GetDataTemplateByIdDto> result;
	@ApiModelProperty(notes = "Status")
	private String fail;

	public ArrayList<GetDataTemplateByIdDto> getResult() {
		return result;
	}

	public void setResult(ArrayList<GetDataTemplateByIdDto> result) {
		this.result = result;
	}

	public String getFail() {
		return fail;
	}

	public void setFail(String fail) {
		this.fail = fail;
	}
	
}
