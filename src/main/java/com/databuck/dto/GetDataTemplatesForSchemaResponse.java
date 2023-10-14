package com.databuck.dto;

import java.util.ArrayList;

import io.swagger.annotations.ApiModelProperty;

public class GetDataTemplatesForSchemaResponse {

	@ApiModelProperty(notes = "Result")
	private ArrayList<DataTemplateforSchemaDto> result;
	
	@ApiModelProperty(notes = "Status")
	private String fail;

	public ArrayList<DataTemplateforSchemaDto> getResult() {
		return result;
	}

	public void setResult(ArrayList<DataTemplateforSchemaDto> result) {
		this.result = result;
	}

	public String getFail() {
		return fail;
	}

	public void setFail(String fail) {
		this.fail = fail;
	}
	

}
