package com.databuck.dto;

import io.swagger.annotations.ApiModelProperty;

public class ProjectResponse {

	@ApiModelProperty(notes = "Project Id")
	private long idProject;  
	@ApiModelProperty(notes = "Project Name")
	private String projectName; 
	@ApiModelProperty(notes = "Project Description")
	private String projectDescription;
	
	
	public long getIdProject() {
		return idProject;
	}
	public void setIdProject(long idProject) {
		this.idProject = idProject;
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
