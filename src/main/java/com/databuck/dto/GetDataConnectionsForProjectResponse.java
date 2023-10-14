package com.databuck.dto;

import java.util.ArrayList;

import io.swagger.annotations.ApiModelProperty;

public class GetDataConnectionsForProjectResponse {

	@ApiModelProperty(notes = "Result")
	private ArrayList<ConnectionforProjectDto> result;
	
	@ApiModelProperty(notes = "Status")
	private String fail;

	public ArrayList<ConnectionforProjectDto> getResult() {
		return result;
	}

	public void setResult(ArrayList<ConnectionforProjectDto> result) {
		this.result = result;
	}

	public String getFail() {
		return fail;
	}

	public void setFail(String fail) {
		this.fail = fail;
	}
	
}
