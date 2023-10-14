package com.databuck.dto;

import java.util.ArrayList;

import io.swagger.annotations.ApiModelProperty;

public class GetAdvancedRulesResponse {

	@ApiModelProperty(notes = "Result")
	private ArrayList<GetAdvancedRulesDto> result;
	@ApiModelProperty(notes = "Status")
	private String fail;

	public ArrayList<GetAdvancedRulesDto> getResult() {
		return result;
	}

	public void setResult(ArrayList<GetAdvancedRulesDto> result) {
		this.result = result;
	}

	public String getFail() {
		return fail;
	}

	public void setFail(String fail) {
		this.fail = fail;
	}

}
