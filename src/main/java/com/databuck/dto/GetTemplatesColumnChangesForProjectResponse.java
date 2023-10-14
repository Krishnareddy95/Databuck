package com.databuck.dto;

import java.util.ArrayList;

import io.swagger.annotations.ApiModelProperty;

public class GetTemplatesColumnChangesForProjectResponse {

	@ApiModelProperty(notes = "Result")
	private ArrayList<GetTemplatesColumnChangesForProjectDto> result;
	@ApiModelProperty(notes = "Status")
	private String status;
	@ApiModelProperty(notes = "Message")
	private String message;

	public ArrayList<GetTemplatesColumnChangesForProjectDto> getResult() {
		return result;
	}

	public void setResult(ArrayList<GetTemplatesColumnChangesForProjectDto> metadata) {
		this.result = metadata;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
