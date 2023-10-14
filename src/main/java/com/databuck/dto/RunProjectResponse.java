package com.databuck.dto;

import io.swagger.annotations.ApiModelProperty;

public class RunProjectResponse {

	@ApiModelProperty(notes = "Project ID")
	private long projectId;
	@ApiModelProperty(notes = "Status")
	private String status;
	@ApiModelProperty(notes = "Unique ID")
	private String uniqueId;
	@ApiModelProperty(notes = "Message")
	private String message;
	
	
	public long getProjectId() {
		return projectId;
	}
	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getUniqueId() {
		return uniqueId;
	}
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
