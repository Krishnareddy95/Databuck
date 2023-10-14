package com.databuck.dto;

import io.swagger.annotations.ApiModelProperty;

public class ProjectDto {

	@ApiModelProperty(notes = "Project Id")
	private long projectId;
	@ApiModelProperty(notes = "Project Name")
	private String projectName;
	@ApiModelProperty(notes = "Project Description")
	private String projectDescription;
	
	
	public long getProjectId() {
		return projectId;
	}
	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getProjectDescription() {
		return projectDescription;
	}
	public void setProjectDescription(String projectDescription) {
		this.projectDescription = projectDescription;
	}
	
	
}
