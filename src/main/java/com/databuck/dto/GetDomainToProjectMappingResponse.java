package com.databuck.dto;

import java.util.ArrayList;

import io.swagger.annotations.ApiModelProperty;

public class GetDomainToProjectMappingResponse {

	@ApiModelProperty(notes = "Domain Id")
	private int domainId;
	@ApiModelProperty(notes = "Domain Name")
	private String domainName;
	@ApiModelProperty(notes = "Associated Projects")
	private ArrayList<ProjectDto> associatedProjects;
	
	public int getDomainId() {
		return domainId;
	}
	public void setDomainId(int domainId) {
		this.domainId = domainId;
	}
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	public ArrayList<ProjectDto> getAssociatedProjects() {
		return associatedProjects;
	}
	public void setAssociatedProjects(ArrayList<ProjectDto> associatedProjects) {
		this.associatedProjects = associatedProjects;
	}
	
	
	
}
