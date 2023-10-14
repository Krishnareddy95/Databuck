package com.databuck.dto;

import java.util.ArrayList;
import java.util.List;

import com.databuck.bean.ListDataDefinition;

import io.swagger.annotations.ApiModelProperty;

public class GetTemplatesColumnChangesForProjectListResponse {

	@ApiModelProperty(notes = "Result")
	private List<ListDataDefinition> result;
	@ApiModelProperty(notes = "Status")
	private String status;
	@ApiModelProperty(notes = "Message")
	private String message;

	public List<ListDataDefinition> getResult() {
		return result;
	}

	public void setResult(List<ListDataDefinition> metadata) {
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
