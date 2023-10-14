package com.databuck.dto;

import io.swagger.annotations.ApiModelProperty;

public class RunProjectByNameResponse {

	@ApiModelProperty(notes = "Status")
	private String status;
	@ApiModelProperty(notes = "Message")
	private String message;
	@ApiModelProperty(notes = "Project Name")
	private String projectName;
	@ApiModelProperty(notes = "Unique Id")
	private String uniqueId;
	
	
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
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getUniqueId() {
		return uniqueId;
	}
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
	
	
}
