package com.databuck.dto;

import java.util.ArrayList;

import io.swagger.annotations.ApiModelProperty;

public class GetAllProjectsResponseDto {
	
	@ApiModelProperty(notes = "Result")
	private ArrayList<ProjectResponse> result;
	
	@ApiModelProperty(notes = "Status")
	private String fail;
	
	public ArrayList<ProjectResponse> getResult() {
		return result;
	}

	public void setResult(ArrayList<ProjectResponse> result) {
		this.result = result;
	}

	public String getFail() {
		return fail;
	}

	public void setFail(String fail) {
		this.fail = fail;
	}
	
}
