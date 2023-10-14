package com.databuck.dto;

import java.util.ArrayList;

import io.swagger.annotations.ApiModelProperty;

public class CheckProjectJobStatusByIdResponse {

	@ApiModelProperty(notes = "Id")
	private String id;
	@ApiModelProperty(notes = "Project Id")
	private Long projectId;
	@ApiModelProperty(notes = "Project Job Status")
	private String projectJobStatus;
	@ApiModelProperty(notes = "Associated Connections")
	private ArrayList<ConnectionDto> associatedConnections;

	private String message;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	public String getProjectJobStatus() {
		return projectJobStatus;
	}
	public void setProjectJobStatus(String projectJobStatus) {
		this.projectJobStatus = projectJobStatus;
	}
	public ArrayList<ConnectionDto> getAssociatedConnections() {
		return associatedConnections;
	}
	public void setAssociatedConnections(ArrayList<ConnectionDto> associatedConnections) {
		this.associatedConnections = associatedConnections;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	
	
}
