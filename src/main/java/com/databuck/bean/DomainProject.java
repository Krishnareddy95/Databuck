package com.databuck.bean;

public class DomainProject implements java.io.Serializable {
	
	/**
	 * 
	 */
	static final long serialVersionUID = 8999999L;
	public String domainName ;
	public String  projectName ;
	public Integer domainId ;
	public Integer idProject ;
	public DomainProject() {
		super();
		// TODO Auto-generated constructor stub
	}
	public DomainProject(String domainName, String projectName, Integer domainId, Integer idProject) {
		super();
		this.domainName = domainName;
		this.projectName = projectName;
		this.domainId = domainId;
		this.idProject = idProject;
	}
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public Integer getDomainId() {
		return domainId;
	}
	public void setDomainId(Integer domainId) {
		this.domainId = domainId;
	}
	public Integer getIdProject() {
		return idProject;
	}
	public void setIdProject(Integer idProject) {
		this.idProject = idProject;
	}
	@Override
	public String toString() {
		return "DomainProject [domainName=" + domainName + ", projectName=" + projectName + ", domainId=" + domainId
				+ ", idProject=" + idProject + "]";
	}
	
	

}
