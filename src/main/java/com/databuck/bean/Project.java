
package com.databuck.bean;

import java.util.Date;
import java.io.Serializable;
public class Project implements java.io.Serializable {
	static final long serialVersionUID = 8999999L;
	
	private long idProject;  
	private String projectName; 
	private String projectDescription;
	private String isOwner;
	private Date createdAt;   
	private Date updatedAt;
	private long domainId;
	
	public long getDomainId() {
		return domainId;
	}
	public void setDomainId(long domainId) {
		this.domainId = domainId;
	}
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
	
	

	
	public String getIsOwner() {
		return isOwner;
	}
	public void setIsOwner(String isOwner) {
		this.isOwner = isOwner;
	}
	  
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public Date getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

}
